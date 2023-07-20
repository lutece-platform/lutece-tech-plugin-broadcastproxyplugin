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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.broadcastproxy.business.SubscriptionLink;
import fr.paris.lutece.plugins.broadcastproxy.business.SubscriptionLinkHome;
import fr.paris.lutece.plugins.broadcastproxy.business.providers.dolist.DolistConstants;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * 
 * Load the list of subscriptions
 *
 */
public class BroadcastDaemon extends Daemon
{

    // Constants
    private static final String JSON_NODE_ITEMLIST              = AppPropertiesService.getProperty( "dolist.jsonNode.ItemList" );
    private static final String JSON_NODE_GROUP                 = AppPropertiesService.getProperty( "dolist.jsonNode.item.Group" );
    private static final String JSON_NODE_GROUP_ID              = AppPropertiesService.getProperty( "dolist.jsonNode.item.GroupId" );
    private static final String JSON_NODE_INTEREST_LIST         = AppPropertiesService.getProperty( "dolist.jsonNode.item.InterestList" );
    private static final String JSON_NODE_GROUP_NAME            = AppPropertiesService.getProperty( "dolist.jsonNode.group.Name" );
    private static final String JSON_NODE_DELETE_DATE           = AppPropertiesService.getProperty( "dolist.jsonNode.interest.isActive" );
    private static final String PROPERTY_ACCOUNT_ID             = AppPropertiesService.getProperty( "dolist.CONSTANTE_ACCOUNT_ID" );
    private static final String PROPERTY_GROUP_IDS              = AppPropertiesService.getProperty( "dolist.list.group_ids" );
    
    @Override
    public void run( )
    {
        loadSubscription( PROPERTY_ACCOUNT_ID );
    }

    /**
     * Retrieve subscriptions
     */
    private void loadSubscription( String strAccountId )
    {
        BroadcastService broadcastService = BroadcastService.getInstance( );
        String jsonAllSubscriptionsInterest = broadcastService.getAllSubscriptionByGroup( DolistConstants.TYPE_INTEREST, strAccountId );
        String subscriptionsInJson = broadcastService.getAllSubscriptionByGroup( DolistConstants.TYPE_SUBSCRIPTION, strAccountId );

        ObjectMapper mapper = new ObjectMapper( );
        String groupName = StringUtils.EMPTY;
        Map<String, String> subscriptionsMapIdName = new HashMap<>( );
        List<String> listGroupIds = Arrays.asList( PROPERTY_GROUP_IDS.split( ";" ) );

        try
        {
            // Get subscriptions data (id and name)
            JsonNode nodesSub = mapper.readTree( subscriptionsInJson );

            JsonNode itemListNodeSub = nodesSub.get( JSON_NODE_ITEMLIST );

            for ( JsonNode node : itemListNodeSub )
            {
                if ( node.get( "IsEnabled" ).asBoolean( ) )
                {
                    subscriptionsMapIdName.put( node.get( JSON_NODE_GROUP_NAME ).asText( ), node.get( "ID" ).asText( ) );
                }
            }
            
            //Interests
            JsonNode nodes = mapper.readTree( jsonAllSubscriptionsInterest );
            if ( !nodes.get( JSON_NODE_ITEMLIST ).isNull( ) )
            {
                JsonNode itemListNode = nodes.get( JSON_NODE_ITEMLIST );

                for ( JsonNode itemNode : itemListNode )
                {
                    JsonNode groupData = itemNode.get( JSON_NODE_GROUP );                    
                    
                    if( listGroupIds.contains( groupData.get( JSON_NODE_GROUP_ID ).asText( ) ) )
                    {
                        groupName = groupData.get( JSON_NODE_GROUP_NAME ).asText( );

                        if ( groupName.substring( 0, 1 ).equals( "[" ) && groupName.substring( groupName.length( ) - 1, groupName.length( ) ).equals( "]" ) && groupName.length( ) > 2 )
                        {
                            String[] splitDlGrName = groupName.split( "\\]" );
                            groupName = splitDlGrName[0].substring( 1, splitDlGrName[0].length( ) );
                        }
                        
                        for ( JsonNode node : itemNode.get( JSON_NODE_INTEREST_LIST ) )
                        {
                            createSubscriptionLink( subscriptionsMapIdName, groupName, node  );
                        }
                    }

                }
            }
        } catch ( Exception e )
        {
            String strError = "Error occured while building list of all subscription.";
            AppLogService.error( strError + e.getMessage( ), e );
        }
    }

    private void createSubscriptionLink( Map<String, String> subscriptionsMapIdName, String groupName, JsonNode node )
    {
        String strName = node.get( JSON_NODE_GROUP_NAME ).asText( );

        if ( subscriptionsMapIdName.containsKey( strName ) && !node.has( JSON_NODE_DELETE_DATE )
                && !SubscriptionLinkHome.findBySubscriptionId( Integer.parseInt( subscriptionsMapIdName.get( strName ) ) ).isPresent( ) )
        {
            SubscriptionLink subLink = new SubscriptionLink( );
            
            subLink.setSubscriptionId( Integer.parseInt( subscriptionsMapIdName.get( strName ) ) );
            subLink.setInterestId( node.get( "ID" ).asInt( ) );
            subLink.setGroupId( node.get( "GroupID" ).asInt( ) );
            subLink.setDescription( StringUtils.EMPTY );
            subLink.setLabel( strName );
            subLink.setPictogramme( StringUtils.EMPTY );
            subLink.setFrequency( StringUtils.EMPTY );
            subLink.setEnabled( true );
            subLink.setGroup( groupName );

            SubscriptionLinkHome.create( subLink );
        }

    }

}
