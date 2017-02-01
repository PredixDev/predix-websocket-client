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

import org.apache.http.Header;

import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.websocket.config.IWebSocketConfig;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

/**
 * 
 * @author predix
 */
public interface WebSocketClient
{

    /**
     * @param restClient - 
     * @param config - use implemented IWebSocketConfig
     * @param headers - headers set by the user when connections are opened and placed in the pool.  Be careful about Header values that can expire, see UpdateHeaders()
     * @param messageListener - the listener that the user of this webSocketClient passes in
     */
    public void init(RestClient restClient, List<Header> headers, WebSocketAdapter messageListener);

    /**
     * @param ws - WebSocket Instance
     * @param text - input type
     * @param listener -
     * @throws IOException -
     * @throws WebSocketException -
     */
    public abstract void postTextWSData(String text)
            throws IOException, WebSocketException;

    /**
     * @param ws - WebSocket Instance
     * @param textList - input type
     * @param listener -
     * @throws IOException -
     * @throws WebSocketException -
     */
    public abstract void postTextArrayWSData(List<String> textList)
            throws IOException, WebSocketException;

    /**
     * @param ws - WebSocket Instance
     * @param bytes - input type
     * @param listener -
     * @throws IOException -
     * @throws WebSocketException -
     */
    public abstract void postBinaryWSData(byte[] bytes)
            throws IOException, WebSocketException;

    /**
     * Update the Headers set when opening a new connection and adding it to the pool.  Useful when Header value can expire.
     * 
     * @param headers -
     */
    void updateHeaders(List<Header> headers);


	/**
	 * @param oauthConfig -
	 * @param websocketConfig -
	 */
    void overrideWebSocketConfig(IWebSocketConfig websocketConfig);

}
