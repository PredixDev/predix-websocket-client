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

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.solsvc.websocket.config.IWebSocketConfig;
import com.ge.predix.solsvc.websocket.config.WebSocketConfigFactory;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

/**
 * 
 * @author 212438846
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("local")
@ContextConfiguration(locations = { "classpath*:META-INF/spring/predix-websocket-client-scan-context.xml",
		"classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml" })
public class WebSocketRuntimeClientIT {

	/**
	 * 
	 */
	// not declared as private--emulated by a synthetic accessor method warning
	static Logger log = LoggerFactory.getLogger(WebSocketClientIT.class);

	@Autowired
	private WebSocketClient runtimeClient;
	
	//we say 'runtimeConfig' but to keep our test cases tidy we read from the properties
	@Value("${predix.oauth.issuerId.url:#{null}}")
	private String oauthIssuerId;
	@Value("${predix.oauth.clientId:#{null}}")
	private String oauthClientId;
    @Value("${predix.timeseries.websocket.uri}")
    private String             wsUri;
    @Value("${predix.timeseries.zoneid:#{null}}")
    private String             zoneId;

	/**
	 * 
	 */
	private WebSocketAdapter messageListener = new WebSocketAdapter() {
		@SuppressWarnings("nls")
		@Override
		public void onTextMessage(WebSocket wsocket, String message) {
			log.debug("RECEIVED...." + message); // $$
		}

		@SuppressWarnings("nls")
		@Override
		public void onBinaryMessage(WebSocket wsocket, byte[] binary) {
			String str = new String(binary, StandardCharsets.UTF_8);
			log.debug("RECEIVED...." + str); // $$
		}
	};

	/**
	 * 
	 */
	@Before
	public void setup() {
		//
	}

	
	/**
	 *  -
	 */
	@SuppressWarnings("nls")
	@Test
	public void postDataTestUsingRuntimeConfig() {

		try {
			boolean encodeClientId = true;
			IWebSocketConfig testConfig = WebSocketConfigFactory.zoneEndpointClientCredentials(this.wsUri, this.zoneId, this.oauthIssuerId, this.oauthClientId, encodeClientId);

			List<Header> nullHeaders = null;
			this.runtimeClient.init(nullHeaders, this.messageListener);
			this.runtimeClient.overrideWebSocketConfig(testConfig);
			
			String testMessage1 = "{\"messageId\": \"1453338376222\",\"body\": [{\"name\": \"Compressor-2015:CompressionRatio\",\"datapoints\": [[1453338376222,10,3],[1453338376222,10,1]],\"attributes\": {\"host\": \"server1\",\"customer\": \"Acme1\"}}]}"; // $$
			this.runtimeClient.postTextWSData(testMessage1);

			// post data as text List
			this.runtimeClient.postTextArrayWSData(generateTextArray());

			// port data as binary array
			byte[] byteArray = generateBinaryArray();
			if (byteArray != null && byteArray.length != 0) {
				this.runtimeClient.postBinaryWSData(byteArray);
			}

		} catch (IOException e) {
			fail("Failed to connect to WS due to IOException." + e.getMessage()); // $$
		} catch (WebSocketException e) {
			fail("Failed to connect to WS due to WebSocketException." + e.getMessage()); // $$
		}

		try {// wait added for time delay in callback from websocket endpoint
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			fail("Failed due to thread interruption." + e.getMessage()); // $$
		}
	}

	

	@SuppressWarnings("nls")
	private List<String> generateTextArray() {
		String testMessage1 = "{\"messageId\": \"1453338376210\",\"body\": [{\"name\": \"Compressor-2010:CompressionRatio\",\"datapoints\": [[1453338376223,10,3],[1453338376223,10,1]],\"attributes\": {\"host\": \"server2\",\"customer\": \"Acme2\"}}]}"; // $$
		String testMessage2 = "{\"messageId\": \"1453338376211\",\"body\": [{\"name\": \"Compressor-2011:CompressionRatio\",\"datapoints\": [[1453338376223,10,3],[1453338376223,10,1]],\"attributes\": {\"host\": \"server2\",\"customer\": \"Acme2\"}}]}"; // $$
		String testMessage3 = "{\"messageId\": \"1453338376212\",\"body\": [{\"name\": \"Compressor-2012:CompressionRatio\",\"datapoints\": [[1453338376223,10,3],[1453338376223,10,1]],\"attributes\": {\"host\": \"server2\",\"customer\": \"Acme2\"}}]}"; // $$
		String testMessage4 = "{\"messageId\": \"1453338376213\",\"body\": [{\"name\": \"Compressor-2013:CompressionRatio\",\"datapoints\": [[1453338376223,10,3],[1453338376223,10,1]],\"attributes\": {\"host\": \"server2\",\"customer\": \"Acme2\"}}]}"; // $$
		String testMessage5 = "{\"messageId\": \"1453338376214\",\"body\": [{\"name\": \"Compressor-2014:CompressionRatio\",\"datapoints\": [[1453338376223,10,3],[1453338376223,10,1]],\"attributes\": {\"host\": \"server2\",\"customer\": \"Acme2\"}}]}"; // $$

		List<String> textList = new ArrayList<String>();
		textList.add(testMessage1);
		textList.add(testMessage2);
		textList.add(testMessage3);
		textList.add(testMessage4);
		textList.add(testMessage5);

		return textList;
	}

	@SuppressWarnings("nls")
	private byte[] generateBinaryArray() {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("testData/LargeTimeSeriesData.txt").getFile()); // $$
		ByteArrayOutputStream ous = null;
		InputStream ios = null;
		try {
			byte[] buffer = new byte[4096];
			ous = new ByteArrayOutputStream();
			try {
				ios = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
			int read = 0;
			try {
				while ((read = ios.read(buffer)) != -1) {
					ous.write(buffer, 0, read);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} finally {
			try {
				if (ous != null)
					ous.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			try {
				if (ios != null)
					ios.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return (ous != null) ? ous.toByteArray() : null;
	}

}
