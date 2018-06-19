/*
 * Copyright (c) 2018 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.websocket.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.restclient.config.RestConfigFactory;

/**
 * If you are not using Environment variables (or properties), you can
 * programatically create a Config using this Factory class. This will use the
 * DefaultConfig as a baseline. This is helpful when your microservice is
 * handling multiple user or clients or endpoints.
 * 
 * @author predix -
 */
@Component
public class WebSocketConfigFactory implements ApplicationContextAware {

	/**
	 * 
	 */
	@Autowired
	protected static ApplicationContext context;

	/**
	 * Use this to generate to speak to a wsUri using a predix zoneId
	 * 
	 * @param wsUri
	 *            -
	 * @param zoneId
	 *            -
	 * 
	 * @return -
	 */
	@SuppressWarnings("nls")
	static public IWebSocketConfig zoneEndpoint(String wsUri, String zoneId) {
		DefaultWebSocketConfigForTimeseries defaultConfig = (DefaultWebSocketConfigForTimeseries) context
				.getBean("defaultWebSocketConfig");
		defaultConfig.setWsUri(wsUri);
		defaultConfig.setZoneId(zoneId);
		return defaultConfig;
	}

	/**
	 * Use this to generate to speak to a wsUri using a predix zoneId
	 * 
	 * @param wsUri
	 *            -the websocket endpoint url
	 * @param zoneId
	 *            -the predix zone-id passed in the header
	 * @param oauthIssuerId
	 *            - Issuer ID URL of the UAA
	 * @param oauthClientIdColonSecret
	 *            - clientId:secret separated by a colon
	 * @param oauthClientIdEncode
	 *            - whether the SDK should base64 encode the clientId:secret,
	 *            default is true. true means it is clear-text. false is what
	 *            you should do in order to not display the secret in
	 *            clear-text.
	 * 
	 * @return - A config object
	 */
	@SuppressWarnings("nls")
	static public IWebSocketConfig zoneEndpointClientCredentials(String wsUri, String zoneId, String oauthIssuerId,
			String oauthClientIdColonSecret, boolean oauthClientIdEncode) {
		DefaultWebSocketConfigForTimeseries defaultConfig = (DefaultWebSocketConfigForTimeseries) context
				.getBean("defaultWebSocketConfig");
		defaultConfig.setWsUri(wsUri);
		defaultConfig.setZoneId(zoneId);
		RestConfigFactory.setClientCredentialProps(oauthIssuerId, oauthClientIdColonSecret, oauthClientIdEncode, defaultConfig);
		return defaultConfig;
	}

	/**
	 * @param wsUri
	 *            -the websocket endpoint URL
	 * @param zoneId
	 *            -the predix zone-id passed in the header
	 * @param oauthIssuerId
	 *            - Issuer ID URL of the UAA
	 * @param oauthClientIdColonSecret
	 *            - clientId:secret separated by a colon
	 * @param oauthClientIdEncode
	 *            - whether the clientId:secret is base 64 encoded when passed
	 *            in, default is true
	 * @param oauthUserName
	 *            - the username to generate a token for. As set up in UAA or
	 *            the UAA Federated IDP.
	 * @param oauthUserPassword
	 *            - the password of the user
	 * @param oauthEncodeUserPassword
	 *            - whether to base64 encode the password, default is true
	 * @return -
	 */
	@SuppressWarnings("nls")
	static public IWebSocketConfig zoneEndpointPasswordGrant(String wsUri, String zoneId, String oauthIssuerId,
			String oauthClientIdColonSecret, boolean oauthClientIdEncode, String oauthUserName, String oauthUserPassword,
			boolean oauthEncodeUserPassword) {
		DefaultWebSocketConfigForTimeseries defaultConfig = (DefaultWebSocketConfigForTimeseries) context
				.getBean("defaultWebSocketConfig");
		defaultConfig.setWsUri(wsUri);
		defaultConfig.setZoneId(zoneId);
		RestConfigFactory.setPasswordProps(oauthIssuerId, oauthClientIdColonSecret, oauthClientIdEncode, oauthUserName,
				oauthUserPassword, oauthEncodeUserPassword, defaultConfig);
		return defaultConfig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationContextAware#setApplicationContext
	 * (org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		WebSocketConfigFactory.context = context;

	}

}
