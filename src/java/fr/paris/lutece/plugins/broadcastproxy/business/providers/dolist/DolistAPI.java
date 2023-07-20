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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

public class DolistAPI
{

    // URLs
    private static final String PROPERTY_ACCOUNT_ID = AppPropertiesService.getProperty( "dolist.CONSTANTE_ACCOUNT_ID" );
    private static final String URL_BASE_API = AppPropertiesService.getProperty( "dolist.URL_PATH_BASE_API" );
    private static final String URL_CONTACTS = AppPropertiesService.getProperty( "dolist.URL_PATH_CONTACTS" );
    private static final String URL_EXISTS = AppPropertiesService.getProperty( "dolist.URL_PATH_EXISTS" );
    private static final String URL_ACCOUNT_ID = AppPropertiesService.getProperty( "dolist.URL_PATH_ACCOUNT_ID" );
    private static final String URL_SUBSRIPTIONS = AppPropertiesService.getProperty( "dolist.URL_PATH_SUBSRIPTIONS" );
    private static final String URL_INTERESTS = AppPropertiesService.getProperty( "dolist.URL_PATH_INTERESTS" );
    private static final String URL_CHANNEL = AppPropertiesService.getProperty( "dolist.URL_PATH_CHANNEL" );
    private static final String URL_GROUP_INTERESTS = AppPropertiesService.getProperty( "dolist.URL_PATH_GROUP_INTERESTS" );
    private static final String URL_OPERATION_MODE = AppPropertiesService.getProperty( "dolist.URL_PATH_OPERATION_MODE" );
    private static final String URL_CONTACT_INTEREST_ORIGIN = AppPropertiesService.getProperty( "dolist.URL_PATH_CONTACT_INTEREST_ORIGIN" );
    private static final String URL_INTERESTS_ACTIVE_ONLY = AppPropertiesService.getProperty( "dolist.URL_PATH_INTERESTS_ACTIVE_ONLY" );

    // Markers
    private static final String MARK_HEADER_CONTENT_TYPE = AppPropertiesService.getProperty( "dolist.MARK_HEADER_CONTENT_TYPE" );
    private static final String MARK_HEADER_ACCEPT = AppPropertiesService.getProperty( "dolist.MARK_HEADER_ACCEPT" );
    private static final String MARK_HEADER_ACCEPT_LANGUAGE = AppPropertiesService.getProperty( "dolist.MARK_HEADER_ACCEPT_LANGUAGE" );
    private static final String MARK_HEADER_X_API_KEY = AppPropertiesService.getProperty( "dolist.MARK_HEADER_X_API_KEY" );

    // Header constants
    private static final String CONSTANTE_HEADER_CONTENT_TYPE = AppPropertiesService.getProperty( "dolist.CONSTANTE_HEADER_CONTENT_TYPE" );
    private static final String CONSTANTE_HEADER_ACCEPT = AppPropertiesService.getProperty( "dolist.CONSTANTE_HEADER_ACCEPT" );
    private static final String CONSTANTE_HEADER_ACCEPT_LANGUAGE = AppPropertiesService.getProperty( "dolist.CONSTANTE_HEADER_ACCEPT_LANGUAGE" );
    private static final String CONSTANTE_HEADER_X_API_KEY = AppPropertiesService.getProperty( "dolist.CONSTANTE_HEADER_X_API_KEY" );
    private static final String CONSTANTE_HEADER_X_API_KEY_PREFIX = "dolist.CONSTANTE_HEADER_X_API_KEY_PARIS_";
    
    // URL parameter's constants
    private static final String CONSTANTE_CHANNEL = AppPropertiesService.getProperty( "dolist.CONSTANTE_CHANNEL" );
    private static final String CONSTANTE_EMAIL_FIELD_ID = AppPropertiesService.getProperty( "dolist.CONSTANTE_EMAIL_FIELD_ID" );
    private static final String CONSTANTE_CONTACT_INTEREST_ORIGIN = AppPropertiesService.getProperty( "dolist.CONSTANTE_CONTACT_INTEREST_ORIGIN" );
    private static final String CONSTANTE_INTERESTS_ACTIVE_ONLY = AppPropertiesService.getProperty( "dolist.CONSTANTE_INTERESTS_ACTIVE_ONLY" );

    private static final String CONSTANTE_REQUEST_BODY_CONTACT = AppPropertiesService.getProperty( "dolist.CONSTANTE_REQUEST_BODY_CONTACT" );
    private static final String CONSTANTE_REQUEST_BODY_COMPOSITE_LIST = AppPropertiesService.getProperty( "dolist.CONSTANTE_REQUEST_BODY_COMPOSITE_LIST" );
    private static final String CONSTANTE_REQUEST_BODY_SUBSCRIPTIONS_LIST = AppPropertiesService
            .getProperty( "dolist.CONSTANTE_REQUEST_BODY_SUBSCRIPTIONS_LIST" );
    private static final String CONSTANTE_REQUEST_BODY_INTERESTS_LIST = AppPropertiesService.getProperty( "dolist.CONSTANTE_REQUEST_BODY_INTERESTS_LIST" );

