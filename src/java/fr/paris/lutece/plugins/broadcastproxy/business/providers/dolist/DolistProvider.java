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
package fr.paris.lutece.plugins.broadcastproxy.business.providers.dolist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.broadcastproxy.business.Feed;
import fr.paris.lutece.plugins.broadcastproxy.business.IBroadcastProvider;
import fr.paris.lutece.plugins.broadcastproxy.business.Subscription;
import fr.paris.lutece.plugins.broadcastproxy.business.SubscriptionLink;
import fr.paris.lutece.plugins.broadcastproxy.business.SubscriptionLinkHome;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class DolistProvider implements IBroadcastProvider
{
    // Constants
    private static final String PROVIDER_NAME = "Dolist";

    private static final String JSON_NODE_ITEMLIST = AppPropertiesService.getProperty( "dolist.jsonNode.ItemList" );
    private static final String JSON_NODE_ITEM_INTERESTLIST = AppPropertiesService.getProperty( "dolist.jsonNode.item.InterestList" );
    private static final String JSON_NODE_ITEM_GROUP = AppPropertiesService.getProperty( "dolist.jsonNode.item.Group" );
    private static final String JSON_NODE_SUB_ID = AppPropertiesService.getProperty( "dolist.jsonNode.sub.SubscriptionID" );
    private static final String JSON_NODE_INTEREST_NAME = AppPropertiesService.getProperty( "dolist.jsonNode.interest.Name" );
    private static final String JSON_NODE_INTEREST_IS_ACTIVE = AppPropertiesService.getProperty( "dolist.jsonNode.interest.isActive" );
    private static final String JSON_NODE_GROUP_NAME = AppPropertiesService.getProperty( "dolist.jsonNode.group.Name" );
    private static final String JSON_NODE_SUB_NAME = AppPropertiesService.getProperty( "dolist.jsonNode.sub.Name" );
    private static final String JSON_NODE_SUB_SUBSCRIBED = AppPropertiesService.getProperty( "dolist.jsonNode.sub.Subscribed" );
    private static final String JSON_NODE_SUB_UNSUBSCRIBED = AppPropertiesService.getProperty( "dolist.jsonNode.sub.Unsubscribed" );

    private static final String CONSTANT_OPERATION_MODE_ADD = AppPropertiesService.getProperty( "dolist.CONSTANTE_OPERATION_MODE_ADD" );
    private static final String CONSTANT_OPERATION_MODE_DELETE = AppPropertiesService.getProperty( "dolist.CONSTANTE_OPERATION_MODE_DELETE" );

    // instance variables
    private DolistAPI _dolistAPI;

    private TreeMap<Integer, String> _groupViewRang = new TreeMap<>( );
    private Map<String, String> _groupsMapIdName = new HashMap<>( );
    private Map<String, String> _interestsMapIdName = new HashMap<>( );
    private Map<String, String> _subscriptionsMapIdName = new HashMap<>( );
    
    /**
     * Constructor
     * 
     * @throws Exception
     */
    private DolistProvider( ) throws Exception
    {
        _dolistAPI = new DolistAPI( );
    }

    @Override
    public String getName( )
    {
        return PROVIDER_NAME;
    }

    @Override
    public boolean updateSubscribtions( String userId, List<Subscription> subscriptionsList ) throws Exception
    {
        return false;
    }

    @Override
    public boolean updateSubscribtions( String userId, String jsonSubscriptions, String strAccountId ) throws Exception
    {
        Map<String, String> subscriptionStatus = new HashMap<>( );
        List<Integer> interestsToAdd = new ArrayList<>( );
        List<Integer> interestsToDelete = new ArrayList<>( );
        try
        {
            Map<Integer, Boolean> subscriptionsToUpdate = getUserSubscribtionsToUpdate( jsonSubscriptions );

            for ( Map.Entry<Integer, Boolean> sub : subscriptionsToUpdate.entrySet( ) )
            {
                Optional<SubscriptionLink> subscriptionLink = SubscriptionLinkHome.findBySubscriptionId( sub.getKey( ) );
                if(  subscriptionLink.isPresent( ) )
                {
                    if ( sub.getValue( ) )
                    {
                        subscriptionStatus.put( String.valueOf( sub.getKey( ) ), JSON_NODE_SUB_SUBSCRIBED );
                        if( subscriptionLink.get( ).getInterestId( ) > 0)
                        {
                            interestsToAdd.add( subscriptionLink.get( ).getInterestId( ) );
                        }
                    }
                    else
                    {
                        subscriptionStatus.put( String.valueOf( sub.getKey( ) ), JSON_NODE_SUB_UNSUBSCRIBED );
                        if( subscriptionLink.get( ).getInterestId( ) > 0)
                        {
                            interestsToDelete.add( subscriptionLink.get( ).getInterestId( ) );
                        }
                    }
                }
            }

            updateSubAndInterests( userId, strAccountId, subscriptionStatus, interestsToAdd, interestsToDelete );

        }
        catch( Exception e )
        {
            AppLogService.error( "An error occured while updating subscriptions : " + e.getMessage( ) );
            return false;
        }

        return true;
    }
    
    @Override
    public boolean updateArrondissementSubscribtions( String userId, String jsonSubscriptions, String strAccountId ) throws Exception
    {
        Map<String, String> subscriptionStatus = new HashMap<>( );
        List<Integer> interestsToAdd = new ArrayList<>( );
        List<Integer> interestsToDelete = new ArrayList<>( );
        initMapIdNameArrondissement( strAccountId );
        
        try
        {
            Map<Integer, Boolean> subscriptionsToUpdate = getUserSubscribtionsToUpdate( jsonSubscriptions );

            for ( Map.Entry<Integer, Boolean> sub : subscriptionsToUpdate.entrySet( ) )
            {
                for( Map.Entry<String, String>  subscription: _subscriptionsMapIdName.entrySet( ) )
                {
                    subscriptionStatus.put( subscription.getKey( ), sub.getValue( ) ? JSON_NODE_SUB_SUBSCRIBED : JSON_NODE_SUB_UNSUBSCRIBED);
                }  
                for( Map.Entry<String, String>  interest: _interestsMapIdName.entrySet( ) )
                {
                    if ( sub.getValue( ) )
                    {
                        interestsToAdd.add( Integer.parseInt( interest.getKey( ) ) );
                    }
                    else
                    {
                        interestsToDelete.add( Integer.parseInt( interest.getKey( ) ) );
                    }
                }
            }
            
            updateSubAndInterests( userId, strAccountId, subscriptionStatus, interestsToAdd, interestsToDelete );

        }
        catch( Exception e )
        {
            AppLogService.error( "An error occured while updating subscriptions : " + e.getMessage( ) );
            return false;
        }

        return true;
    }
    
    private void updateSubAndInterests( String userId, String strAccountId, Map<String, String> subscriptionStatus, List<Integer> interestsToAdd, List<Integer> interestsToDelete ) throws Exception
    {
        // update dolist subscriptions
        _dolistAPI.updateSubscribtions( userId, subscriptionStatus, strAccountId );

        // Update Dolist Interests
        if ( !interestsToAdd.isEmpty( ) )
        {
            _dolistAPI.updateInterests( userId, interestsToAdd, CONSTANT_OPERATION_MODE_ADD, strAccountId );
        }

        if ( !interestsToDelete.isEmpty( ) )
        {
            _dolistAPI.updateInterests( userId, interestsToDelete, CONSTANT_OPERATION_MODE_DELETE, strAccountId );
        }
    }


    @Override
    public boolean update( Subscription sub, String strAccountId ) throws Exception
    {
        Map<String, String> subscriptionStatus = new HashMap<String, String>( );
        initMapIdName( strAccountId );
        if ( sub.isActive( ) )
        {
            subscriptionStatus.put( getSubscriptionDolistId( sub.getName( ), _interestsMapIdName ), JSON_NODE_SUB_SUBSCRIBED );
        }
        else
        {
            subscriptionStatus.put( getSubscriptionDolistId( sub.getName( ), _interestsMapIdName ), JSON_NODE_SUB_UNSUBSCRIBED );
        }

        _dolistAPI.updateSubscribtions( sub.getUserId( ), subscriptionStatus, strAccountId );

        return true;
    }

    @Override
    public List<Feed> getFeeds( )
    {
        return null;
    }
    
    @Override
    public String getAllSubscriptionByGroup( String typeSubscription, String strAccountId )
    {
        String strAllSubscription = StringUtils.EMPTY;
        try
        {
            strAllSubscription = _dolistAPI.getAllSubscriptions( typeSubscription, strAccountId );
        }
        catch ( Exception e )
        {
            AppLogService.error( "Error occured while getting all subscriptions.", e.getMessage( ) );
        }
        return strAllSubscription;
    }

    public Map<Integer, Boolean> getUserSubscribtionsToUpdate( String jsonSubscriptions ) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper( );
        Map<Integer, Boolean> subscriptionsToUpdate = new HashMap<>( );

        try
        {
            JsonNode jsonNodes = mapper.readTree( jsonSubscriptions );
            subscriptionsToUpdate.put( jsonNodes.get( "id" ).asInt( ),
            Boolean.valueOf( jsonNodes.get( "active" ).asText( ) ) );
        }
        catch( Exception e )
        {
            AppLogService.error( "An error occured while updating subscriptions : " + e.getMessage( ) );
            return null;
        }

        return subscriptionsToUpdate;
    }

    /**
     * init
     * @param strAccountId
     * @throws Exception
     */
    private void initMapIdName( String strAccountId ) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper( );

        String subscriptionsInJson = _dolistAPI.getAllSubscriptions( DolistConstants.TYPE_SUBSCRIPTION, strAccountId );
        String interestsInJson = _dolistAPI.getAllSubscriptions( DolistConstants.TYPE_INTEREST, strAccountId );

        try
        {
            // Get subscriptions data (id and name)
            JsonNode nodes = mapper.readTree( subscriptionsInJson );

            JsonNode itemListNode = nodes.get( JSON_NODE_ITEMLIST );

            for ( JsonNode node : itemListNode )
            {
                if ( node.get( "IsEnabled" ).asBoolean( ) )
                {
                    _subscriptionsMapIdName.put( node.get( "ID" ).asText( ), node.get( JSON_NODE_SUB_NAME ).asText( ) );
                }
            }

            // Get interests data (id and name)
            nodes = null;
            itemListNode = null;

            nodes = mapper.readTree( interestsInJson );

            itemListNode = nodes.get( JSON_NODE_ITEMLIST );

            for ( JsonNode node : itemListNode )
            {
                JsonNode groupData = node.get( JSON_NODE_ITEM_GROUP );

                String dolistGroupName = groupData.get( JSON_NODE_GROUP_NAME ).asText( );
                if ( dolistGroupName.substring( 0, 1 ).equals( "[" )
                        && dolistGroupName.substring( dolistGroupName.length( ) - 1, dolistGroupName.length( ) ).equals( "]" ) )
                {
                    String [ ] splitDlGrName = dolistGroupName.split( "\\]" );

                    if ( splitDlGrName.length > 0 )
                    {
                        String groupName = splitDlGrName [0].substring( 1, splitDlGrName [0].length( ) );

                        if ( splitDlGrName.length == 2 && splitDlGrName [1].length( ) > 0 && !groupName.equals( "Alertes" ) )
                        {
                            _groupViewRang.put( Integer.valueOf( splitDlGrName [1].substring( 1, splitDlGrName [1].length( ) ) ), groupName );
                        }

                        _groupsMapIdName.put( groupData.get( "ID" ).asText( ), groupName );

                        JsonNode intersts = node.get( JSON_NODE_ITEM_INTERESTLIST );
                        for ( JsonNode interest : intersts )
                        {
                            if ( !interest.has( JSON_NODE_INTEREST_IS_ACTIVE ) )
                            {
                                _interestsMapIdName.put( interest.get( "ID" ).asText( ), interest.get( JSON_NODE_INTEREST_NAME ).asText( ) );
                            }
                        }
                    }
                }
            }
        }
        catch( Exception e )
        {
            String strError = "Error occured while mapping Ids and Names of subscriptions.";
            AppLogService.error( strError + e.getMessage( ), e );
        }
    }

    /**
     * init
     * @param strAccountId
     * @throws Exception
     */
    private void initMapIdNameArrondissement( String strAccountId ) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper( );
        _interestsMapIdName = new HashMap<>( );
        _subscriptionsMapIdName = new HashMap<>( );
        
        String subscriptionsInJson = _dolistAPI.getAllSubscriptions( DolistConstants.TYPE_SUBSCRIPTION, strAccountId );
        String interestsInJson = _dolistAPI.getAllSubscriptions( DolistConstants.TYPE_INTEREST, strAccountId );

        try
        {
            // Get subscriptions data (id and name)
            JsonNode nodes = mapper.readTree( subscriptionsInJson );

            JsonNode itemListNode = nodes.get( JSON_NODE_ITEMLIST );

            for ( JsonNode node : itemListNode )
            {
                if ( node.get( "IsEnabled" ).asBoolean( ) )
                {
                    _subscriptionsMapIdName.put( node.get( "ID" ).asText( ), node.get( JSON_NODE_SUB_NAME ).asText( ) );
                }
            }

            // Get interests data (id and name)
            nodes = mapper.readTree( interestsInJson );

            itemListNode = nodes.get( JSON_NODE_ITEMLIST );

            for ( JsonNode node : itemListNode )
            {
                if ( node != null )
                {
                    JsonNode intersts = node.get( JSON_NODE_ITEM_INTERESTLIST );
                    for ( JsonNode interest : intersts )
                    {
                        if ( !interest.has( JSON_NODE_INTEREST_IS_ACTIVE ) )
                        {
                            _interestsMapIdName.put( interest.get( "ID" ).asText( ), interest.get( JSON_NODE_INTEREST_NAME ).asText( ) );
                        }
                    }
                }
            }
        }
        catch( Exception e )
        {
            String strError = "Error occured while mapping Ids and Names of subscriptions.";
            AppLogService.error( strError + e.getMessage( ), e );
        }
    }
    

    /**
     * get subscription dolist ID
     * 
     * @param sub
     * @return the map
     */
    public String getSubscriptionDolistId( String subscriptionName, Map<String, String> mapIdName )
    {
        String subscriptionDolistId = null;

        for ( Map.Entry<String, String> subMapIdName : mapIdName.entrySet( ) )
        {
            if ( subMapIdName.getValue( ).equals( subscriptionName ) )
            {
                subscriptionDolistId = subMapIdName.getKey( );
            }
        }

        return subscriptionDolistId;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public List<JSONObject> getUserSubscriptionIds( String strUserId, String strAccountId )
    {        
        ObjectMapper mapper = new ObjectMapper( );
        List<JSONObject> jsonLsit = new ArrayList<>();
        
        try
        {
            String userDolistSubscriptionsList = _dolistAPI.getUserSubscriptions( strUserId, DolistConstants.TYPE_SUBSCRIPTION, strAccountId );

            JsonNode nodes = mapper.readTree( userDolistSubscriptionsList );
            JsonNode itemListNode = nodes.get( JSON_NODE_ITEMLIST );
                    
            if ( itemListNode != null )
            {
                for ( JsonNode itemNode : itemListNode )
                {
                    if( itemNode.get( "Status").asText( ).equals( "Subscribed" )  )
                    {
                        JSONObject json = new JSONObject();
                        json.put( "id", itemNode.get( JSON_NODE_SUB_ID ).asInt( )  );
                        
                        jsonLsit.add( json );
                    }
                }
            }
        }
        catch (Exception e)
        {
            AppLogService.error( "Error occured while getting the list of user subscriptions ids :", e.getMessage( ) );
        }
        
        return jsonLsit;
    }

}
