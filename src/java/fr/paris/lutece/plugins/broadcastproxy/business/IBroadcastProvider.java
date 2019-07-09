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
package fr.paris.lutece.plugins.broadcastproxy.business;

import java.util.List;

public interface IBroadcastProvider
{

    /**
     * get provider name
     * 
     * @return
     */
    String getName( );

    /**
     * subscribe
     * 
     * @param userId
     * @param subscriptionId
     * @param typeSubscription
     * @return true if success
     * @throws java.lang.Exception
     */
    boolean subscribe( String userId, String subscriptionId, String typeSubscription ) throws Exception;

    /**
     * unsubscribe
     * 
     * @param userId
     * @param subscriptionId
     * @param typeSubscription
     * @return true if success
     * @throws java.lang.Exception
     */
    boolean unsubscribe( String userId, String subscriptionId, String typeSubscription ) throws Exception;

    /**
     * returns the user subscribtions list as list of Subscriptions beans
     * 
     * @param userId
     * @param typeSubscription
     * @return the list
     * @throws java.lang.Exception
     */
    List<Subscription> getUserSubscriptionsAsList( String userId, String typeSubscription ) throws Exception;

    /**
     * returns the user subscribtions list as a JSON string like :
     * 
     * {"subscriptions":[{"name":"EXAMPLE_ONE","active":"0"},{"name":"EXAMPLE_TWO","active":"1","data":["data1","date2","data3"]}]}
     * 
     * @param userId
     * @param typeSubscription
     * @return a JSON string
     * @throws java.lang.Exception
     */
    String getUserSubscriptionsAsJson( String userId, String typeSubscription ) throws Exception;

    /**
     * update user subscriptions to the specified subscription list
     * 
     * @param subscriptionsList
     * @return true if success
     * @throws java.lang.Exception
     */
    boolean updateSubscribtions( List<Subscription> subscriptionsList ) throws Exception;

    /**
     * update user subscription
     * 
     * @param subscription
     * @return true if success
     * @throws java.lang.Exception
     */
    boolean update( Subscription subscription ) throws Exception;

    /**
     * get feed list
     * 
     * @return the list
     */
    List<Feed> getFeeds( );
}
