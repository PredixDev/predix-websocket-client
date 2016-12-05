package com.ge.predix.solsvc.websocket.factory;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.websocket.config.IWebSocketConfig;
import com.neovisionaries.ws.client.ProxySettings;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;

/**
 * 
 * @author 212438846 -
 */
public class WebSocketPoolableConnectionFactory extends BasePoolableObjectFactory
{
    private static Logger    log                 = LoggerFactory.getLogger(WebSocketPoolableConnectionFactory.class);

    /**
     * not autowired, passed in to init to ensure correct set of properties are used
     */
   private RestClient       restClient;

    /**
     * not autowired, passed in to init to ensure correct set of properties are used
     */
    private IWebSocketConfig config;

    /**
     * 
     */
    // callback to know if connection was successful
    private WebSocketAdapter onConnectedListener = new WebSocketAdapter()
                                                 {
                                                     private Logger logger = LoggerFactory
                                                             .getLogger(WebSocketPoolableConnectionFactory.class);

                                                     @SuppressWarnings("nls")
                                                     @Override
                                                     public void onConnected(WebSocket websocket,
                                                             Map<String, List<String>> wsHeaders)
                                                     {
                                                         this.logger.debug("CONNECTED...." + wsHeaders.toString());
                                                     }
                                                 };

    private WebSocketAdapter userListener;

    private List<Header>     userHeaders;

    /**
     * @param aRestClient - 
     * @param wsConfig -
     * @param headers headers the user needs to add, these should not hold values that expire or else a this init() method should be called again.
     * @param aListener - the listener the user of this pool passes in
     */
    @SuppressWarnings("nls")
    public void init(RestClient aRestClient, IWebSocketConfig wsConfig, List<Header> headers, WebSocketAdapter aListener)
    {
        this.restClient = aRestClient;
        this.config = wsConfig;
        this.userHeaders = headers;
        this.userListener = aListener;
        log.info("Factory Initialized with:" + this.toString());
    }

    /**
     * This is a callback from the pool manager factory.  When the pool gets low or has bad connections or upon initialization.
     * 
     */
    @SuppressWarnings("nls")
    @Override
    public Object makeObject()
    {
        try
        {
            WebSocketFactory factory = new WebSocketFactory();
            detectAndSetProxy(factory);
            WebSocket ws = factory.createSocket(this.config.getWsUri());
            setHeaders(ws);
            ws.connect();
            ws.addListener(this.onConnectedListener);
            ws.addListener(this.userListener);
            return ws;
        }
        catch (Throwable e)
        {
            throw new RuntimeException("unable to make websocket connection config=" + this.config,e);
        }
    }

    /**
     * Note that this is called when the pool is initialized as well as when connections expire and the pool is replenished
     * 
     * @param headers
     */
    @SuppressWarnings("nls")
    private void setHeaders(WebSocket ws)
    {
        log.debug("Creating WebSocketConnectionHeaders for zone=" + this.config.getZoneId() + " issuerId=" + this.restClient.getRestConfig().getOauthIssuerId() + " config=" + this.config.toString());
        // headers required for authentication and for predix service
        List<Header> headers = this.restClient.getSecureTokenForClientId();
        headers.add(new BasicHeader("Predix-Zone-Id", this.config.getZoneId()));
        // Origin header required as it is not being set by the websocket
        headers.add(new BasicHeader("Origin", "http://localhost"));
        if ( this.userHeaders != null )
            headers.addAll(this.userHeaders);
        for (Header header : headers)
        {
            ws.addHeader(header.getName(), header.getValue());
        }
    }

    @Override
    public boolean validateObject(Object obj)
    {
        if ( obj instanceof WebSocket )
        {
            if ( ((WebSocket) obj).isOpen() )
            {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("nls")
    private void detectAndSetProxy(WebSocketFactory factory)
    {
        // setting proxies for websocket
        if ( !StringUtils.isEmpty(this.config.getOauthProxyHost()) && !StringUtils.isEmpty(this.config.getOauthProxyPort()) )
        {
            ProxySettings settings = factory.getProxySettings();
            settings.setServer("http://" + this.config.getOauthProxyHost() + ":" + this.config.getOauthProxyPort());
        }
    }

    /**
     * @param headers -
     */
    public void setHeaders(List<Header> headers)
    {
        this.userHeaders = headers;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "WebSocketPoolableConnectionFactory:" + this.hashCode() + "[restClient=" + this.restClient
                + ", userHeaders=" + this.userHeaders + ", config=" + this.config + ", userListener=" + this.userListener + "]";
    }

}
