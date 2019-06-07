/*
 * Copyright (c) 2002-2019, Mairie de Paris
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

import java.util.HashMap;
import java.util.Map;

import fr.paris.lutece.plugins.broadcastproxy.business.IBroadcastProvider;

public class MockProvider implements IBroadcastProvider
{

    private static final String DEFAULT_USER_SUBSCRIPTIONS_JSON = "[{id:\"1\",name:\"sub1\"},{id:\"2\",name:\"sub2\"}]";
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
    public String getUserSubscriptions( String userId, String typeSubsciption )
    {
        return DEFAULT_USER_SUBSCRIPTIONS_JSON;
    }

    @Override
    public boolean updateUserSubscribtions( String userId, Map<String, String> listSubscriptions, String typeSubsciption )
    {
        return true;
    }

    @Override
    public boolean subscribe( String userId, String subscriptionId, String typeSubsciption )
    {
        return true;
    }

    @Override
    public boolean unsubscribe( String userId, String subscriptionId, String typeSubsciption )
    {
        return true;
    }

    @Override
    public Map<String, String> getUserSubscriptionsAsMap( String userId, String typeSubsciption )
    {
        return DEFAULT_USER_SUBSCRIPTIONS_MAP;
    }

}
