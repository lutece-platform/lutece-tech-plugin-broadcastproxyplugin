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
package fr.paris.lutece.plugins.broadcastproxy.business.providers.hubscore;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.paris.lutece.plugins.broadcastproxy.business.Feed;
import java.util.HashMap;
import java.util.Map;

import fr.paris.lutece.plugins.broadcastproxy.business.IBroadcastProvider;
import fr.paris.lutece.plugins.broadcastproxy.business.Subscription;
import fr.paris.lutece.plugins.broadcastproxy.service.Constants;
import fr.paris.lutece.plugins.referencelist.service.ReferenceListService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceList;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;

public class HubScoreProvider implements IBroadcastProvider
{

    // Constants
    private static final String PROVIDER_NAME = "HubScore";
    private static final String CONSTANT_KEY_RECORDS = "records";
    private static final String HUBSCORE_BROADCASTPROXY_LABELS = "HUBSCORE_BROADCASTPROXY_LABELS";

    // instance variables
    private HubScoreAPI _hubScoreAPI;

    /**
     * Constructor
     */
    public HubScoreProvider( )
    {
        _hubScoreAPI = new HubScoreAPI( );
    }

    @Override
    public String getName( )
    {
        return PROVIDER_NAME;
    }

    @Override
    public boolean subscribe( String userId, String subscriptionId, String typeSubsciption ) throws Exception
    {

        try
        {
            _hubScoreAPI.manageUser( userId, Constants.TYPE_NEWSLETTER, Constants.ACTION_ADD, false );
        }
        catch( Exception e )
        {
            // try with a new token
            _hubScoreAPI.manageUser( userId, Constants.TYPE_NEWSLETTER, Constants.ACTION_ADD, true );
        }
        return true;
    }

    @Override
    public boolean unsubscribe( String userName, String subscriptionId, String typeSubscription ) throws Exception
    {
        Subscription sub = new Subscription( );
        sub.setUserName( userName );
        sub.setId( subscriptionId );
        sub.setType( typeSubscription );

        return update( sub );
    }

    @Override
    public boolean update( Subscription sub ) throws Exception
    {
        _hubScoreAPI.updateSubscribtions( sub.getUserId( ), subToMap( sub ), sub.getType( ), false );

        return true;
    }

    /**
     * get map from sub datas
     * 
     * @param sub
     * @return the map
     */
    public Map<String, String> subToMap( Subscription sub )
    {
        Map<String, String> mapDatas = new HashMap<>( );

        String name = sub.getId( );

        if ( !name.startsWith( "Optin_" ) )
            name = "Optin_" + name;
        String active = ( sub.isActive( ) ? "1" : "0" );
        mapDatas.put( name, active );

        // add date of update
        if ( sub.isActive( ) )
        {
            String key = "Date_Consentement_" + name.substring( 6 );
            mapDatas.put( key, getFormattedCurrentLocaleDateTime( ) );
        }
        else
        {

            String key = "Date_Desinscription_" + name.substring( 6 );

            if ( sub.getType( ).equals( Constants.TYPE_NEWSLETTER ) )
                key = "Date_Dsinscription_" + name.substring( 6 ); // !!!

            mapDatas.put( key, getFormattedCurrentLocaleDateTime( ) );
        }

        // add themes
        if ( sub.getData( ) != null && sub.getData( ).size( ) > 0 )
        {
            String values = "";
            boolean isFirst = true;

            for ( String theme : sub.getData( ).keySet( ) )
            {
                if ( sub.getData( ).get( theme ).equals( "1" ) )
                {
                    if ( !isFirst )
                        values += ",";
                    values += theme;

                    isFirst = false;
                }
            }

            String feedName = name.substring( 6 ); // name without "Optin_"

            // add themes
            mapDatas.put( feedName, values );
        }

        return mapDatas;
    }

    @Override
    public List<Subscription> getUserSubscriptionsAsList( String userId, String typeSubscription ) throws Exception
    {
        String userSubscriptionsList = null;

        try
        {
            userSubscriptionsList = _hubScoreAPI.getUserSubscriptions( userId, typeSubscription, false );
        }
        catch( Exception e )
        {
            // try with new token
            userSubscriptionsList = _hubScoreAPI.getUserSubscriptions( userId, typeSubscription, true );
        }

        return buildSubscriptionList( userSubscriptionsList, userId, typeSubscription );
    }

