/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.websocket.test.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.websocket.config.DefaultWebSocketConfigForTimeseries;

/**
 * Properties needed to make rest calls to the Time Series instance
 * 
 * @author 212421693
 */
@Component("copyOfDefaultTimeseriesConfig")
public class CopyOfDefaultTimeseriesConfig extends DefaultWebSocketConfigForTimeseries
        implements EnvironmentAware, ICopyOfTimeseriesConfig
{

    @Value("${predix.timeseries.queryUrl}")
    private String             queryUrl;





    /*
     * (non-Javadoc)
     * @see org.springframework.context.EnvironmentAware#setEnvironment(org.
     * springframework.core.env.Environment)
     */
    @SuppressWarnings("nls")
    @Override
    public void setEnvironment(Environment env)
    {
        super.setEnvironment(env);
        String vcapPropertyName = null;
        String tsName = env.getProperty(TIME_SERIES_VCAPS_NAME); // this is set
                                                                 // on the
                                                                 // manifest
                                                                 // of the
                                                                 // application

        vcapPropertyName = null;
        vcapPropertyName = "vcap.services." + tsName + ".credentials.query.uri";
        if ( !StringUtils.isEmpty(env.getProperty(vcapPropertyName)) )
        {
            this.queryUrl = env.getProperty(vcapPropertyName);

        }

        

    }

    /*
     * (non-Javadoc)
     * @see com.ge.predix.solsvc.timeseries.bootstrap.config.ITimeseriesConfig#getQueryUrl()
     */
    @Override
    public String getQueryUrl()
    {
        return this.queryUrl;
    }

    /**
     * @param queryUrl -
     */
    public void setQueryUrl(String queryUrl)
    {
        this.queryUrl = queryUrl;
    }



}
