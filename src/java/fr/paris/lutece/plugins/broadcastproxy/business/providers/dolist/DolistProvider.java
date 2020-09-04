/*
 * Copyright (c) 2002-2020, City of Paris
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

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
    private static final String JSON_NODE_GROUP_NAME = AppPropertiesService.getProperty( "dolist.jsonNode.group.Name" );
    private static final String JSON_NODE_SUB_NAME = AppPropertiesService.getProperty( "dolist.jsonNode.sub.Name" );
    private static final String JSON_NODE_SUB_SUBSCRIBED = AppPropertiesService.getProperty( "dolist.jsonNode.sub.Subscribed" );
    private static final String JSON_NODE_SUB_UNSUBSCRIBED = AppPropertiesService.getProperty( "dolist.jsonNode.sub.Unsubscribed" );

    private static final String CONSTANTE_OPERATION_MODE_ADD = AppPropertiesService.getProperty( "dolist.CONSTANTE_OPERATION_MODE_ADD" );
    private static final String CONSTANTE_OPERATION_MODE_DELETE = AppPropertiesService.getProperty( "dolist.CONSTANTE_OPERATION_MODE_DELETE" );

    // instance variables
    private DolistAPI _dolistAPI;

    private Map<String, String> _groupsMapIdName = new HashMap<>( );
    private Map<String, String> _interestsMapIdName = new HashMap<>( );
    private Map<String, String> _subscriptionsMapIdName = new HashMap<>( );

    /**
     * Constructor
     */
    public DolistProvider( )
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

        Map<String, Map<String, List<Subscription>>> userSubscriptions = getUserSubscriptionsByGroup( userDolistSubscriptionsList, userId );

        return userSubscriptions;
    }

    @Override
    public String getUserSubscriptionsAsJson( String userId ) throws Exception
    {
        Map<String, Map<String, List<Subscription>>> userSubscriptions = getUserSubscriptionsAsList( userId );

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
                _dolistAPI.updateInterests( userId, interestsToAdd, CONSTANTE_OPERATION_MODE_ADD );
            }

            if ( !interestsToDelete.isEmpty( ) )
            {
                _dolistAPI.updateInterests( userId, interestsToDelete, CONSTANTE_OPERATION_MODE_DELETE );
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
                    String subscriptionType = StringUtils.EMPTY;
                    List<Subscription> subscriptionsList = new ArrayList<>( );
                    String groupName = subscriptionsNamesByGroup.getKey( );

                    if ( groupName.equals( "Alertes" ) )
                    {
                        subscriptionType = Constants.TYPE_ALERT;
                    }
                    else
                    {
                        subscriptionType = Constants.TYPE_NEWSLETTER;
                    }

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

                    if ( subscriptionsList.size( ) == 1 && subscriptionsList.get( 0 ).getName( ).equals( groupName ) )
                    {
                        groupName = DolistConstants.NO_GROUP_NAME;
                    }

                    if ( subscriptionType.equals( Constants.TYPE_NEWSLETTER ) )
                    {
                        if ( userNewsletters.containsKey( groupName ) )
                        {
                            subscriptionsList.addAll( userNewsletters.get( groupName ) );
                        }

                        userNewsletters.put( groupName, subscriptionsList );
                    }
                    else
                        if ( subscriptionType.equals( Constants.TYPE_ALERT ) )
                        {
                            if ( userAlerts.containsKey( groupName ) )
                            {
                                subscriptionsList.addAll( userAlerts.get( groupName ) );
                            }

                            userAlerts.put( groupName, subscriptionsList );
                        }
                }
            }

            userSubscriptions.put( Constants.TYPE_NEWSLETTER, userNewsletters );
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

                if ( groupName.substring( 0, 1 ).equals( "[" ) && groupName.substring( groupName.length( ) - 1, groupName.length( ) ).equals( "]" ) )
                {
                    groupName = groupName.substring( 1, groupName.length( ) - 1 );

                    List<String> SubscriptionsNamesList = new ArrayList<String>( );
                    for ( JsonNode node : itemNode.get( JSON_NODE_ITEM_INTERESTLIST ) )
                    {
                        SubscriptionsNamesList.add( node.get( JSON_NODE_INTEREST_NAME ).asText( ) );
                    }

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

        try
        {
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
                        if ( itemNode.get( JSON_NODE_ITEM_STATUS ).asText( ).equals( JSON_NODE_SUB_SUBSCRIBED ) )
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
        String jsonSubscriptions = StringUtils.EMPTY;
        List<String> jsonSubByGrList = new ArrayList<String>( );

        for ( Map.Entry<String, Map<String, List<Subscription>>> subscriptionsByGroup : userSubscriptions.entrySet( ) )
        {
            if ( subscriptionsByGroup.getKey( ) != null )
            {
                for ( Map.Entry<String, List<Subscription>> subscriptionsList : subscriptionsByGroup.getValue( ).entrySet( ) )
                {
                    String jsonSubList = mapper.writeValueAsString( subscriptionsList.getValue( ) );

                    String jsonSubGr = "{\"groupName\":" + subscriptionsByGroup.getKey( ) + ",\"subscriptionsList\":" + jsonSubList + "}";

                    jsonSubByGrList.add( jsonSubGr );
                }
            }
        }

        jsonSubscriptions = "{\"userSubscriptions\":" + mapper.writeValueAsString( jsonSubByGrList ) + "}";

        return jsonSubscriptions;
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
     */
    private void initMapIdName( )
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
                _subscriptionsMapIdName.put( node.get( "ID" ).asText( ), node.get( JSON_NODE_SUB_NAME ).asText( ) );
            }

            // Get interests data (id and name)
            nodes = null;
            itemListNode = null;

            nodes = mapper.readTree( interestsInJson );

            itemListNode = nodes.get( JSON_NODE_ITEMLIST );

            for ( JsonNode node : itemListNode )
            {
                JsonNode groupData = node.get( JSON_NODE_ITEM_GROUP );
                _groupsMapIdName.put( groupData.get( "ID" ).asText( ), groupData.get( JSON_NODE_GROUP_NAME ).asText( ) );

                JsonNode intersts = node.get( JSON_NODE_ITEM_INTERESTLIST );
                for ( JsonNode interest : intersts )
                {
                    _interestsMapIdName.put( interest.get( "ID" ).asText( ), interest.get( JSON_NODE_INTEREST_NAME ).asText( ) );
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