    @Override
    public String getUserSubscriptionsAsJson( String userId, String typeSubscription ) throws Exception
    {
        String userSubscriptionsList = null;

        try
        {
            userSubscriptionsList = _hubScoreAPI.getUserSubscriptions( userId, typeSubscription, false );
        }
        catch( Exception e )
        {
            // try with new token
            userSubscriptionsList = _hubScoreAPI.getUserSubscriptions( userId, typeSubscription, true );
        }

        return buildJson( buildSubscriptionList( userSubscriptionsList, userId, typeSubscription ) );
    }

    @Override
    public boolean updateSubscribtions( List<Subscription> subscriptionsList ) throws Exception
    {

        Map<String, String> mapDatas = new HashMap<>( );
        String feedType ;
        String userId ;

        if ( subscriptionsList.isEmpty( ) ) return false;

        for ( Subscription sub : subscriptionsList )
        {
            mapDatas.putAll( subToMap( sub ) );
        }

        _hubScoreAPI.updateSubscribtions( subscriptionsList.get(0).getUserId( ), mapDatas, subscriptionsList.get(0).getType( ), false );

        return true;
    }

    /**
     * Build JSON response from subscription beans list
     * 
     * @param the
     *            list
     * @return a JSON String
     */
    private String buildJson( List<Subscription> subList ) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper( );
        ObjectNode jsonResult = mapper.createObjectNode( );

        String jsonList = mapper.writeValueAsString( subList );

        jsonResult.putPOJO( "subscriptions", jsonList );

