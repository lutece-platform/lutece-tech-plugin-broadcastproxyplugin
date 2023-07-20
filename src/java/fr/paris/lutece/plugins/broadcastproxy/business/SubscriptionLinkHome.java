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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;


import java.util.List;
import java.util.Optional;

/**
 * This class provides instances management methods (create, find, ...) for SubscriptionLink objects
 */
public final class SubscriptionLinkHome
{
    // Static variable pointed at the DAO instance
    private static ISubscriptionLinkDAO _dao = SpringContextService.getBean( "broadcastproxy.subscriptionLinkDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "broadcastproxy" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private SubscriptionLinkHome(  )
    {
    }

    /**
     * Create an instance of the subscriptionLink class
     * @param subscriptionLink The instance of the SubscriptionLink which contains the informations to store
     * @return The  instance of subscriptionLink which has been created with its primary key.
     */
    public static SubscriptionLink create( SubscriptionLink subscriptionLink )
    {
        _dao.insert( subscriptionLink, _plugin );

        return subscriptionLink;
    }

    /**
     * Update of the subscriptionLink which is specified in parameter
     * @param subscriptionLink The instance of the SubscriptionLink which contains the data to store
     * @return The instance of the  subscriptionLink which has been updated
     */
    public static SubscriptionLink update( SubscriptionLink subscriptionLink )
    {
        _dao.store( subscriptionLink, _plugin );

        return subscriptionLink;
    }

    /**
     * Remove the subscriptionLink whose identifier is specified in parameter
     * @param nKey The subscriptionLink Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a subscriptionLink whose identifier is specified in parameter
     * @param nKey The subscriptionLink primary key
     * @return an instance of SubscriptionLink
     */
    public static Optional<SubscriptionLink> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Returns an instance of a subscriptionLink whose identifier is specified in parameter
     * @param nSubscriptionId The subscriptionLink subscription id 
     * @return an instance of SubscriptionLink
     */
    public static Optional<SubscriptionLink> findBySubscriptionId ( int nSubscriptionId )
    {
        return _dao.loadBySubscriptionId( nSubscriptionId, _plugin );
    }
    
    /**
     * Load the data of all the subscriptionLink objects and returns them as a list
     * @return the list which contains the data of all the subscriptionLink objects
     */
    public static List<SubscriptionLink> getSubscriptionLinksList( )
    {
        return _dao.selectSubscriptionLinksList( _plugin );
    }
    
    /**
     * Load the id of all the subscriptionLink objects and returns them as a list
     * @return the list which contains the id of all the subscriptionLink objects
     */
    public static List<Integer> getIdSubscriptionLinksList( )
    {
        return _dao.selectIdSubscriptionLinksList( _plugin );
    }
    
    /**
     * Load the data of all the subscriptionLink objects and returns them as a referenceList
     * @return the referenceList which contains the data of all the subscriptionLink objects
     */
    public static ReferenceList getSubscriptionLinksReferenceList( )
    {
        return _dao.selectSubscriptionLinksReferenceList( _plugin );
    }
    
	
    /**
     * Load the data of all the avant objects and returns them as a list
     * @param listIds liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<SubscriptionLink> getSubscriptionLinksListByIds( List<Integer> listIds )
    {
        return _dao.selectSubscriptionLinksListByIds( _plugin, listIds );
    }

    /**
     * Return true if exist disabled newsletter
     * @param plugin
     * @return Return true if exist disabled newsletter
     */
    public static boolean existDisabledNewsletter (  )
    {
        return _dao.existDisabledNewsletter( _plugin );
    }
    
}

