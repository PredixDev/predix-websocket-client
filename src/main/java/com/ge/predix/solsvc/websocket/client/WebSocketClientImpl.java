/*
 * Copyright (c) 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.websocket.client;

import java.io.IOException;
import java.util.List;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.websocket.config.IWebSocketConfig;
import com.ge.predix.solsvc.websocket.factory.WebSocketPoolableConnectionFactory;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;

/**
 * Implements a pool of WebSocket Connections.  This class is Predix Aware, retrieving the Token and setting the Predix-Zone-id headers when creating connections.
 * Ensure you call the init() method to set up the WebSocketClient.
 * 
 * @author predix
 */
@Component
@Scope("prototype")
public class WebSocketClientImpl
        implements WebSocketClient
{
    private static Logger                      log           = LoggerFactory.getLogger(WebSocketClientImpl.class);

    private boolean                            isInitialized = false;
    
    @Autowired
    @Qualifier("defaultWebSocketConfig")
    private IWebSocketConfig webSocketConfig;

    private WebSocketPoolableConnectionFactory webSocketPoolableConnectionFactory = new WebSocketPoolableConnectionFactory();

    private final GenericObjectPool            wsPool        = new GenericObjectPool();

    
    @Override
    public void overrideWebSocketConfig(IWebSocketConfig websocketConfig){
    	this.webSocketConfig = websocketConfig;
    }
    
    
    /**
     * Method to initialize the WS pool
     */
    @SuppressWarnings(
    {
            "nls"
    })
    @Override
    public void init(RestClient restClient, List<Header> headers, WebSocketAdapter messageListener)
    {
        log.info("Init WebSocketClient with config=" + this.webSocketConfig + " for this=" + this.toString());
        this.wsPool.setMaxActive(this.webSocketConfig.getWsMaxActive());
        this.wsPool.setMaxIdle(this.webSocketConfig.getWsMaxIdle());
        this.wsPool.setMaxWait(this.webSocketConfig.getWsMaxWait());
        this.wsPool.setTestOnBorrow(true);
        this.wsPool.setTestOnReturn(true);
        this.wsPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
        
        
        this.webSocketPoolableConnectionFactory.init(restClient, this.webSocketConfig, headers, messageListener);
        this.wsPool.setFactory(this.webSocketPoolableConnectionFactory);
        for (int i = 0; i < this.wsPool.getMaxActive(); ++i)
        {
            try
            {
                this.wsPool.addObject();
            }
            catch (Exception e)
            {
                log.error("Encountered issue creating WebSocket in pool. " + e);
                throw new RuntimeException("Encountered issue creating WebSocket in pool. ", e);
            }
        }

        this.isInitialized = true;
    }
    

    @Override
    public void updateHeaders(List<Header> headers)
    {
        if ( this.webSocketPoolableConnectionFactory != null )
            this.webSocketPoolableConnectionFactory.setHeaders(headers);
    }

    /**
     * Method that gets a websocket instance
     */
    @SuppressWarnings("nls")
    private WebSocket validateAndBorrowWSfromPool()
    {
        try
        {
            return (WebSocket) this.wsPool.borrowObject();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to get WebSocket from pool", e);
        }
    }

    /**
     * Method that returns a websocket instance
     */
    @SuppressWarnings("nls")
    private void validateAndReturnWStoPool(WebSocket ws)
    {
        try
        {
            this.wsPool.returnObject(ws);
            log.debug("returned connection to pool: " + ws);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to return websocket to the pool ", e);
        }
    }

    /**
     * @param ws - WebSocket Instance
     * @param text - input type
     */

    @SuppressWarnings("nls")
    @Override
    public void postTextWSData(String text)
    {
        WebSocket ws = null;
        try
        {
            if ( !this.isInitialized )
            {
                throw new UnsupportedOperationException("WebSocket Pool has not been initialized. Call init method");
            }

            ws = validateAndBorrowWSfromPool();
            try
            {
                checkWebSocketConnection(ws);
                log.debug("WebSocketClient sending to=" + ws + " text=" + text);
                ws.sendText(text);
            }
            catch (IOException e)
            {
                throw new RuntimeException("unable to complete task ws=" + ws.getURI(), e);
            }
            finally
            {
                // return connection to pool
                validateAndReturnWStoPool(ws);
            }
        }
        catch (Throwable e)
        {
            String socket = null;
            if ( ws != null && ws.getSocket() != null ) socket = ws.getSocket().toString();
            throw new RuntimeException("unable to post: " + text + " to endpoint=" + socket, e);
        }
    }

    /**
     * @param ws - WebSocket Instance
     * @param textList - input type
     * 
     */
    @SuppressWarnings("nls")
    @Override
    public void postTextArrayWSData(List<String> textList)
    {
        WebSocket ws = null;
        try
        {
            if ( !this.isInitialized )
            {
                throw new UnsupportedOperationException("WebSocket Pool has not been initialized. Call init method");
            }
            ws = validateAndBorrowWSfromPool();
            try
            {
                checkWebSocketConnection(ws);
                for (String text : textList)
                {
                    log.debug("Sending text: " + text);
                    ws.sendText(text);
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException("unable to complete task ws=" + ws.getURI(), e);
            }
            finally
            {
                // return connection to pool
                validateAndReturnWStoPool(ws);
            }
        }
        catch (Throwable e)
        {
            String socket = null;
            if ( ws != null && ws.getSocket() != null ) socket = ws.getSocket().toString();
            throw new RuntimeException("unable to post: " + textList + " to endpoint=" + socket, e);
        }
    }

    /**
     * @param ws - WebSocket Instance
     * @param bytes - input type
     */
    @SuppressWarnings("nls")
    @Override
    public void postBinaryWSData(byte[] bytes)
    {
        WebSocket ws = null;
        try
        {
            if ( !this.isInitialized )
            {
                throw new UnsupportedOperationException("WebSocket Pool has not been initialized. Call init method");
            }
            ws = validateAndBorrowWSfromPool();
            try
            {
                checkWebSocketConnection(ws);

                log.debug("Sending binary: " + bytes);
                ws.sendBinary(bytes);
            }
            catch (IOException e)
            {
                throw new RuntimeException("unable to complete task ws=" + ws.getURI(), e);
            }
            finally
            {
                // return connection to pool
                validateAndReturnWStoPool(ws);
            }
        }
        catch (Throwable e)
        {
            String socket = null;
            if ( ws != null && ws.getSocket() != null ) socket = ws.getSocket().toString();
            throw new RuntimeException("unable to post: " + bytes + " to endpoint=" + socket, e);
        }
    }

    /**
     * @param ws
     * @throws IOException
     */
    @SuppressWarnings("nls")
    private void checkWebSocketConnection(WebSocket ws)
            throws IOException
    {
        if ( ws == null || !ws.isOpen() )
        {
            log.error("Websocket Connection is NOT OPEN");
            throw new IOException("Websocket Connection is NOT OPEN");
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "WebSocketClientImpl " + this.hashCode() + " [isInitialized=" + this.isInitialized
                + ", webSocketPoolableConnectionFactory=" + this.webSocketPoolableConnectionFactory + "]";
    }

}
