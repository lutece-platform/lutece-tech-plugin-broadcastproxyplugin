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
package fr.paris.lutece.plugins.broadcastproxy.business.providers.mock;

import fr.paris.lutece.plugins.broadcastproxy.business.Feed;
import fr.paris.lutece.plugins.broadcastproxy.business.IBroadcastProvider;
import fr.paris.lutece.plugins.broadcastproxy.business.Subscription;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

public class MockProvider implements IBroadcastProvider
{

    private static final String DEFAULT_USER_SUBSCRIPTIONS_JSON = "{\"subscriptions\":[{\"name\":\"EXAMPLE_ONE\",\"active\":\"0\"},{\"name\":\"EXAMPLE_TWO\",\"active\":\"1\",\"data\":[\"data1\",\"date2\"]}]}";
    private static final Map<String, String> DEFAULT_USER_SUBSCRIPTIONS_MAP = createDefaultMap( );

    /**
     * initialize default map
     * 
     * @return the map
     */
    private static Map<String, String> createDefaultMap( )
    {
        Map<String, String> myMap = new HashMap<>( );
        myMap.put( "1", "sub1" );
        myMap.put( "2", "sub2" );
        return myMap;
    }

    @Override
    public String getName( )
    {
        return "Mock";
    }

    @Override
    public String getUserSubscriptionsAsJson( String userId )
    {
        return DEFAULT_USER_SUBSCRIPTIONS_JSON;
    }

    public boolean updateSubscribtions( List<Subscription> subscriptionsList )
    {
        return true;
    }

    @Override
    public boolean subscribe( String userId, String subscriptionId, String typeSubscription )
    {
        return true;
    }

    @Override
    public boolean unsubscribe( String userId, String subscriptionId, String typeSubscription )
    {
        return true;
    }

    @Override
    public Map<String, Map<String, List<Subscription>>> getUserSubscriptionsAsList( String userId ) throws Exception
    {
        Map<String, Map<String, List<Subscription>>> map = new HashMap<String, Map<String, List<Subscription>>>( );

        /*
         * Subscription sub1 = new Subscription( ); sub1.setId( "EXAMPLE_ONE" ); sub1.setUserId( userId ); sub1.setActive( false ); sub1.setType(
         * typeSubscription ); Map<String, String> mapDatas = new HashMap<>( ); mapDatas.put( "T1", "theme1" ); mapDatas.put( "T2", "theme2" ); sub1.setData(
         * mapDatas );
         * 
         * Subscription sub2 = new Subscription( ); sub2.setId( "EXAMPLE_TWO" ); sub2.setUserId( userId ); sub2.setActive( true ); sub2.setType(
         * typeSubscription ); List<String> data = new ArrayList<>( );
         * 
         * List<Subscription> list = new ArrayList<>( ); list.add( sub1 ); list.add( sub2 );
         * 
         * //map.put(null, list);
         */
        return map;

    }

    @Override
    public boolean update( Subscription subscription ) throws Exception
    {
        return true;
    }

    public List<Feed> getFeeds( )
    {
        List<Feed> list = new ArrayList<>( );

        Feed testFeed = new Feed( );
        testFeed.setActive( true );
        testFeed.setName( "EXAMPLE_ONE" );
        testFeed.setId( "ONE" );
        testFeed.setDescription( "Test" );
        testFeed.setType( "TEST" );
        Map<String, String> mapDatas = new HashMap<>( );
        mapDatas.put( "T1", "theme1" );
        mapDatas.put( "T2", "theme2" );
        testFeed.setData( mapDatas );

        list.add( testFeed );

        Feed testFeed2 = new Feed( );
        testFeed2.setActive( false );
        testFeed2.setName( "EXAMPLE_TWO" );
        testFeed2.setId( "TWO" );
        testFeed2.setDescription( "Test" );
        testFeed2.setType( "TEST" );

        list.add( testFeed2 );

        return list;
    }

    @Override
    public boolean updateSubscribtions( String userId, String jsonSubscriptions ) throws Exception
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean updateSubscribtions( String userId, List<Subscription> subscriptionsList ) throws Exception
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<String> getSubscriptionViewOrder( )
    {
        // TODO Auto-generated method stub
        return null;
    }

}
