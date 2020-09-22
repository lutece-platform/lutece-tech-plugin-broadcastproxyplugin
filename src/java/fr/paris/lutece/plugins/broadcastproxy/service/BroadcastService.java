/*
 * Copyright (c) 2002-2020, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.broadcastproxy.service;

import fr.paris.lutece.plugins.broadcastproxy.business.Feed;
import fr.paris.lutece.plugins.broadcastproxy.business.IBroadcastProvider;
import fr.paris.lutece.plugins.broadcastproxy.business.Subscription;
import fr.paris.lutece.portal.service.init.LuteceInitException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.util.List;
import java.util.Map;

public class BroadcastService
{

    private static final String BEAN_BROADCAST_PROVIDER = "broadcastproxy.provider";

    private static IBroadcastProvider _broadcastProvider;
    private static BroadcastService _instance;

    /**
     * Private constructor
     */
    private BroadcastService( )
    {
    }

    /**
     * get provider name
     * 
     * @return the name of the provider
     */
    public String getName( )
    {
        return _broadcastProvider.getName( );
    }

    /**
     * Get the unique instance of the Security Service
     *
     * @return The instance
     */
    public static synchronized BroadcastService getInstance( )
    {
        if ( _instance == null )
        {
            _instance = new BroadcastService( );
            _instance.init( );
        }

        return _instance;
    }

    /**
     * Initialize service
     *
     * @throws LuteceInitException
     *             If error while initialization
     */
    private synchronized void init( )
    {
        if ( _broadcastProvider == null )
        {
            _broadcastProvider = (IBroadcastProvider) SpringContextService.getBean( BEAN_BROADCAST_PROVIDER );
            AppLogService.info( "BroadcastProvider loaded : " + _broadcastProvider.getName( ) );
        }
    }

    /**
     * get user subscriptions returns the user subscription list as a JSON string like :
     * 
     * {"subscriptions":[{"name":"EXAMPLE_ONE","active":"0"},{"name":"EXAMPLE_TWO","active":"1","data":["data1","date2","data3"]}]}
     * 
     * @param userId
     * @param typeSubscription
     * @return a JSON String
     */
    public String getUserSubscriptionsAsJson( String userId ) throws Exception
    {
        return _broadcastProvider.getUserSubscriptionsAsJson( userId );
    }

    /**
     * returns the user subscriptions list as list of Subscriptions by groups (themes) in HashMap with Group name as key
     * 
     * @param userId
     * @param typeSubscription
     * @return a JSON String
     */
    public Map<String, Map<String, List<Subscription>>> getUserSubscriptionsAsList( String userId ) throws Exception
    {
        return _broadcastProvider.getUserSubscriptionsAsList( userId );
    }

    /**
     * update user subscriptions to the specified subscription list
     * 
     * @param subscriptionsList
     * @return true if success
     * @throws Exception
     */
    public boolean updateSubscribtions( String userId, List<Subscription> subscriptionsList ) throws Exception
    {
        return _broadcastProvider.updateSubscribtions( userId, subscriptionsList );
    }

    /**
     * update user subscriptions to the specified subscription list
     * 
     * @param subscriptionsList
     * @return true if success
     * @throws Exception
     */
    public boolean updateSubscribtions( String userId, String jsonSubscriptions ) throws Exception
    {
        return _broadcastProvider.updateSubscribtions( userId, jsonSubscriptions );
    }

    /**
     * Subscribe
     * 
     * @param userId
     * @param subscriptionId
     * @param typeSubscription
     * @return true if success
     * @throws Exception
     */
    public boolean subscribe( String userId, String subscriptionId, String typeSubscription ) throws Exception
    {
        return _broadcastProvider.subscribe( userId, subscriptionId, typeSubscription );
    }

    /**
     * unsubscribe
     * 
     * @param userId
     * @param subscriptionId
     * @param typeSubscription
     * @return true if success
     * @throws Exception
     */
    public boolean unsubscribe( String userId, String subscriptionId, String typeSubscription ) throws Exception
    {
        return _broadcastProvider.unsubscribe( userId, subscriptionId, typeSubscription );
    }

    /**
     * updates a Subscription bean
     * 
     * @param sub
     * @return the map
     * @throws java.lang.Exception
     */
    public boolean update( Subscription sub ) throws Exception
    {
        return _broadcastProvider.update( sub );
    }

    public List<String> getSubscriptionViewOrder( )
    {
        return _broadcastProvider.getSubscriptionViewOrder( );
    }

    /**
     * get the list of available feeds
     * 
     * @return the list
     */
    public List<Feed> getFeeds( )
    {
        return _broadcastProvider.getFeeds( );
    }

}
