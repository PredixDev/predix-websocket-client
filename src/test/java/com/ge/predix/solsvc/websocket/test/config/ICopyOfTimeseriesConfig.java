/*
 * Copyright (c) 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.websocket.test.config;

import com.ge.predix.solsvc.websocket.config.IWebSocketConfig;

/**
 * Copied here so we don't have a circular reference to timeseries project
 * 
 * @author 212438846
 */
public interface ICopyOfTimeseriesConfig extends IWebSocketConfig {


	/**
	 * @return -
	 */
	public abstract String getQueryUrl();



}