    // Other constants
    private static final String JSON_NODE_USER_ID = AppPropertiesService.getProperty( "dolist.jsonNode.user.ID" );
    private static final String JSON_NODE_SEARCH_NAME = AppPropertiesService.getProperty( "dolist.jsonNode.item.Name" );
    private static final String JSON_NODE_FIELD_LIST = AppPropertiesService.getProperty( "dolist.jsonNode.FieldList" );

    // Instance variables
    private String _userEmail;
    private String _contactId;

    /**
     * get ContactID
     * 
     * @param userEmail
     * @param strAccountId
     * @return Dolist contact id
     * @throws Exception
     */
    public String getDolistContactId( String userEmail, String strAccountId ) throws Exception
    {
        if ( userEmail == null )
            return null;        

        if ( userEmail.equals( _userEmail ) )
            return _contactId;

        ObjectMapper mapper = new ObjectMapper( );
        Map<String, Object> queryParams = new HashMap<>( );
        String strResponse = null;
        String strContactId = "";

        String strUrl = URL_BASE_API + URL_CONTACTS + URL_EXISTS + "?" + URL_ACCOUNT_ID + "=" + strAccountId;

        try
        {
            // Set Headers
            Map<String, String> mapHeaders = constructHeader( strAccountId );

            // Set request parameters
            queryParams.put( JSON_NODE_SEARCH_NAME, userEmail );
            queryParams.put( JSON_NODE_USER_ID, Integer.parseInt( CONSTANTE_EMAIL_FIELD_ID ) );

            String strParamsInJson = "{ \"" + CONSTANTE_REQUEST_BODY_COMPOSITE_LIST + "\":{\"FieldValueList\":[" + mapper.writeValueAsString( queryParams ) + "]}}";

            // Call Dolist API
            strResponse = callDoPost( strUrl, strParamsInJson, mapHeaders );

            // Get ContactId from response
            JsonNode nodes = mapper.readTree( strResponse );

            if ( Integer.parseInt( nodes.get( JSON_NODE_USER_ID ).asText( ) ) > 0 )
            {
                strContactId = nodes.get( JSON_NODE_USER_ID ).asText( );
                if ( strContactId == null || strContactId.isEmpty( ) )
                {
                    throw new Exception( );
                }

                // set instance variables
                _userEmail = userEmail;
                _contactId = strContactId;
            }
//            else if ( Integer.parseInt( nodes.get( "Count" ).asText( ) ) > 1 ) // There is some accounts with the same email
//            {
//            	String strError = "There is some accounts with the same email : '" + userEmail;
//                AppLogService.error( strError );
//                return null;
//            }
            else // There is not account for this user
            {
                return "";            	
            }

        }
        catch( Exception e )
        {
            String strError = "Error occured while getting Contact ID from '" + strUrl + "' : " + e.getMessage( );
            AppLogService.error( strError + e.getMessage( ), e );
            throw new Exception( strError );
        }

        return strContactId;
    }

    /**
     * ADD user
     * 
     * @param eMail
     * @param strAccountId
     * @throws IOException
     */
    public String addUser( String userEmail, String strAccountId ) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper( );
        Map<String, String> param = new HashMap<>( );
        List<Map<String, String>> paramsList = new ArrayList<Map<String, String>>( );
        String strResponse = null;
        String strContactId = "";

        // Set URL
        String strUrl = URL_BASE_API + URL_CONTACTS + "?" + URL_ACCOUNT_ID + "=" + strAccountId;

        try
        {
            // Set Headers
            Map<String, String> mapHeaders = constructHeader( strAccountId );

            param.put( "ID", CONSTANTE_EMAIL_FIELD_ID );
            param.put( "Value", userEmail );

            paramsList.add( param );

            String strParamsInJson = "{ \"" + CONSTANTE_REQUEST_BODY_CONTACT + "\":{\"" + JSON_NODE_FIELD_LIST + "\":" + mapper.writeValueAsString( paramsList )
                    + "}}";

            // Call Dolist API
            strResponse = callDoPost( strUrl, strParamsInJson, mapHeaders );

            // Get ContactId from response
            JsonNode nodes = mapper.readTree( strResponse );

            strContactId = nodes.get( JSON_NODE_USER_ID ).asText( );
        }
        catch( IOException | HttpAccessException e )
        {
            String strError = "Error occured while add/delete user. '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage( ), e );
            return null;
        }

        // set instance variables
        _userEmail = userEmail;
        _contactId = strContactId;

