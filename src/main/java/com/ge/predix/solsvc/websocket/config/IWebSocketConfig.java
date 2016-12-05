package com.ge.predix.solsvc.websocket.config;

import com.ge.predix.solsvc.restclient.config.IOauthRestConfig;

/**
 * 
 * @author 212438846
 */
public interface IWebSocketConfig extends IOauthRestConfig
{
    
    /**
     * The websocket endpoint URI
     * 
     * @return -
     */
    public abstract String getWsUri();

    /**
     * The Predix-Zone-Id HTTP Header value when the websocket endpoint requires it.  This is usually the instanceId of the service
     * 
     * @return -
     */
    public abstract String getZoneId();

    /**
     * Sets the cap on the number of "idle" instances in the pool. Use a negative value to indicate an unlimited number of idle instances.
     * 
     * @return -
     */
    public abstract int getWsMaxIdle();

    /**
     * The cap on the total number of active instances from the pool. Use a negative value for no limit.
     * 
     * @return -
     */
    public abstract int getWsMaxActive();

    /**
     * Sets the maximum amount of time (in milliseconds) the borrowObject method should block before throwing an exception when the pool is exhausted and the
     * "when exhausted" action is WHEN_EXHAUSTED_BLOCK. When less than or equal to 0, the borrowObject method may block indefinitely.
     * 
     * @return -
     */
    public abstract int getWsMaxWait();

    /**
     * @return -
     */
    public abstract String getZoneIdHeader();


}