        return jsonResult.toString( );
    }

    /**
     * Parse JSON response as subscription beans
     * 
     * @param jsonResponse
     * @return a JSON String
     */
    private List<Subscription> buildSubscriptionList( String jsonResponse, String userId, String typeSubscription ) throws IOException
    {

        ObjectMapper mapper = new ObjectMapper( );
        List<Subscription> subscriptionList = new ArrayList<>( );

        JsonNode jsonNode = mapper.readTree( jsonResponse );
        ArrayNode arrayRecordsNode = (ArrayNode) jsonNode.get( CONSTANT_KEY_RECORDS );

        if ( arrayRecordsNode != null && arrayRecordsNode.size( ) > 0 )
        {
            Iterator<Map.Entry<String, JsonNode>> fieldsIterator = arrayRecordsNode.get( 0 ).fields( );
            while ( fieldsIterator.hasNext( ) )
            {
                Map.Entry<String, JsonNode> field = fieldsIterator.next( );

                String key = field.getKey( );

                // subscription name key must start with "Optin_"
                if ( key.startsWith( "Optin_" ) )
                {
                    String subId = key.substring( 6 );
                    String subState = field.getValue( ).asText( );

                    Subscription sub = new Subscription( );

                    sub.setId( subId );
                    sub.setActive( "1".equals( subState ) );
                    sub.setUserId( userId );
                    sub.setType( typeSubscription );

                    if ( arrayRecordsNode.get( 0 ).get( subId ) != null )
                    {
                        String data = arrayRecordsNode.get( 0 ).get( subId ).asText( );
                        String [ ] themes = data.split( "," );

                        Map<String, String> themeList = new HashMap<>( );
                        for ( String theme : themes )
                        {
                            themeList.put( theme, theme );
                        }

                        sub.setData( themeList );
                    }

                    subscriptionList.add( sub );
                }
            }
        }

        return subscriptionList;
    }

    /**
     * Parse JSON response like :
     * 
     * {"count":"1","records":[{"id":"123456","Email":"XXX.YYY@ZZZ.com","Optin_Paris":"1","Date_Consentement_Paris":"2019-06-07 14:34:45","Optin_QFAP":"1",
     * "Date_Consentement_QFAP"
     * :"2019-06-07 14:34:45","Optin_Alerte":"1","Alerte":"parcs_et_jardins,Paris_sport_vacances","Date_Consentement_Alerte":"2019-06-07 14:34:45"
     * ,"hubBlacklist":false}]}
     * 
     * and build generic MAP response as pairs of (id,name)
     * 
     * @param jsonResponse
     * @return a JSON String
     */
    private Map<String, String> buildMap( String jsonResponse ) throws IOException
    {
        Map<String, String> map = new HashMap<>( );
        ObjectMapper mapper = new ObjectMapper( );
        JsonNode jsonNode = null;

        jsonNode = mapper.readTree( jsonResponse );

        ArrayNode arrayNode = (ArrayNode) jsonNode.get( CONSTANT_KEY_RECORDS );

        if ( arrayNode != null && arrayNode.size( ) > 0 )
        {
            Iterator<Map.Entry<String, JsonNode>> fieldsIterator = arrayNode.get( 0 ).fields( );
            while ( fieldsIterator.hasNext( ) )
            {
                Map.Entry<String, JsonNode> field = fieldsIterator.next( );

                String key = field.getKey( );

                // subscription name key must start with "Optin_"
                if ( key.startsWith( "Optin_" ) )
                {
                    String subName = key;
                    String subState = field.getValue( ).asText( );
                    map.put( subName, subState );
                }

                // Patricular case : "Alerte" subscription has datas
                if ( key.equals( "Alerte" ) )
                {
                    String subName = key;
                    String subState = field.getValue( ).asText( );
                    map.put( subName, subState );
                }

            }
        }

        return map;
    }

    /**
     * get formatted current locale date time
     * 
     * @return the formatted date
     */
    private String getFormattedCurrentLocaleDateTime( )
    {
        LocalDateTime now = LocalDateTime.now( );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" );

        return now.format( formatter );
    }

    @Override
    public List<Feed> getFeeds( )
    {
        List<Feed> list = new ArrayList<>( );

        /*
         * example : broadcastproxy.hubscore.feedsType=ALERT,NEWSLETTER broadcastproxy.hubscore.feeds.type.ALERT=Paris,QFAP,Alerte
         * broadcastproxy.hubscore.feeds.type.NEWSLETTER=Budget_Participatif,Carte_Citoyenne,Nuit_Debats,Lettre_Climat,Quartier_Populaire,asso.paris
         * broadcastproxy
         * .hubscore.feeds.type.ALERT.Alerte.data==ateliers_beaux_arts,bmo,circulation,CMA,collecte_des_dechets,conservatoires,elections,parcs_et_jardins
         * ,Paris_sport_vacances,senior_plus,stationnement_residentiel,universite_permanente,vacances_arc_en_ciel
         */

        String [ ] feedsTypes = AppPropertiesService.getProperty( "broadcastproxy.hubscore.feedsType", "" ).split( "," );

        // get labels in reference lists
        ReferenceList labelList = ReferenceListService.getInstance( ).getReferenceList( HUBSCORE_BROADCASTPROXY_LABELS, "fr" );
        Map<String, String> labelsMap = ( labelList != null ? labelList.toMap( ) : new HashMap<>( ) );

        for ( String feedType : feedsTypes )
        {
            String [ ] feedIds = AppPropertiesService.getProperty( "broadcastproxy.hubscore.feeds.type." + feedType, "" ).split( "," );
            for ( String feedId : feedIds )
            {
                String label = labelsMap.get( feedType + "." + feedId );
                Feed feed = new Feed( feedId, ( label != null ? label : feedId ), feedType );

                String description = labelsMap.get( feedType + "." + feedId + ".description" );

                if ( description != null )
                    feed.setDescription( description );

                String datas [ ] = AppPropertiesService.getProperty( "broadcastproxy.hubscore.feeds.type." + feedType + "." + feedId + ".data", "" )
                        .split( "," );
                if ( datas.length > 0 )
                {
                    Map<String, String> mapData = new HashMap<>( );
                    for ( String data : datas )
                    {
                        String dataLabel = labelsMap.get( feedType + "." + feedId + ".data." + data );
                        if ( !StringUtils.isBlank( data ) )
                            mapData.put( data, ( label != null ? dataLabel : data ) );
                    }
                    feed.setData( mapData );
                }

                list.add( feed );
            }
        }

        return list;
    }

}
