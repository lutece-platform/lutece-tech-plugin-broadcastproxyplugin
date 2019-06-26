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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.broadcastproxy.business.providers.hubscore;

import fr.paris.lutece.plugins.broadcastproxy.service.Constants;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;
import java.io.IOException;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.apache.http.client.methods.HttpPatch;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class HubScoreAPI
{

    // Constants
    private static final String URL_SITE_HUB_SCORE = AppPropertiesService.getProperty( "broadcastproxy.hubscore.URL_SITE_HUB_SCORE" );
    private static final String URL_PATH_DATABASE_HUB_SCORE = AppPropertiesService.getProperty( "broadcastproxy.hubscore.URL_PATH_DATABASE_HUB_SCORE" );
    private static final String URL_PATH_USERS_HUB_SCORE = AppPropertiesService.getProperty( "broadcastproxy.hubscore.URL_PATH_USERS_HUB_SCORE" );
    private static final String URL_PATH_AUTHENTICATION_HUB_SCORE = AppPropertiesService
            .getProperty( "broadcastproxy.hubscore.URL_PATH_AUTHENTICATION_HUB_SCORE" );

    private static final String PATH_GET_USR_SUBSRIPTIONS_PART1 = AppPropertiesService.getProperty( "broadcastproxy.hubscore.PATH_GET_USR_SUBSRIPTIONS_PART1" );
    private static final String PATH_GET_USR_SUBSRIPTIONS_PART2 = AppPropertiesService.getProperty( "broadcastproxy.hubscore.PATH_GET_USR_SUBSRIPTIONS_PART2" );
    private static final String PATH_MANAGE_USR = AppPropertiesService.getProperty( "broadcastproxy.hubscore.PATH_MANAGE_USR" );
    private static final String PATH_USR_SUBSCRIBE = AppPropertiesService.getProperty( "broadcastproxy.hubscore.PATH_USR_SUBSCRIBE" );

    // Markers
    private static final String MARK_HEADER_AUTHORIZATION = AppPropertiesService.getProperty( "broadcastproxy.hubscore.MARK_HEADER_AUTHORIZATION" );
    private static final String MARK_HEADER_BEARER = AppPropertiesService.getProperty( "broadcastproxy.hubscore.MARK_HEADER_BEARER" );

    // A mettre dans un fichier .properties
    private static final String LOGIN_AUTH_NEWSLETTER = AppPropertiesService.getProperty( "broadcastproxy.hubscore.LOGIN_AUTH_NEWSLETTER" );
    private static final String PASSWORD_AUTH_NEWSLETTER = AppPropertiesService.getProperty( "broadcastproxy.hubscore.PASSWORD_AUTH_NEWSLETTER" );
    private static final String LOGIN_AUTH_ALERT = AppPropertiesService.getProperty( "broadcastproxy.hubscore.LOGIN_AUTH_ALERT" );
    private static final String PASSWORD_AUTH_ALERT = AppPropertiesService.getProperty( "broadcastproxy.hubscore.PASSWORD_AUTH_ALERT" );

    // get proxy from HttpAccess properties
    private static final String PROXY_ADR = AppPropertiesService.getProperty( "httpAccess.proxyHost" );
    private static final int PROXY_PORT = AppPropertiesService.getPropertyInt( "httpAccess.proxyPort", 3128 );

    // Instance variables
    private String _strNewsletterToken;
    private String _strAlertToken;
    private LocalDateTime _newsletterTokenExpiredTime;
    private LocalDateTime _alertTokenExpiredTime;
    private String _userName;
    private String _userId;

    public HubScoreAPI( )
    {
    }

    /**
     * get UserID
     * 
     * @param userName
     * @param forceTokenRefresh
     * @param typeSubsciption
     * @return the response message
     */
    public String getUserId( String userName, String typeSubsciption, boolean forceTokenRefresh )
    {

        if ( userName == null )
            return null;

        if ( userName.equals( _userName ) )
            return _userId;

        String strResponse = null;
        String _strUserId = "";
        ObjectMapper mapper = new ObjectMapper( );

        strResponse = getUserSubscriptions( userName, typeSubsciption, forceTokenRefresh );

        try
        {
            JsonNode nodes = mapper.readTree( strResponse );

            if ( nodes.get( "records" ).findValue( "id" ) == null )
                return null;

            _strUserId = nodes.get( "records" ).findValue( "id" ).asText( );
        }
        catch( IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
            return null;
        }

        // set instance variables
        _userName = userName;
        _userId = _strUserId;

        return _strUserId;
    }

    /**
     * Hubscore user record api
     * 
     * @param eMail
     * @param typeSubsciption
     * @param action
     * @return the response message
     * @throws java.lang.Exception
     */
    public String manageUser( String eMail, String typeSubsciption, String action, boolean forceTokenRefresh ) throws Exception
    {

        try
        {
            ObjectMapper mapper = new ObjectMapper( );
            HttpAccess httpAccess = new HttpAccess( );

            String strUrl = URL_SITE_HUB_SCORE + URL_PATH_DATABASE_HUB_SCORE + PATH_MANAGE_USR;

            Map<String, String> mapHeaders = new HashMap<>( );
            mapHeaders.put( MARK_HEADER_AUTHORIZATION, MARK_HEADER_BEARER + HubScoreAPI.this.getToken( typeSubsciption, forceTokenRefresh ) );

            Map<String, String> hmUser = new HashMap<>( );
            hmUser.put( "Email", eMail );

            Map<String, String> params = new HashMap<>( );
            params.put( "datas", mapper.writeValueAsString( hmUser ) );

            if ( Constants.ACTION_ADD.equals( action ) )
            {
                return httpAccess.doPost( strUrl, params, null, null, mapHeaders );
            }

            if ( Constants.ACTION_DELETE.equals( action ) )
            {
                return httpAccess.doDelete( strUrl, null, null, null, mapHeaders );
            }

        }
        catch( HttpAccessException e )
        {
            String strError = "Error connecting to '" + URL_SITE_HUB_SCORE + URL_PATH_DATABASE_HUB_SCORE + PATH_MANAGE_USR + "' : ";
            AppLogService.error( strError + e.getMessage( ), e );
            throw e;
        }
        catch( JsonParseException | JsonMappingException | JsonGenerationException e )
        {
            AppLogService.error( e.getMessage( ), e );
            throw ( e );
        }

        return null;
    }

    /**
     * get user subscriptions
     * 
     * @param userId
     * @param typeSubsciption
     * @param forceTokenRefresh
     * @return json
     */
    public String getUserSubscriptions( String userId, String typeSubsciption, boolean forceTokenRefresh )
    {

        String strResponse = StringUtils.EMPTY;
        HttpAccess httpAccess = new HttpAccess( );

        String strUrl = URL_SITE_HUB_SCORE + URL_PATH_DATABASE_HUB_SCORE + PATH_GET_USR_SUBSRIPTIONS_PART1 + userId + PATH_GET_USR_SUBSRIPTIONS_PART2;

        Map<String, String> mapHeaders = new HashMap<>( );

        try
        {
            String token = getToken( typeSubsciption, forceTokenRefresh );

            mapHeaders.put( MARK_HEADER_AUTHORIZATION, MARK_HEADER_BEARER + token );

            return httpAccess.doGet( strUrl, null, null, mapHeaders );
        }
        catch( HttpAccessException | IOException e )
        {
            String strError = "Error occured while getting user subscriptions list from '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage( ), e );
            return null;
        }

    }

    /**
     * update subecriptions
     * 
     * @param userName
     * @param mapSubscriptions
     * @param typeSubscription
     * @param forceTokenRefresh
     * @return the response
     * @throws java.lang.Exception
     */
    public HttpResponse updateSubscribtions( String userName, Map<String, String> mapSubscriptions, String typeSubscription, boolean forceTokenRefresh )
            throws Exception
    {
        String strUrl = URL_SITE_HUB_SCORE + URL_PATH_DATABASE_HUB_SCORE + PATH_USR_SUBSCRIBE;

        try
        {
            // Get user ID
            String strUserId = getUserId( userName, typeSubscription, forceTokenRefresh );

            if ( strUserId == null && !StringUtils.isBlank( userName ) )
            {
                // create user
                manageUser( userName, typeSubscription, Constants.ACTION_ADD, forceTokenRefresh );

                // get the new id
                strUserId = getUserId( userName, typeSubscription, forceTokenRefresh );
            }

            Map<String, String> mapHeaders = new HashMap<>( );
            mapHeaders.put( MARK_HEADER_AUTHORIZATION, MARK_HEADER_BEARER + HubScoreAPI.this.getToken( typeSubscription, forceTokenRefresh ) );

            strUrl += "/" + strUserId + ".json";

            AppLogService.info( "Trying to update subscription of : " + userName );

            HttpResponse response = doPatch( strUrl, mapSubscriptions, mapHeaders );

            if ( response.getStatusLine( ).getStatusCode( ) != 204 )
                AppLogService.error( "Error connecting to '" + strUrl + "' : " + response.getStatusLine( ).getReasonPhrase( ) );

            AppLogService.info( "Response Status when trying to update subscription of " + userName + " : " + response.getStatusLine( ).getReasonPhrase( ) );

            return response;
        }
        catch( HttpAccessException | IOException e )
        {
            String strError = "Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage( ), e );
            return null;
        }
    }

    /**
     * Call PATCH http method
     * 
     */
    private HttpResponse doPatch( String strUrl, Map<String, String> params, Map<String, String> headers ) throws HttpAccessException, JsonGenerationException,
            JsonMappingException, IOException
    {
        ObjectMapper mapper = new ObjectMapper( );
        HttpResponse httpResponse = null;
        HttpPatch method = new HttpPatch( strUrl );

        if ( headers != null )
        {
            for ( String headerType : headers.keySet( ) )
            {
                method.setHeader( headerType, headers.get( headerType ) );
            }
        }

        String strParamsInJson = mapper.writeValueAsString( params );
        List<NameValuePair> listDatas = new ArrayList<>( );
        listDatas.add( new BasicNameValuePair( "datas", strParamsInJson ) );

        // StringEntity stringEntity = new StringEntity(datas, ContentType.APPLICATION_FORM_URLENCODED);
        // stringEntity.setContentType( new BasicHeader( "Content-Type", "application/json;charset=UTF-8" ) );
        // stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
        // StringEntity stringEntity = new StringEntity( new UrlEncodedFormEntity( mapDatas) );
        // stringEntity.setContentType( new BasicHeader( "Content-Type", "application/x-www-form-urlencoded" ) );

        method.setHeader( "Content-Type", "application/x-www-form-urlencoded" );
        method.setEntity( new UrlEncodedFormEntity( listDatas ) );

        // stringEntity.setContentEncoding("UTF-8");
        // StringEntity stringEntity = new StringEntity(datas);
        // StringEntity stringEntity = new StringEntity(ContentType.parse(datas));

        try
        {
            CloseableHttpClient client = HttpClientBuilder.create( ).build( );

            // add proxy
            HttpHost proxy = new HttpHost( PROXY_ADR, PROXY_PORT );
            RequestConfig config = RequestConfig.custom( ).setProxy( proxy ).build( );
            method.setConfig( config );

            httpResponse = client.execute( method );
        }
        catch( IOException e )
        {
            String strError = "HttpPatch - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage( ), e );
            throw new HttpAccessException( strError + e.getMessage( ), e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection( );
        }

        // httpcode 204 : no content in response
        if ( httpResponse != null && httpResponse.getStatusLine( ).getStatusCode( ) != 204 )
        {
            String strError = "HttpPatch - Problem connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + httpResponse.getStatusLine( ).getReasonPhrase( ) );
        }

        return httpResponse;
    }

    /*** TOKEN MANAGEMENT ***/

    /**
     * get Token by subscription type
     * 
     */
    private String getToken( String typeSubscription, boolean forceRefresch ) throws HttpAccessException, IOException
    {
        if ( Constants.TYPE_NEWSLETTER.equals( typeSubscription ) )
        {
            return getNewsletterToken( forceRefresch );
        }

        if ( Constants.TYPE_ALERT.equals( typeSubscription ) )
        {
            return getAlertToken( forceRefresch );
        }

        return null;
    }

    /**
     * get newsletter token
     * 
     * @param forceRefresh
     * @return the token as string
     */
    public String getNewsletterToken( boolean forceRefresh ) throws HttpAccessException, IOException
    {
        if ( _strNewsletterToken == null || forceRefresh ||
                _newsletterTokenExpiredTime.isAfter( LocalDateTime.now( ) )  )
        {
            _strNewsletterToken = getToken( LOGIN_AUTH_NEWSLETTER, PASSWORD_AUTH_NEWSLETTER );
            // hubscore token is valid 12 hours
            _newsletterTokenExpiredTime = LocalDateTime.now( ).plusHours( 10 );
        }

        return _strNewsletterToken;
    }

    /**
     * get alert token
     * 
     * @param forceRefresh
     * @return the token as string
     * @throws HttpAccessException
     * @throws IOException
     */
    public String getAlertToken( boolean forceRefresh ) throws HttpAccessException, IOException
    {
        if ( _strAlertToken == null || forceRefresh  ||
                _alertTokenExpiredTime.isAfter( LocalDateTime.now( ) )  )
        {
            _strAlertToken = getToken( LOGIN_AUTH_ALERT, PASSWORD_AUTH_ALERT );
            // hubscore token is valid 12 hours
            _alertTokenExpiredTime = LocalDateTime.now( ).plusHours( 10 );
        }

        return _strAlertToken;
    }

    /**
     * get Token
     * 
     * @param strUsername
     * @param strPassword
     * @return the token
     * @throws HttpAccessException
     */
    private String getToken( String strUsername, String strPassword ) throws HttpAccessException, IOException
    {
        Map<String, String> params = new HashMap<>( );
        HttpAccess httpAccess = new HttpAccess( );
        ObjectMapper mapper = new ObjectMapper( );

        String strUrl = URL_SITE_HUB_SCORE + URL_PATH_AUTHENTICATION_HUB_SCORE;

        params.put( "Username", strUsername );
        params.put( "Password", strPassword );

        try
        {
            String strResponse = httpAccess.doPost( strUrl, params );

            String token = (String) mapper.readValue( strResponse, HashMap.class ).get( "token" );

            return token;
        }
        catch( HttpAccessException | IOException e )
        {
            String strError = "Error getting token from '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage( ), e );
            throw e;
        }
    }
}
