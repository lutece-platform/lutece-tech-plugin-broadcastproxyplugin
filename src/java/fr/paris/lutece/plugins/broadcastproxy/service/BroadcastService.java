/*
 * Copyright (c) 2002-2023, City of Paris
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

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import fr.paris.lutece.plugins.broadcastproxy.business.Feed;
import fr.paris.lutece.plugins.broadcastproxy.business.IBroadcastProvider;
import fr.paris.lutece.plugins.broadcastproxy.business.Subscription;
import fr.paris.lutece.portal.service.init.LuteceInitException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

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
     * @param strAccountId
     * @return true if success
     * @throws Exception
     */
    public boolean updateSubscribtions( String userId, String jsonSubscriptions, String strAccountId ) throws Exception
    {
        return _broadcastProvider.updateSubscribtions( userId, jsonSubscriptions, strAccountId );
    }
    
    /**
     * update user subscriptions to the specified subscription list
     * 
     * @param subscriptionsList
     * @param strAccountId
     * @return true if success
     * @throws Exception
     */
    public boolean updateArrondissementSubscribtions( String userId, String jsonSubscriptions, String strAccountId ) throws Exception
    {
        return _broadcastProvider.updateArrondissementSubscribtions( userId, jsonSubscriptions, strAccountId );
    }

    /**
     * updates a Subscription bean
     * 
     * @param sub
     * @param strAccountId
     * @return the map
     * @throws java.lang.Exception
     */
    public boolean update( Subscription sub, String strAccountId ) throws Exception
    {
        return _broadcastProvider.update( sub, strAccountId );
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

    /**
     * get all subscription by group
     * @param typeSubscription
     * @param strAccountId
     * @return
     */
    public String getAllSubscriptionByGroup( String typeSubscription, String strAccountId )
    {
        return _broadcastProvider.getAllSubscriptionByGroup( typeSubscription, strAccountId );
    }
    
    /**
     * 
     * @param userId
     * @param strAccountId
     * @return
     * @throws Exception 
     */
    public List<JSONObject> getUserSubscriptionIds( String strUserId, String strAccountId )
    {
        return _broadcastProvider.getUserSubscriptionIds( strUserId, strAccountId );
    }
}