        return strContactId;
    }

    /**
     * get subscriptions Names (label)
     * 
     * @param typeSubscription
     * @param strAccountId
     * @return json
     * @throws Exception
     */
    public String getAllSubscriptions( String typeSubscription, String strAccountId ) throws Exception
    {
        String strResponse = StringUtils.EMPTY;
        String strUrl = StringUtils.EMPTY;

        try
        {
            if ( typeSubscription.equals( DolistConstants.TYPE_SUBSCRIPTION ) )
            {
                return getSubJson( );
                // Get dolist newsletters ids and names (Dolist subscriptions)
                //strUrl = URL_BASE_API + URL_SUBSRIPTIONS + "?" + URL_CHANNEL + "=" + CONSTANTE_CHANNEL + "&" + URL_ACCOUNT_ID + "=" + strAccountId;
            }
            else
                if ( typeSubscription.equals( DolistConstants.TYPE_INTEREST ) )
                {
                    return getInteretJson( );
                    // Get dolist alerts ids and names (Dolist interests)
//                    strUrl = URL_BASE_API + URL_INTERESTS + URL_GROUP_INTERESTS + "?" + URL_ACCOUNT_ID + "=" + strAccountId + "&"
//                            + URL_INTERESTS_ACTIVE_ONLY + "=" + CONSTANTE_INTERESTS_ACTIVE_ONLY;
                }

            Map<String, String> mapHeaders = constructHeader( strAccountId );

            strResponse = callDoGet( strUrl, mapHeaders );
        }
        catch( IOException e )
        {
            String strError = "Error occured while getting the list of subscriptions names '" + strUrl + "' : " + e.getMessage( );
            AppLogService.error( strError + e.getMessage( ), e );
            throw new Exception( strError );
        }

        return strResponse;
        
    }

    /**
     * get Contact's subscriptions
     * 
     * @param userEmail
     * @param strAccountId
     * @return json
     * @throws Exception
     */
    public String getUserSubscriptions( String userEmail, String typeSubscription, String strAccountId ) throws Exception
    {
        String strResponse = StringUtils.EMPTY;
        String strUrl = StringUtils.EMPTY;

        // Get contact ID
        String idContact = getDolistContactId( userEmail, strAccountId );

        if ( idContact == null )
        	return null;
        
        // if Email (user) does not exist
        if ( idContact.equals( "" ) )
        	return "";

        try
        {
            if ( typeSubscription.equals( DolistConstants.TYPE_SUBSCRIPTION ) )
            {
                // Get user newsletters (Dolist subscriptions)
                strUrl = URL_BASE_API + URL_CONTACTS + "/" + idContact + URL_SUBSRIPTIONS + "?" + URL_ACCOUNT_ID + "=" + strAccountId + "&"
                        + URL_CHANNEL + "=" + CONSTANTE_CHANNEL;
            }
            else
                if ( typeSubscription.equals( DolistConstants.TYPE_INTEREST ) )
                {
                    // Get user alerts (Dolist interests)
                    strUrl = URL_BASE_API + URL_CONTACTS + "/" + idContact + URL_INTERESTS + "?" + URL_ACCOUNT_ID + "=" + strAccountId;
                }

            Map<String, String> mapHeaders = constructHeader( strAccountId );

            strResponse = callDoGet( strUrl, mapHeaders );

        }
        catch( IOException e )
        {
            String strError = "Error occured while getting Contact subscriptions list from '" + strUrl + "' : " + e.getMessage( );
            AppLogService.error( strError + e.getMessage( ), e );
            throw new Exception( strError );
        }

        return strResponse;
        
    }

    /**
     * update subscriptions
     * 
     * @param ContactName
     * @param mapSubscriptions
     * @param typeSubscription
     * @param strAccountId
     * @return response
     * @throws Exception
     */
    public String updateSubscribtions( String userEmail, Map<String, String> subscriptionsToUpdate, String strAccountId ) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper( );
        List<Map<String, Object>> listModifiedSubscriptions = new ArrayList<>( );

        // Get dolist contact ID
        String userDolistId = getDolistContactId( userEmail, strAccountId );

        // if Email (user) does not exist ==> Create user
        if ( userDolistId.equals( "" ) )
            userDolistId = addUser( userEmail, strAccountId );

        // Set Headers
        Map<String, String> mapHeaders = constructHeader( strAccountId );

        // Update Dolist Subscriptions
        String strUrl = URL_BASE_API + URL_CONTACTS + "/" + userDolistId + URL_SUBSRIPTIONS + "?" + URL_ACCOUNT_ID + "=" + strAccountId;

        // Set request parameters
        for ( Map.Entry<String, String> subStatus : subscriptionsToUpdate.entrySet( ) )
        {
            Map<String, Object> sub = new HashMap<>( );
            sub.put( "SubscriptionID", Integer.parseInt( subStatus.getKey( ) ) );
            sub.put( "Status", subStatus.getValue( ) );
            listModifiedSubscriptions.add( sub );
        }

        String strParamsInJson = "{ \"" + CONSTANTE_REQUEST_BODY_SUBSCRIPTIONS_LIST + "\":" + mapper.writeValueAsString( listModifiedSubscriptions ) + "}";

        // Call http method (PUT)
        String response = callDoPut( strUrl, strParamsInJson, mapHeaders );

        return response;
    }

    public String updateInterests( String userEmail, List<Integer> subscriptionsToUpdate, String action, String strAccountId ) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper( );
        String response = StringUtils.EMPTY;

        // Get dolist contact ID
        String userDolistId = getDolistContactId( userEmail, strAccountId );
        
        // if Email (user) does not exist ==> Create user
        if ( userDolistId.equals( "" ) )
            userDolistId = addUser( userEmail, strAccountId );

        // Set Headers
        Map<String, String> mapHeaders = constructHeader( strAccountId );

        String strUrl = URL_BASE_API + URL_CONTACTS + "/" + userDolistId + URL_INTERESTS + "?" + URL_CONTACT_INTEREST_ORIGIN + "="
                + CONSTANTE_CONTACT_INTEREST_ORIGIN + "&" + URL_OPERATION_MODE + "=" + action + "&" + URL_ACCOUNT_ID + "=" + strAccountId;

        try
        {
            // Update Dolist Interests
            String strParamsInJson = "{\"" + CONSTANTE_REQUEST_BODY_INTERESTS_LIST + "\":" + mapper.writeValueAsString( subscriptionsToUpdate ) + "}";

            // Call http method (PUT)
            response = callDoPut( strUrl, strParamsInJson, mapHeaders );

        }
        catch( IOException e )
        {
            String strError = "Error connecting to '" + strUrl + "' : " + e.getMessage( );
            AppLogService.error( strError + e.getMessage( ), e );
            throw new Exception( strError );
        }

        return response;
    }

    /*** HTTP METHODS ***/

    /**
     * call get method
     * 
     * @param strUrl
     * @param mapHeaders
     * @param typeSubscription
     * @return the response message
     * @throws IOException
     */
    private String callDoGet( String strUrl, Map<String, String> mapHeaders ) throws IOException
    {
        HttpAccess httpAccess = new HttpAccess( );

        String strResponse = StringUtils.EMPTY;

        try
        {
            strResponse = httpAccess.doGet( strUrl, null, null, mapHeaders );
        }
        catch( HttpAccessException e )
        {
            AppLogService.error( "Returned Dolist error : " + strResponse );
        }

        return strResponse;
    }

    /**
     * call post method
     * 
     * @param strUrl
     * @param params
     * @param mapHeaders
     * @return true or false
     * @throws IOException
     * @throws HttpAccessException
     */
    private String callDoPost( String strUrl, String jsonParams, Map<String, String> mapHeaders ) throws IOException, HttpAccessException
    {
        DolistHttpAccess dlhttpaccess = new DolistHttpAccess( );

        return dlhttpaccess.doPost( strUrl, jsonParams, mapHeaders );
    }

    /**
     * call post method
     * 
     * @param strUrl
     * @param params
     * @param mapHeaders
     * @return true or false
     * @throws IOException
     * @throws HttpAccessException
     */
    private String callDoPut( String strUrl, String jsonParams, Map<String, String> mapHeaders ) throws IOException, HttpAccessException
    {
        DolistHttpAccess dlhttpaccess = new DolistHttpAccess( );

        return dlhttpaccess.doPut( strUrl, jsonParams, mapHeaders );
    }

    private Map<String, String> constructHeader( String strAccountId )
    {
        Map<String, String> mapHeader = new HashMap<>( );
        
        String strApiKey = CONSTANTE_HEADER_X_API_KEY;
        if ( StringUtils.isNotEmpty( strAccountId ) && !strAccountId.equals( PROPERTY_ACCOUNT_ID )  )
        {
            strApiKey = AppPropertiesService.getProperty( CONSTANTE_HEADER_X_API_KEY_PREFIX + strAccountId  ) ;
        }
        
        mapHeader.put( MARK_HEADER_X_API_KEY, strApiKey );
        mapHeader.put( MARK_HEADER_CONTENT_TYPE, CONSTANTE_HEADER_CONTENT_TYPE );
        mapHeader.put( MARK_HEADER_ACCEPT, CONSTANTE_HEADER_ACCEPT );
        mapHeader.put( MARK_HEADER_ACCEPT_LANGUAGE, CONSTANTE_HEADER_ACCEPT_LANGUAGE );

        return mapHeader;
    }
    
    private String getInteretJson ( )
    {
        return "{"
                + "    \"Count\": 9,"
                + "    \"Total\": 9,"
                + "    \"ItemList\": ["
                + "        {"
                + "            \"Group\": {"
                + "                \"ID\": 18,"
                + "                \"MobileCount\": 0,"
                + "                \"EmailCount\": 0,"
                + "                \"CreateDate\": \"2020-06-24T14:55:19.0070000+02:00\","
                + "                \"UpdateDate\": \"2022-07-06T14:41:35.1100000+02:00\","
                + "                \"Name\": \"Evenement fin juillet\","
                + "                \"GroupType\": \"Multiple\""
                + "            },"
                + "            \"InterestList\": ["
                + "                {"
                + "                    \"ID\": 56,"
                + "                    \"DisplayRank\": 1,"
                + "                    \"GroupID\": 18,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-06-24T14:55:30.3070000+02:00\","
                + "                    \"UpdateDate\": \"2020-06-24T14:55:30.3070000+02:00\","
                + "                    \"Name\": \"29 juillet\""
                + "                },"
                + "                {"
                + "                    \"ID\": 57,"
                + "                    \"DisplayRank\": 2,"
                + "                    \"GroupID\": 18,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-06-24T14:55:35.7630000+02:00\","
                + "                    \"UpdateDate\": \"2020-06-24T14:55:35.7630000+02:00\","
                + "                    \"Name\": \"30 juillet\""
                + "                },"
                + "                {"
                + "                    \"ID\": 58,"
                + "                    \"DisplayRank\": 3,"
                + "                    \"GroupID\": 18,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-06-24T14:55:40.6300000+02:00\","
                + "                    \"UpdateDate\": \"2020-06-24T14:55:40.6300000+02:00\","
                + "                    \"Name\": \"31 juillet\""
                + "                }"
                + "            ]"
                + "        },"
                + "        {"
                + "            \"Group\": {"
                + "                \"ID\": 19,"
                + "                \"MobileCount\": 0,"
                + "                \"EmailCount\": 0,"
                + "                \"CreateDate\": \"2020-06-24T14:58:36.5470000+02:00\","
                + "                \"UpdateDate\": \"2022-07-06T14:41:35.1100000+02:00\","
                + "                \"Name\": \"Thématique QFAP\","
                + "                \"GroupType\": \"Multiple\""
                + "            },"
                + "            \"InterestList\": ["
                + "                {"
                + "                    \"ID\": 77,"
                + "                    \"DisplayRank\": 3,"
                + "                    \"GroupID\": 19,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2021-09-13T16:10:07.6570000+02:00\","
                + "                    \"UpdateDate\": \"2021-09-13T16:10:07.6570000+02:00\","
                + "                    \"Name\": \"Nature\""
                + "                },"
                + "                {"
                + "                    \"ID\": 78,"
                + "                    \"DisplayRank\": 4,"
                + "                    \"GroupID\": 19,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2021-09-13T16:10:41.4870000+02:00\","
                + "                    \"UpdateDate\": \"2021-09-13T16:10:41.4870000+02:00\","
                + "                    \"Name\": \"Sport\""
                + "                },"
                + "                {"
                + "                    \"ID\": 79,"
                + "                    \"DisplayRank\": 5,"
                + "                    \"GroupID\": 19,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2021-09-13T16:10:52.6000000+02:00\","
                + "                    \"UpdateDate\": \"2021-09-13T16:10:52.6000000+02:00\","
                + "                    \"Name\": \"Expos\""
                + "                },"
                + "                {"
                + "                    \"ID\": 80,"
                + "                    \"DisplayRank\": 6,"
                + "                    \"GroupID\": 19,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2021-09-13T16:11:00.0470000+02:00\","
                + "                    \"UpdateDate\": \"2021-09-13T16:11:00.0470000+02:00\","
                + "                    \"Name\": \"Enfants\""
                + "                }"
                + "            ]"
                + "        },"
                + "        {"
                + "            \"Group\": {"
                + "                \"ID\": 20,"
                + "                \"MobileCount\": 0,"
                + "                \"EmailCount\": 0,"
                + "                \"CreateDate\": \"2020-07-02T11:34:51.9400000+02:00\","
                + "                \"UpdateDate\": \"2022-07-06T14:41:35.1100000+02:00\","
                + "                \"Name\": \"[Paris.fr][1]\","
                + "                \"GroupType\": \"Multiple\""
                + "            },"
                + "            \"InterestList\": ["
                + "                {"
                + "                    \"ID\": 61,"
                + "                    \"DisplayRank\": 1,"
                + "                    \"GroupID\": 20,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-07-02T11:35:00.0430000+02:00\","
                + "                    \"UpdateDate\": \"2020-07-02T11:35:00.0430000+02:00\","
                + "                    \"Name\": \"Paris.fr\""
                + "                }"
                + "            ]"
                + "        },"
                + "        {"
                + "            \"Group\": {"
                + "                \"ID\": 21,"
                + "                \"MobileCount\": 0,"
                + "                \"EmailCount\": 0,"
                + "                \"CreateDate\": \"2020-07-06T15:57:34.7370000+02:00\","
                + "                \"UpdateDate\": \"2022-07-06T14:41:35.1100000+02:00\","
                + "                \"Name\": \"[Que faire à Paris][2]\","
                + "                \"GroupType\": \"Multiple\""
                + "            },"
                + "            \"InterestList\": ["
                + "                {"
                + "                    \"ID\": 62,"
                + "                    \"DisplayRank\": 1,"
                + "                    \"GroupID\": 21,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-07-06T15:57:45.6500000+02:00\","
                + "                    \"UpdateDate\": \"2020-09-23T16:56:52.2000000+02:00\","
                + "                    \"Name\": \"Que faire à Paris\""
                + "                }"
                + "            ]"
                + "        },"
                + "        {"
                + "            \"Group\": {"
                + "                \"ID\": 22,"
                + "                \"MobileCount\": 0,"
                + "                \"EmailCount\": 0,"
                + "                \"CreateDate\": \"2020-09-11T11:52:56.6400000+02:00\","
                + "                \"UpdateDate\": \"2022-07-06T14:41:35.1100000+02:00\","
                + "                \"Name\": \"[Alertes][3]\","
                + "                \"GroupType\": \"Multiple\""
                + "            },"
                + "            \"InterestList\": ["
                + "                {"
                + "                    \"ID\": 63,"
                + "                    \"DisplayRank\": 1,"
                + "                    \"GroupID\": 22,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-09-11T12:08:32.6270000+02:00\","
                + "                    \"UpdateDate\": \"2020-09-11T12:08:32.6270000+02:00\","
                + "                    \"Name\": \"Intempéries\""
                + "                },"
                + "                {"
                + "                    \"ID\": 64,"
                + "                    \"DisplayRank\": 2,"
                + "                    \"GroupID\": 22,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-09-11T12:08:57.2500000+02:00\","
                + "                    \"UpdateDate\": \"2020-09-11T12:08:57.2500000+02:00\","
                + "                    \"Name\": \"Circulation\""
                + "                },"
                + "                {"
                + "                    \"ID\": 65,"
                + "                    \"DisplayRank\": 3,"
                + "                    \"GroupID\": 22,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-09-11T12:09:02.9200000+02:00\","
                + "                    \"UpdateDate\": \"2020-09-11T12:09:02.9200000+02:00\","
                + "                    \"Name\": \"Vélo\""
                + "                },"
                + "                {"
                + "                    \"ID\": 66,"
                + "                    \"DisplayRank\": 4,"
                + "                    \"GroupID\": 22,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-09-11T12:09:10.7430000+02:00\","
                + "                    \"UpdateDate\": \"2020-09-11T12:09:10.7430000+02:00\","
                + "                    \"Name\": \"Propreté\""
                + "                },"
                + "                {"
                + "                    \"ID\": 67,"
                + "                    \"DisplayRank\": 5,"
                + "                    \"GroupID\": 22,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-09-11T12:09:34.6000000+02:00\","
                + "                    \"UpdateDate\": \"2020-09-11T12:09:34.6000000+02:00\","
                + "                    \"Name\": \"Familles\""
                + "                },"
                + "                {"
                + "                    \"ID\": 68,"
                + "                    \"DisplayRank\": 6,"
                + "                    \"GroupID\": 22,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-09-11T12:09:41.3300000+02:00\","
                + "                    \"UpdateDate\": \"2020-09-11T18:43:36.3330000+02:00\","
                + "                    \"Name\": \"Séniors\""
                + "                },"
                + "                {"
                + "                    \"ID\": 69,"
                + "                    \"DisplayRank\": 7,"
                + "                    \"GroupID\": 22,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-09-11T12:09:46.5130000+02:00\","
                + "                    \"UpdateDate\": \"2020-09-11T12:09:46.5130000+02:00\","
                + "                    \"Name\": \"Santé\""
                + "                },"
                + "                {"
                + "                    \"ID\": 71,"
                + "                    \"DisplayRank\": 9,"
                + "                    \"GroupID\": 22,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-09-11T12:09:58.4530000+02:00\","
                + "                    \"UpdateDate\": \"2020-09-11T16:00:53.8230000+02:00\","
                + "                    \"Name\": \"Conservatoires\""
                + "                },"
                + "                {"
                + "                    \"ID\": 72,"
                + "                    \"DisplayRank\": 10,"
                + "                    \"GroupID\": 22,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-09-11T12:10:07.8770000+02:00\","
                + "                    \"UpdateDate\": \"2020-09-11T12:10:07.8770000+02:00\","
                + "                    \"Name\": \"Beaux-Arts\""
                + "                },"
                + "                {"
                + "                    \"ID\": 73,"
                + "                    \"DisplayRank\": 11,"
                + "                    \"GroupID\": 22,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-09-11T12:10:28.8030000+02:00\","
                + "                    \"UpdateDate\": \"2020-09-11T12:10:28.8030000+02:00\","
                + "                    \"Name\": \"Élections\""
                + "                },"
                + "                {"
                + "                    \"ID\": 74,"
                + "                    \"DisplayRank\": 12,"
                + "                    \"GroupID\": 22,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-09-11T12:10:35.5530000+02:00\","
                + "                    \"UpdateDate\": \"2020-09-11T12:10:35.5530000+02:00\","
                + "                    \"Name\": \"Bulletin Officiel\""
                + "                },"
                + "                {"
                + "                    \"ID\": 75,"
                + "                    \"DisplayRank\": 13,"
                + "                    \"GroupID\": 22,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2020-09-11T16:00:16.3030000+02:00\","
                + "                    \"UpdateDate\": \"2021-02-11T09:46:50.6100000+01:00\","
                + "                    \"Name\": \"Cours d'adultes de Paris\""
                + "                }"
                + "            ]"
                + "        },"
                + "        {"
                + "            \"Group\": {"
                + "                \"ID\": 23,"
                + "                \"MobileCount\": 0,"
                + "                \"EmailCount\": 0,"
                + "                \"CreateDate\": \"2021-04-15T17:20:54.1530000+02:00\","
                + "                \"UpdateDate\": \"2022-07-06T14:41:35.1100000+02:00\","
                + "                \"Name\": \"Dashboard\","
                + "                \"GroupType\": \"Single\""
                + "            },"
                + "            \"InterestList\": ["
                + "                {"
                + "                    \"ID\": 76,"
                + "                    \"DisplayRank\": 1,"
                + "                    \"GroupID\": 23,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2021-04-15T17:21:23.7730000+02:00\","
                + "                    \"UpdateDate\": \"2021-04-15T17:21:23.7730000+02:00\","
                + "                    \"Name\": \"Dashboard\""
                + "                }"
                + "            ]"
                + "        },"
                + "        {"
                + "            \"Group\": {"
                + "                \"ID\": 24,"
                + "                \"MobileCount\": 0,"
                + "                \"EmailCount\": 0,"
                + "                \"CreateDate\": \"2022-03-30T13:16:46.0470000+02:00\","
                + "                \"UpdateDate\": \"2022-07-06T14:41:35.1100000+02:00\","
                + "                \"Name\": \"Baromètre\","
                + "                \"GroupType\": \"Single\""
                + "            },"
                + "            \"InterestList\": ["
                + "                {"
                + "                    \"ID\": 81,"
                + "                    \"DisplayRank\": 1,"
                + "                    \"GroupID\": 24,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2022-03-30T13:18:16.3730000+02:00\","
                + "                    \"UpdateDate\": \"2022-03-30T13:18:16.3730000+02:00\","
                + "                    \"Name\": \"Baromètre\""
                + "                }"
                + "            ]"
                + "        },"
                + "        {"
                + "            \"Group\": {"
                + "                \"ID\": 25,"
                + "                \"MobileCount\": 0,"
                + "                \"EmailCount\": 0,"
                + "                \"CreateDate\": \"2022-07-06T14:40:58.0100000+02:00\","
                + "                \"UpdateDate\": \"2022-07-06T14:41:35.1100000+02:00\","
                + "                \"Name\": \"Test géolocalisation\","
                + "                \"GroupType\": \"Single\""
                + "            },"
                + "            \"InterestList\": ["
                + "                {"
                + "                    \"ID\": 82,"
                + "                    \"DisplayRank\": 1,"
                + "                    \"GroupID\": 25,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2022-07-06T14:41:05.8900000+02:00\","
                + "                    \"UpdateDate\": \"2022-07-06T14:41:35.1100000+02:00\","
                + "                    \"Name\": \"Paris centre test\""
                + "                },"
                + "                {"
                + "                    \"ID\": 83,"
                + "                    \"DisplayRank\": 2,"
                + "                    \"GroupID\": 25,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2022-07-06T14:41:17.7500000+02:00\","
                + "                    \"UpdateDate\": \"2022-07-06T14:41:17.7500000+02:00\","
                + "                    \"Name\": \"5ème test\""
                + "                },"
                + "                {"
                + "                    \"ID\": 84,"
                + "                    \"DisplayRank\": 3,"
                + "                    \"GroupID\": 25,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2022-07-06T14:41:24.2900000+02:00\","
                + "                    \"UpdateDate\": \"2022-07-06T14:41:24.2900000+02:00\","
                + "                    \"Name\": \"6ème test\""
                + "                }"
                + "            ]"
                + "        },"
                + "        {"
                + "            \"Group\": {"
                + "                \"ID\": 26,"
                + "                \"MobileCount\": 0,"
                + "                \"EmailCount\": 0,"
                + "                \"CreateDate\": \"2023-07-06T15:36:23.6970000+02:00\","
                + "                \"UpdateDate\": \"2023-07-06T15:36:23.6970000+02:00\","
                + "                \"Name\": \"Site paris.fr\","
                + "                \"GroupType\": \"Single\""
                + "            },"
                + "            \"InterestList\": ["
                + "                {"
                + "                    \"ID\": 85,"
                + "                    \"DisplayRank\": 1,"
                + "                    \"GroupID\": 26,"
                + "                    \"MobileCount\": 0,"
                + "                    \"EmailCount\": 0,"
                + "                    \"CreateDate\": \"2023-07-19T11:10:12.4200000+02:00\","
                + "                    \"UpdateDate\": \"2023-07-19T11:10:12.4200000+02:00\","
                + "                    \"Name\": \"composant nl sur paris.fr\""
                + "                }"
                + "            ]"
                + "        }"
                + "    ]"
                + "}";
    }
    
    private String getSubJson ( )
    {
        return "{"
                + "    \"Count\": 22,"
                + "    \"Total\": 22,"
                + "    \"ItemList\": ["
                + "        {"
                + "            \"ID\": 24,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2023-04-13T11:06:39.6900000+02:00\","
                + "            \"UpdateDate\": \"2023-04-13T11:06:39.6900000+02:00\","
                + "            \"Contacts\": 1931072,"
                + "            \"Name\": \"Mon Paris avril 2023\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 23,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2023-01-18T11:18:39.6400000+01:00\","
                + "            \"UpdateDate\": \"2023-01-18T11:18:39.6400000+01:00\","
                + "            \"Contacts\": 1558,"
                + "            \"Name\": \"Jeux 2024\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 22,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2021-09-17T09:57:32.3530000+02:00\","
                + "            \"UpdateDate\": \"2021-09-17T09:57:32.3530000+02:00\","
                + "            \"Contacts\": 9356,"
                + "            \"Name\": \"Sport\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 21,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2021-09-17T09:57:27.4670000+02:00\","
                + "            \"UpdateDate\": \"2021-09-17T09:57:27.4670000+02:00\","
                + "            \"Contacts\": 17472,"
                + "            \"Name\": \"Expos\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 20,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2021-09-17T09:57:22.7200000+02:00\","
                + "            \"UpdateDate\": \"2021-09-17T09:57:22.7200000+02:00\","
                + "            \"Contacts\": 11532,"
                + "            \"Name\": \"Enfants\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 19,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2021-09-17T09:57:16.8030000+02:00\","
                + "            \"UpdateDate\": \"2021-09-17T09:57:16.8030000+02:00\","
                + "            \"Contacts\": 12535,"
                + "            \"Name\": \"Nature\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 18,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2021-08-23T09:19:30.4130000+02:00\","
                + "            \"UpdateDate\": \"2021-08-23T09:19:30.4130000+02:00\","
                + "            \"Contacts\": 4955,"
                + "            \"Name\": \"Contributeurs\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 17,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2021-05-03T12:10:08.3130000+02:00\","
                + "            \"UpdateDate\": \"2021-05-03T12:10:08.3130000+02:00\","
                + "            \"Contacts\": 1012,"
                + "            \"Name\": \"Magazine À Paris\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 14,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2020-06-23T12:53:01.6030000+02:00\","
                + "            \"UpdateDate\": \"2020-06-23T12:53:01.6030000+02:00\","
                + "            \"Contacts\": 119953,"
                + "            \"Name\": \"Que faire à Paris\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 13,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2020-06-23T12:52:51.7130000+02:00\","
                + "            \"UpdateDate\": \"2020-06-23T12:52:51.7130000+02:00\","
                + "            \"Contacts\": 47813,"
                + "            \"Name\": \"Beaux-Arts\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 12,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2020-06-23T12:52:39.0600000+02:00\","
                + "            \"UpdateDate\": \"2020-06-23T12:52:39.0600000+02:00\","
                + "            \"Contacts\": 13061,"
                + "            \"Name\": \"Bulletin Officiel\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 11,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2020-06-23T12:52:31.0230000+02:00\","
                + "            \"UpdateDate\": \"2020-06-23T12:52:31.0230000+02:00\","
                + "            \"Contacts\": 46214,"
                + "            \"Name\": \"Circulation\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 10,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2020-06-23T12:52:22.2030000+02:00\","
                + "            \"UpdateDate\": \"2023-08-21T13:03:53.8030000+02:00\","
                + "            \"Contacts\": 57261,"
                + "            \"Name\": \"Cours d'Adultes de Paris\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 9,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2020-06-23T12:52:14.6100000+02:00\","
                + "            \"UpdateDate\": \"2020-06-23T12:52:14.6100000+02:00\","
                + "            \"Contacts\": 29010,"
                + "            \"Name\": \"Conservatoires\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 8,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2020-06-23T12:52:09.3270000+02:00\","
                + "            \"UpdateDate\": \"2020-06-23T12:52:09.3270000+02:00\","
                + "            \"Contacts\": 16258,"
                + "            \"Name\": \"Élections\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 7,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2020-06-23T12:52:01.4330000+02:00\","
                + "            \"UpdateDate\": \"2020-06-23T12:52:01.4330000+02:00\","
                + "            \"Contacts\": 41865,"
                + "            \"Name\": \"Familles\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 6,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2020-06-23T12:51:53.5070000+02:00\","
                + "            \"UpdateDate\": \"2020-06-23T12:51:53.5070000+02:00\","
                + "            \"Contacts\": 25617,"
                + "            \"Name\": \"Intempéries\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 5,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2020-06-23T12:36:36.4600000+02:00\","
                + "            \"UpdateDate\": \"2020-06-23T12:36:36.4600000+02:00\","
                + "            \"Contacts\": 16114,"
                + "            \"Name\": \"Propreté\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 4,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2020-06-23T12:36:29.9230000+02:00\","
                + "            \"UpdateDate\": \"2020-06-23T12:36:29.9230000+02:00\","
                + "            \"Contacts\": 16945,"
                + "            \"Name\": \"Santé\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 3,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2020-06-23T12:36:24.2170000+02:00\","
                + "            \"UpdateDate\": \"2020-06-23T12:36:24.2170000+02:00\","
                + "            \"Contacts\": 28593,"
                + "            \"Name\": \"Séniors\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 2,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2020-06-23T12:36:12.0400000+02:00\","
                + "            \"UpdateDate\": \"2020-06-23T12:36:12.0400000+02:00\","
                + "            \"Contacts\": 18014,"
                + "            \"Name\": \"Vélo\","
                + "            \"Channel\": \"Email\""
                + "        },"
                + "        {"
                + "            \"ID\": 1,"
                + "            \"IsEnabled\": true,"
                + "            \"CreateDate\": \"2020-06-23T12:54:01.6030000+02:00\","
                + "            \"UpdateDate\": \"2020-06-23T12:54:01.6030000+02:00\","
                + "            \"Contacts\": 309571,"
                + "            \"Name\": \"Paris.fr\","
                + "            \"Channel\": \"Email\""
                + "        }"
                + "    ]"
                + "}";
    }

}
