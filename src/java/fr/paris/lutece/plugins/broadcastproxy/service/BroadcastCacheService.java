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


import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.portal.service.cache.AbstractCacheableService;


/**
 * 
 * BroadcastCacheService
 *
 */
public class BroadcastCacheService extends AbstractCacheableService
{

    private static final String SERVICE_NAME = "broadcastCacheService";
    private static BroadcastCacheService _singleton;
    
    /**
     * Init cache
     */
    private BroadcastCacheService ( )
    {
        initCache( );
    }
    
    /**
     * Get instance of BroadcastCacheService
     * @return instance of broadcastCacheService
     */
    public static BroadcastCacheService getInstance ( )
    {
        if ( _singleton == null )
        {
            _singleton = new BroadcastCacheService( );
        }
        
        return _singleton;
    }
    
    
    @Override
    public String getName( )
    {
        return SERVICE_NAME;
    }
    
    public void addUserSubscription (  String strUserId, String strListSubscriptionIds  )
    {
        if ( StringUtils.isNotEmpty( strListSubscriptionIds ) && getUserSubscriptionIds(  getCacheKey( strUserId ) ) == null )
        {
            putInCache( getCacheKey(strUserId  ), strListSubscriptionIds );
        }
    }
    
    public void removeUserSubscription (  String strUserId  )
    {
         removeKey( getCacheKey(strUserId  ) );
    }

    public String getUserSubscriptionIds ( String strUserId )
    {
        return  ( String ) getFromCache( getCacheKey( strUserId ) );
    }

    private String getCacheKey( String strUserId )
    {
        StringBuilder sbKey = new StringBuilder( );
        sbKey.append( "[subscription_user:" ).append( strUserId ).append( "]" );
        return sbKey.toString( );
    }
    
}
