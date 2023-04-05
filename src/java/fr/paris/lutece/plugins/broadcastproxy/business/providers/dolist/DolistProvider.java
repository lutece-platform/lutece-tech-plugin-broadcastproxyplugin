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

import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.broadcastproxy.business.Feed;
import fr.paris.lutece.plugins.broadcastproxy.business.IBroadcastProvider;
import fr.paris.lutece.plugins.broadcastproxy.business.Subscription;
import fr.paris.lutece.plugins.broadcastproxy.service.Constants;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class DolistProvider implements IBroadcastProvider
{
    // Constants
    private static final String PROVIDER_NAME = "Dolist";

    private static final String JSON_NODE_ITEMLIST = AppPropertiesService.getProperty( "dolist.jsonNode.ItemList" );
    private static final String JSON_NODE_ITEM_INTERESTLIST = AppPropertiesService.getProperty( "dolist.jsonNode.item.InterestList" );
    private static final String JSON_NODE_ITEM_INTEREST = AppPropertiesService.getProperty( "dolist.jsonNode.item.Interest" );
    private static final String JSON_NODE_ITEM_GROUP = AppPropertiesService.getProperty( "dolist.jsonNode.item.Group" );
    private static final String JSON_NODE_ITEM_STATUS = AppPropertiesService.getProperty( "dolist.jsonNode.item.Status" );
    private static final String JSON_NODE_SUB_ID = AppPropertiesService.getProperty( "dolist.jsonNode.sub.SubscriptionID" );
    private static final String JSON_NODE_INTEREST_NAME = AppPropertiesService.getProperty( "dolist.jsonNode.interest.Name" );
    private static final String JSON_NODE_INTEREST_IS_ACTIVE = AppPropertiesService.getProperty( "dolist.jsonNode.interest.isActive" );
    private static final String JSON_NODE_GROUP_NAME = AppPropertiesService.getProperty( "dolist.jsonNode.group.Name" );
    private static final String JSON_NODE_SUB_NAME = AppPropertiesService.getProperty( "dolist.jsonNode.sub.Name" );
    private static final String JSON_NODE_SUB_SUBSCRIBED = AppPropertiesService.getProperty( "dolist.jsonNode.sub.Subscribed" );
    private static final String JSON_NODE_SUB_UNSUBSCRIBED = AppPropertiesService.getProperty( "dolist.jsonNode.sub.Unsubscribed" );

    private static final String CONSTANT_OPERATION_MODE_ADD = AppPropertiesService.getProperty( "dolist.CONSTANTE_OPERATION_MODE_ADD" );
    private static final String CONSTANT_OPERATION_MODE_DELETE = AppPropertiesService.getProperty( "dolist.CONSTANTE_OPERATION_MODE_DELETE" );
    private static final String CONSTANT_SUB_DESCRIPTION_PREFIX = AppPropertiesService.getProperty( "subscription.description.CONSTANT_PREFIX" );

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
    public DolistProvider( ) throws Exception
    {
        _dolistAPI = new DolistAPI( );
        initMapIdName( );
    }

    @Override
    public String getName( )
    {
        return PROVIDER_NAME;
    }

    @Override
    public boolean subscribe( String userId, String subscriptionId, String typeSubscription ) throws Exception
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean unsubscribe( String userId, String subscriptionId, String typeSubscription ) throws Exception
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Map<String, Map<String, List<Subscription>>> getUserSubscriptionsAsList( String userId ) throws Exception
    {

        String userDolistSubscriptionsList = _dolistAPI.getUserSubscriptions( userId, DolistConstants.TYPE_SUBSCRIPTION );
        if ( userDolistSubscriptionsList == null || userDolistSubscriptionsList.isEmpty() )
        	return null;
        
        Map<String, Map<String, List<Subscription>>> userSubscriptions = getUserSubscriptionsByGroup( userDolistSubscriptionsList, userId );

        return userSubscriptions;
    }

    @Override
    public String getUserSubscriptionsAsJson( String userId ) throws Exception
    {
        Map<String, Map<String, List<Subscription>>> userSubscriptions = getUserSubscriptionsAsList( userId );
        if ( userSubscriptions == null )
        	return null;
        
        String jsonUserSubscriptions = buildUserSubscriptionsJson( userSubscriptions );

        return jsonUserSubscriptions;
    }

    @Override
    public boolean updateSubscribtions( String userId, List<Subscription> subscriptionsList ) throws Exception
    {
        return false;
    }

    @Override
    public boolean updateSubscribtions( String userId, String jsonSubscriptions ) throws Exception
    {
        Map<String, String> subscriptionStatus = new HashMap<>( );
        List<Integer> interestsToAdd = new ArrayList<>( );
        List<Integer> interestsToDelete = new ArrayList<>( );

        try
        {
            Map<String, Boolean> subscriptionsToUpdate = getUserSubscribtionsToUpdate( jsonSubscriptions );

            for ( Map.Entry<String, Boolean> sub : subscriptionsToUpdate.entrySet( ) )
            {
                if ( sub.getValue( ) )
                {
                    subscriptionStatus.put( getSubscriptionDolistId( sub.getKey( ), _subscriptionsMapIdName ), JSON_NODE_SUB_SUBSCRIBED );
                    interestsToAdd.add( Integer.parseInt( getSubscriptionDolistId( sub.getKey( ), _interestsMapIdName ) ) );
                }
                else
                {
                    subscriptionStatus.put( getSubscriptionDolistId( sub.getKey( ), _subscriptionsMapIdName ), JSON_NODE_SUB_UNSUBSCRIBED );
                    interestsToDelete.add( Integer.parseInt( getSubscriptionDolistId( sub.getKey( ), _interestsMapIdName ) ) );
                }
            }

            // update dolist subscriptions
            _dolistAPI.updateSubscribtions( userId, subscriptionStatus );

            // Update Dolist Interests
            if ( !interestsToAdd.isEmpty( ) )
            {
                _dolistAPI.updateInterests( userId, interestsToAdd, CONSTANT_OPERATION_MODE_ADD );
            }

            if ( !interestsToDelete.isEmpty( ) )
            {
                _dolistAPI.updateInterests( userId, interestsToDelete, CONSTANT_OPERATION_MODE_DELETE );
            }

        }
        catch( Exception e )
        {
            AppLogService.error( "An error occured while updating subscriptions : " + e.getMessage( ) );
            return false;
        }

        return true;
    }

    @Override
    public boolean update( Subscription sub ) throws Exception
    {
        Map<String, String> subscriptionStatus = new HashMap<String, String>( );

        if ( sub.isActive( ) )
        {
            subscriptionStatus.put( getSubscriptionDolistId( sub.getName( ), _interestsMapIdName ), JSON_NODE_SUB_SUBSCRIBED );
        }
        else
        {
            subscriptionStatus.put( getSubscriptionDolistId( sub.getName( ), _interestsMapIdName ), JSON_NODE_SUB_UNSUBSCRIBED );
        }

        _dolistAPI.updateSubscribtions( sub.getUserId( ), subscriptionStatus );

        return true;
    }

    @Override
    public List<Feed> getFeeds( )
    {
        return null;
    }

    @Override
    public List<String> getSubscriptionViewOrder( )
    {
        List<String> subscriptionViewOrder = new ArrayList<String>( );

        for ( Map.Entry<Integer, String> group : _groupViewRang.entrySet( ) )
        {
            subscriptionViewOrder.add( group.getValue( ) );
        }

        return subscriptionViewOrder;
    }

    public Map<String, List<Subscription>> getOrderedSubscriptions( Map<String, List<Subscription>> subscriptions )
    {
        Map<String, List<Subscription>> ordredSubscriptions = new HashMap<String, List<Subscription>>( );

        if ( subscriptions != null && !subscriptions.isEmpty( ) )
        {
            List<String> subscriptionViewOrder = getSubscriptionViewOrder( );

            for ( String orderedSubscriptionId : subscriptionViewOrder )
            {
                for ( Map.Entry<String, List<Subscription>> subscriptionsList : subscriptions.entrySet( ) )
                {
                    if ( subscriptionsList.getKey( ) != null && subscriptionsList.getKey( ).equals( orderedSubscriptionId ) )
                    {
                        ordredSubscriptions.put( subscriptionsList.getKey( ), subscriptionsList.getValue( ) );
                    }
                }
            }
        }

        return ordredSubscriptions;
    }

    public Map<String, Map<String, List<Subscription>>> getUserSubscriptionsByGroup( String jsonUserDolistSubscriptions, String userId ) throws Exception
    {
        Map<String, Map<String, List<Subscription>>> userSubscriptions = new HashMap<String, Map<String, List<Subscription>>>( );
        List<String> userSubscriptionNamesList = new ArrayList<String>( );
        Map<String, List<String>> allDolistSubscriptionsNamesByGroup = new HashMap<String, List<String>>( );
        Map<String, List<Subscription>> userNewsletters = new HashMap<String, List<Subscription>>( );
        Map<String, List<Subscription>> userAlerts = new HashMap<String, List<Subscription>>( );

        try
        {
            // Build list of user subscriptions and interests names
            userSubscriptionNamesList.addAll( getUserSubscriptionsNamesAsList( jsonUserDolistSubscriptions, DolistConstants.TYPE_SUBSCRIPTION ) );

            // Add all dolist subscriptions and interests names by group
            allDolistSubscriptionsNamesByGroup.putAll( getAllSubscriptionsNamesByGroup( DolistConstants.TYPE_INTEREST ) );

            for ( Map.Entry<String, List<String>> subscriptionsNamesByGroup : allDolistSubscriptionsNamesByGroup.entrySet( ) )
            {
                if ( subscriptionsNamesByGroup.getKey( ) != null )
                {
                    List<Subscription> subscriptionsList = new ArrayList<>( );

                    for ( String name : subscriptionsNamesByGroup.getValue( ) )
                    {
                        Subscription sub = new Subscription( );

                        sub.setUserId( userId );
                        sub.setName( name );
                        sub.setId( name.trim( ).replace( " ", "_" ) );

                        if ( userSubscriptionNamesList.contains( name ) )
                            sub.setActive( true );
                        else
                            sub.setActive( false );

                        subscriptionsList.add( sub );
                    }

                    String groupName = subscriptionsNamesByGroup.getKey( );

                    if ( groupName.equals( "Alertes" ) )
                    {
                        // subscriptionType = Constants.TYPE_ALERT;
                        if ( userAlerts.containsKey( groupName ) )
                        {
                            subscriptionsList.addAll( userAlerts.get( groupName ) );
                        }

                        userAlerts.put( groupName, subscriptionsList );
                    }
                    else
                    {
                        // subscriptionType = Constants.TYPE_NEWSLETTER;
                        if ( userNewsletters.containsKey( groupName ) )
                        {
                            subscriptionsList.addAll( userNewsletters.get( groupName ) );
                        }

                        userNewsletters.put( groupName, subscriptionsList );
                    }
                }
            }

            userSubscriptions.put( Constants.TYPE_NEWSLETTER, getOrderedSubscriptions( userNewsletters ) );
            userSubscriptions.put( Constants.TYPE_ALERT, userAlerts );
        }
        catch( Exception e )
        {
            String strError = "Error occured while getting the user list subscriptions.";
            AppLogService.error( strError + e.getMessage( ), e );
        }

        return userSubscriptions;
    }

    public Map<String, List<String>> getAllSubscriptionsNamesByGroup( String typeSubscription ) throws Exception
    {
        String subscriptionsInJson = _dolistAPI.getAllSubscriptions( typeSubscription );
        return buildDolistAllSubscriptionsNamesListByGroup( subscriptionsInJson, typeSubscription );
    }

    public Map<String, List<String>> buildDolistAllSubscriptionsNamesListByGroup( String jsonAllSubscriptions, String typeSubscription ) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper( );
        Map<String, List<String>> SubscriptionsName = new HashMap<String, List<String>>( );
        String groupName = StringUtils.EMPTY;

        JsonNode nodes = mapper.readTree( jsonAllSubscriptions );
        if ( nodes.get( JSON_NODE_ITEMLIST ).isNull( ) )
            return null;
        try
        {
            JsonNode itemListNode = nodes.get( JSON_NODE_ITEMLIST );

            for ( JsonNode itemNode : itemListNode )
            {
                JsonNode groupData = itemNode.get( JSON_NODE_ITEM_GROUP );

                groupName = groupData.get( JSON_NODE_GROUP_NAME ).asText( );

                if ( groupName.substring( 0, 1 ).equals( "[" ) && groupName.substring( groupName.length( ) - 1, groupName.length( ) ).equals( "]" )
                        && groupName.length( ) > 2 )
                {
                    String [ ] splitDlGrName = groupName.split( "\\]" );

                    groupName = splitDlGrName [0].substring( 1, splitDlGrName [0].length( ) );

                    List<String> SubscriptionsNamesList = new ArrayList<String>( );
                    for ( JsonNode node : itemNode.get( JSON_NODE_ITEM_INTERESTLIST ) )
                    {
                        if ( !node.has( JSON_NODE_INTEREST_IS_ACTIVE ) )
                        {
                            SubscriptionsNamesList.add( node.get( JSON_NODE_INTEREST_NAME ).asText( ) );
                        }
                    }

                    Collections.sort( SubscriptionsNamesList, Collator.getInstance( Locale.FRENCH ) );

                    SubscriptionsName.put( groupName, SubscriptionsNamesList );
                }
            }
        }
        catch( Exception e )
        {
            String strError = "Error occured while getting the list of interests ids and names.";
            AppLogService.error( strError + e.getMessage( ), e );
        }

        return SubscriptionsName;
    }

    private List<String> getUserSubscriptionsNamesAsList( String jsonResponse, String typeSubscription )
    {
        ObjectMapper mapper = new ObjectMapper( );
        List<String> userSubscriptionNamesList = new ArrayList<String>( );
        List<String> activeSubscriptionsId = new ArrayList<String>( );
        
        try
        {
            // Get list of only active subscriptions
            for ( Map.Entry<String, String> subEntry : _subscriptionsMapIdName.entrySet( ) )
            {
                activeSubscriptionsId.add( subEntry.getKey( ) );
            }

            JsonNode nodes = mapper.readTree( jsonResponse );
            if ( nodes.get( JSON_NODE_ITEMLIST ).isNull( ) )
                return null;

            JsonNode itemListNode = nodes.get( JSON_NODE_ITEMLIST );
            if ( itemListNode != null )
            {
                if ( typeSubscription.equals( DolistConstants.TYPE_SUBSCRIPTION ) )
                {
                    for ( JsonNode itemNode : itemListNode )
                    {
                        if ( activeSubscriptionsId.contains( itemNode.get( JSON_NODE_SUB_ID ).asText( ) )
                                && itemNode.get( JSON_NODE_ITEM_STATUS ).asText( ).equals( JSON_NODE_SUB_SUBSCRIBED ) )
                            userSubscriptionNamesList.add( _subscriptionsMapIdName.get( itemNode.get( JSON_NODE_SUB_ID ).asText( ) ) );
                    }
                }
                else
                    if ( typeSubscription.equals( DolistConstants.TYPE_INTEREST ) )
                    {
                        for ( JsonNode node : itemListNode )
                        {
                            userSubscriptionNamesList.add( node.get( JSON_NODE_ITEM_INTEREST ).get( JSON_NODE_INTEREST_NAME ).asText( ) );
                        }
                    }
            }
        }
        catch( Exception e )
        {
            String strError = "Error occured while getting the list of interests ids and names.";
            AppLogService.error( strError + e.getMessage( ), e );
        }

        return userSubscriptionNamesList;
    }

    /**
     * Build JSON response from subscription beans list
     * 
     * @param the
     *            list
     * @return a JSON String
     */
    private String buildUserSubscriptionsJson( Map<String, Map<String, List<Subscription>>> userSubscriptions ) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper( );

        List<LinkedHashMap<String, Object>> subscriptionGroupList = new ArrayList<LinkedHashMap<String, Object>>( );
        Map<String, List<LinkedHashMap<String, Object>>> allSubscriptions = new HashMap<String, List<LinkedHashMap<String, Object>>>( );

        Map<String, String> subscriptionsDescription = getSubscriptionsDescription( );

        for ( Map.Entry<String, Map<String, List<Subscription>>> subscriptionsByGroup : userSubscriptions.entrySet( ) )
        {
            if ( subscriptionsByGroup.getKey( ) != null )
            {
                String groupDescription = StringUtils.EMPTY;
                LinkedHashMap<String, Object> subscriptionGroup = new LinkedHashMap<String, Object>( );
                List<LinkedHashMap<String, Object>> groupSubscriptionsList = new ArrayList<LinkedHashMap<String, Object>>( );

                for ( Map.Entry<String, List<Subscription>> sub : subscriptionsByGroup.getValue( ).entrySet( ) )
                {
                    LinkedHashMap<String, Object> singleSubscription = new LinkedHashMap<String, Object>( );

                    for ( Subscription subList : sub.getValue( ) )
                    {
                        for ( Map.Entry<String, String> description : subscriptionsDescription.entrySet( ) )
                        {
                            if ( description.getKey( ).equals( subList.getId( ) ) )
                            {
                                subList.setDescription( description.getValue( ) );
                                break;
                            }
                        }

                    }

                    singleSubscription.put( "subname", sub.getKey( ) );
                    singleSubscription.put( "sublist", sub.getValue( ) );

                    groupSubscriptionsList.add( singleSubscription );
                }

                for ( Map.Entry<String, String> description : subscriptionsDescription.entrySet( ) )
                {
                    if ( description.getKey( ).equals( "Alertes" ) && subscriptionsByGroup.getKey( ).equals( Constants.TYPE_ALERT ) )
                    {
                        groupDescription = description.getValue( );
                        break;
                    }
                }

                subscriptionGroup.put( "groupname", subscriptionsByGroup.getKey( ) );
                subscriptionGroup.put( "description", groupDescription );
                subscriptionGroup.put( "subscriptions", groupSubscriptionsList );

                subscriptionGroupList.add( subscriptionGroup );
            }
        }

        allSubscriptions.put( "user_subscriptions", subscriptionGroupList );

        return mapper.writeValueAsString( allSubscriptions );
    }

    private Map<String, String> getSubscriptionsDescription( )
    {
        Map<String, String> descriptions = new HashMap<String, String>( );

        List<String> descriptionsList = AppPropertiesService.getKeys( CONSTANT_SUB_DESCRIPTION_PREFIX );
        for ( String description : descriptionsList )
        {
            descriptions.put( description.substring( CONSTANT_SUB_DESCRIPTION_PREFIX.length( ) + 1 ), AppPropertiesService.getProperty( description ) );
        }

        return descriptions;
    }

    public Map<String, Boolean> getUserSubscribtionsToUpdate( String jsonSubscriptions ) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper( );
        Map<String, Boolean> subscriptionsToUpdate = new HashMap<>( );

        try
        {
            JsonNode jsonNodes = mapper.readTree( jsonSubscriptions );

            JsonNode jsonUserSubscriptionList = jsonNodes.get( "userSubscriptions" );

            for ( JsonNode subscriptionsByTypeNode : jsonUserSubscriptionList )
            {
                JsonNode subscriptionsByGrList = subscriptionsByTypeNode.get( "subscriptionsByGroup" );

                for ( JsonNode subscriptionsByGr : subscriptionsByGrList )
                {
                    JsonNode subscriptionsList = subscriptionsByGr.get( "subscriptionsList" );

                    for ( JsonNode jsonSubscription : subscriptionsList )
                    {
                        // Build Map (Name / Status) of subscriptions to update
                        subscriptionsToUpdate.put( jsonSubscription.get( "id" ).asText( ).replace( "_", " " ),
                                Boolean.valueOf( jsonSubscription.get( "active" ).asText( ) ) );
                    }
                }
            }
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
     * 
     * @throws Exception
     */
    private void initMapIdName( ) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper( );

        String subscriptionsInJson = _dolistAPI.getAllSubscriptions( DolistConstants.TYPE_SUBSCRIPTION );
        String interestsInJson = _dolistAPI.getAllSubscriptions( DolistConstants.TYPE_INTEREST );

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
            String strError = "Error occured while getting the list of interests ids and names.";
            AppLogService.error( strError + e.getMessage( ), e );
        }
    }

    /**
     * get map from sub datas
     * 
     * @param sub
     * @return the map
     */
    public Map<String, String> subToMap( Subscription sub, Map<String, String> mapIdName )
    {
        Map<String, String> mapSubData = new HashMap<>( );
        String subscriptionDolistId = StringUtils.EMPTY;

        for ( Map.Entry<String, String> subMapIdName : _subscriptionsMapIdName.entrySet( ) )
        {
            if ( subMapIdName.getValue( ).equals( sub.getName( ) ) )
            {
                subscriptionDolistId = subMapIdName.getKey( );
                continue;
            }
        }

        if ( sub.isActive( ) )
            mapSubData.put( subscriptionDolistId, JSON_NODE_SUB_SUBSCRIBED );
        else
            mapSubData.put( subscriptionDolistId, JSON_NODE_SUB_UNSUBSCRIBED );

        return mapSubData;
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

}
