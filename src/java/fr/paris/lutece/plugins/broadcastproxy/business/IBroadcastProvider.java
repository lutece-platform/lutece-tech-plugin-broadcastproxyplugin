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
package fr.paris.lutece.plugins.broadcastproxy.business;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

public interface IBroadcastProvider
{

    /**
     * get provider name
     * 
     * @return
     */
    String getName( );

    /**
     * update user subscriptions to the specified subscription list
     * 
     * @param subscriptionsList
     * @param strAccountId
     * @return true if success
     * @throws java.lang.Exception
     */
    boolean updateSubscribtions( String userId, String jsonSubscriptions, String strAccountId ) throws Exception;

    /**
     * update user subscriptions to the specified subscription list
     * 
     * @param subscriptionsList
     * @param strAccountId
     * @return true if success
     * @throws java.lang.Exception
     */
    boolean updateArrondissementSubscribtions( String userId, String jsonSubscriptions, String strAccountId ) throws Exception;
    
    /**
     * update user subscriptions to the specified subscription list
     * 
     * @param subscriptionsList
     * @return true if success
     * @throws java.lang.Exception
     */
    boolean updateSubscribtions( String userId, List<Subscription> subscriptionsList ) throws Exception;

    /**
     * update user subscription
     * 
     * @param subscription
     * @param strAccountId
     * @return true if success
     * @throws java.lang.Exception
     */
    boolean update( Subscription subscription, String strAccountId ) throws Exception;

    /**
     * get feed list
     * 
     * @return the list
     */
    List<Feed> getFeeds( );

    
    /**
     * get all subscription by group
     * @param typeSubscription
     * @param strAccountId
     * @return
     * @throws Exception
     */
    String getAllSubscriptionByGroup( String typeSubscription, String strAccountId );
    
    /**
     * get user subscriptions ids
     * @param strUserId
     * @param strAccountId
     * @return list of user subscription ids
     */
    List<JSONObject> getUserSubscriptionIds ( String strUserId, String strAccountId );

}
