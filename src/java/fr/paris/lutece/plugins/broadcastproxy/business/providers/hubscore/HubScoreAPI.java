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
import fr.paris.lutece.portal.service.init.LuteceInitException;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class HubScoreAPI
{

    // Constants
    private static final String URL_SITE_HUB_SCORE = AppPropertiesService.getProperty( "broadcastproxy.hubscore.URL_SITE_HUB_SCORE" );
    private static final String URL_PATH_DATABASE_HUB_SCORE = AppPropertiesService.getProperty( "broadcastproxy.hubscore.URL_PATH_DATABASE_HUB_SCORE" );
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

    // Errors
    private static final String ERROR_MSG_INVALID_TOKEN_MESSAGE = AppPropertiesService.getProperty( "broadcastproxy.hubscore.ERROR_MSG_INVALID_TOKEN_MESSAGE" );
    private static final String ERROR_INVALID_TOKEN_CODE = AppPropertiesService.getProperty( "broadcastproxy.hubscore.ERROR_INVALID_TOKEN_CODE");
    private static final String ERROR_MSG_NO_ERROR_CODE = "#i18n{msg.hubscore.ERROR_MSG_NO_ERROR_CODE";
    private static final String ERROR_MSG_PARSE_JSON = "#i18n{msg.hubscore.ERROR_MSG_PARSE_JSON}";
    
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
    public String GetHubScoreUserId( String userName, String typeSubsciption, boolean forceTokenRefresh )
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
     * @throws IOException 
     * @throws java.lang.Exception
     */
    public String manageUser( String eMail, String typeSubsciption, String action, boolean forceTokenRefresh ) throws IOException {
        
        ObjectMapper mapper = new ObjectMapper( );

        String strUrl = URL_SITE_HUB_SCORE + URL_PATH_DATABASE_HUB_SCORE + PATH_MANAGE_USR;

        Map<String, String> hmHeaders = new HashMap<>( );
        hmHeaders.put( MARK_HEADER_AUTHORIZATION, MARK_HEADER_BEARER + HubScoreAPI.this.getToken( typeSubsciption, forceTokenRefresh ) );

        Map<String, String> hmUser = new HashMap<>( );
        hmUser.put( "Email", eMail );

        Map<String, String> params = new HashMap<>( );
        params.put( "datas", mapper.writeValueAsString( hmUser ) );

        if ( Constants.ACTION_ADD.equals( action ) )
        {
        	return CallDoPost( strUrl, params, hmHeaders, typeSubsciption, forceTokenRefresh );
        }

        if ( Constants.ACTION_DELETE.equals( action ) )
        {
        	String strHubScoreUserId = GetHubScoreUserId(eMail, typeSubsciption, forceTokenRefresh);
        	strUrl = strUrl + "/" + strHubScoreUserId;
            return CallDoDelete( strUrl, hmHeaders, typeSubsciption, forceTokenRefresh );
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

        String strUrl = URL_SITE_HUB_SCORE + URL_PATH_DATABASE_HUB_SCORE + PATH_GET_USR_SUBSRIPTIONS_PART1 + userId + PATH_GET_USR_SUBSRIPTIONS_PART2;

        Map<String, String> mapHeaders = new HashMap<>( );

        try
        {        	
        	strResponse = CallDoGet(strUrl, mapHeaders, typeSubsciption, forceTokenRefresh);        	
        }
        catch( Exception e )
        {        	
            String strError = "Error occured while getting user subscriptions list from '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage( ), e );
        }
        
        return strResponse;
    }    
    
    /**
     * update subscriptions
     * 
     * @param userName
     * @param mapSubscriptions
     * @param typeSubscription
     * @param forceTokenRefresh
     * @return the response
     * @throws java.lang.Exception
     */
    public String updateSubscribtions( String userName, Map<String, String> mapSubscriptions, String typeSubscription, boolean forceTokenRefresh )
            throws Exception
    {
        String strUrl = URL_SITE_HUB_SCORE + URL_PATH_DATABASE_HUB_SCORE + PATH_USR_SUBSCRIBE;

        try
        {
            // Get user ID
            String strUserId = GetHubScoreUserId( userName, typeSubscription, forceTokenRefresh );

            if ( strUserId == null && !StringUtils.isBlank( userName ) )
            {
                // create user
                manageUser( userName, typeSubscription, Constants.ACTION_ADD, forceTokenRefresh );

                // get the new id
                strUserId = GetHubScoreUserId( userName, typeSubscription, forceTokenRefresh );
            }

            Map<String, String> mapHeaders = new HashMap<>( );
            mapHeaders.put( MARK_HEADER_AUTHORIZATION, MARK_HEADER_BEARER + HubScoreAPI.this.getToken( typeSubscription, forceTokenRefresh ) );

            strUrl += "/" + strUserId + ".json";

            AppLogService.info( "Trying to update subscription of : " + userName );

            String response = CallDoPatch( strUrl, mapSubscriptions, mapHeaders, typeSubscription, forceTokenRefresh );
            
            return response;
        }
        catch( HttpAccessException | IOException e )
        {
            String strError = "Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage( ), e );
            return null;
        }
    }

    /*** TOKEN MANAGEMENT ***/

    /**
     * get Token by subscription type
     * @throws IOException 
     * @throws LuteceInitException 
     * 
     */
    private String getToken( String typeSubscription, boolean forceRefresch ) throws IOException {
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
     * @throws IOException 
     * @throws LuteceInitException 
     */
    public String getNewsletterToken( boolean forceRefresh ) throws IOException {
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
     * @throws LuteceInitException 
     */
    public String getAlertToken( boolean forceRefresh ) throws IOException {
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
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     * @throws HttpAccessException
     * @throws LuteceInitException 
     */
    private String getToken( String strUsername, String strPassword ) throws IOException {
        ObjectMapper mapper = new ObjectMapper( );
    	HubScoreHttpAccess hubscorehttpaccess = new HubScoreHttpAccess();
    	HttpResponse httpResponse;
    	String strToken;

        String strUrl = URL_SITE_HUB_SCORE + URL_PATH_AUTHENTICATION_HUB_SCORE;

		List<BasicNameValuePair> listParams = new ArrayList<>( );
		listParams.add( new BasicNameValuePair( "Username", strUsername ));
		listParams.add( new BasicNameValuePair( "Password", strPassword ));
		//listParams.add( new BasicNameValuePair( "Password", "toto" ));
					
		httpResponse = hubscorehttpaccess.doPost( strUrl, listParams, null );

		strToken = (String) mapper.readValue( HttpToStrResponse(httpResponse), HashMap.class ).get( "token" );			

        return strToken;        
    }
    
    private String CallDoGet(String strUrl, Map<String, String> mapHeaders, String typeSubsciption, boolean forceTokenRefresh) throws IOException {
    	
    	String strToken;
    	HttpResponse httpResponse;
    	HubScoreHttpAccess hubscorehttpaccess = new HubScoreHttpAccess();
    	
		strToken = getToken( typeSubsciption, forceTokenRefresh );
		
		mapHeaders.put( MARK_HEADER_AUTHORIZATION, MARK_HEADER_BEARER + strToken );  
		
		// Do Get
		httpResponse = hubscorehttpaccess.doGet(strUrl, mapHeaders);
    	
		// If error
		if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() != 200) {
			String strErrorMsg = GetHubScoreErrorMessage(HttpToStrResponse(httpResponse));
			
			if (strErrorMsg.equals(ERROR_MSG_INVALID_TOKEN_MESSAGE)) {					
				strToken = getToken(typeSubsciption, true);
				mapHeaders.put( MARK_HEADER_AUTHORIZATION, MARK_HEADER_BEARER + strToken );            
				httpResponse = hubscorehttpaccess.doGet(strUrl, mapHeaders);				
			}			
		}
		    	
    	return HttpToStrResponse(httpResponse);
    }
        
    private String CallDoPost(String strUrl, Map<String, String> params, Map<String, String> mapHeaders, String typeSubsciption, boolean forceTokenRefresh) throws IOException {
    	
    	String strToken;
    	HttpResponse httpResponse;
    	HubScoreHttpAccess hubscorehttpaccess = new HubScoreHttpAccess();
    	List<BasicNameValuePair> listParams = new ArrayList<>( );    	

		strToken = getToken( typeSubsciption, forceTokenRefresh );
		
		mapHeaders.put( MARK_HEADER_AUTHORIZATION, MARK_HEADER_BEARER + strToken ); 

		// add request parameters
		if ( params != null )
		{
		    for ( String paramsKey : params.keySet( ) )
		    {
		    	listParams.add( new BasicNameValuePair( paramsKey, params.get(paramsKey) ));
		    }
		}	
		
		// Do Post
		httpResponse = hubscorehttpaccess.doPost(strUrl, listParams, mapHeaders);
    	
		// If error
		if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() != 200) {
			String strErrorMsg = GetHubScoreErrorMessage(HttpToStrResponse(httpResponse));
			
			if (strErrorMsg.equals(ERROR_MSG_INVALID_TOKEN_MESSAGE)) {					
				strToken = getToken(typeSubsciption, true);
				mapHeaders.put( MARK_HEADER_AUTHORIZATION, MARK_HEADER_BEARER + strToken );            
				httpResponse = hubscorehttpaccess.doGet(strUrl, mapHeaders);	
			}			
		}
		    	
    	return HttpToStrResponse(httpResponse);   	
    }
    
    private String CallDoPatch(String strUrl, Map<String, String> params, Map<String, String> mapHeaders, String typeSubsciption, boolean forceTokenRefresh) throws HttpAccessException, IOException, LuteceInitException {
    	String strToken;
    	HttpResponse httpResponse;
    	HubScoreHttpAccess hubscorehttpaccess = new HubScoreHttpAccess();
    	List<BasicNameValuePair> listParams = new ArrayList<>( );    	

    	ObjectMapper mapper = new ObjectMapper( );

		strToken = getToken( typeSubsciption, forceTokenRefresh );
		
		mapHeaders.put( MARK_HEADER_AUTHORIZATION, MARK_HEADER_BEARER + strToken ); 

		// Add parameters
		String strParamsInJson = mapper.writeValueAsString( params );
		listParams.add(new BasicNameValuePair( "datas", strParamsInJson ) );
		
		// add request parameters
		if ( params != null )
		{
		    for ( String paramsKey : params.keySet( ) )
		    {
		    	listParams.add( new BasicNameValuePair( paramsKey, params.get(paramsKey) ));
		    }
		}	
		
		// Do Patch
		httpResponse = hubscorehttpaccess.doPatch(strUrl, listParams, mapHeaders);
    	
		// If error
		if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() != 204) {
			String strErrorMsg = GetHubScoreErrorMessage(HttpToStrResponse(httpResponse));
			
			if (strErrorMsg.equals(ERROR_MSG_INVALID_TOKEN_MESSAGE)) {					
				strToken = getToken(typeSubsciption, true);
				mapHeaders.put( MARK_HEADER_AUTHORIZATION, MARK_HEADER_BEARER + strToken );            
				httpResponse = hubscorehttpaccess.doGet(strUrl, mapHeaders);	
			}			
		}
    	
		return HttpToStrResponse(httpResponse);   	    	
    }

    private String CallDoDelete(String strUrl, Map<String, String> mapHeaders, String typeSubsciption, boolean forceTokenRefresh) throws IOException {
    	String strToken;
    	HttpResponse httpResponse;
    	HubScoreHttpAccess hubscorehttpaccess = new HubScoreHttpAccess();
    	
		strToken = getToken( typeSubsciption, forceTokenRefresh );
		
		mapHeaders.put( MARK_HEADER_AUTHORIZATION, MARK_HEADER_BEARER + strToken );  
		
		// Do Delete
		httpResponse = hubscorehttpaccess.doDelete(strUrl, mapHeaders);
    	
		// If error
		if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() != 200) {
			String strErrorMsg = GetHubScoreErrorMessage(HttpToStrResponse(httpResponse));
			
			if (strErrorMsg.equals(ERROR_MSG_INVALID_TOKEN_MESSAGE)) {					
				strToken = getToken(typeSubsciption, true);
				mapHeaders.put( MARK_HEADER_AUTHORIZATION, MARK_HEADER_BEARER + strToken );            
				httpResponse = hubscorehttpaccess.doGet(strUrl, mapHeaders);				
			}			
		}
		    	
    	return HttpToStrResponse(httpResponse);    	  	
    }

    private String HttpToStrResponse (HttpResponse httpResponse) throws IOException {

		StringBuffer strResponse = new StringBuffer();

		// Get response data in string
		if ( httpResponse != null && httpResponse.getEntity( ) != null && httpResponse.getEntity( ).getContent( ) != null )
                {
                    BufferedReader bufferedreader = new BufferedReader( new InputStreamReader( httpResponse.getEntity( ).getContent( ) ) );
                    String line = "";
                    while ( ( line = bufferedreader.readLine()) != null )
                    {
                        strResponse.append( line );
                    }
                }
		
		return strResponse.toString();
    }    

	private String GetHubScoreErrorMessage(String strResponse) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode nodes = null;
		
		try {
			nodes = mapper.readTree(strResponse);
		} catch (JsonProcessingException e) {
			throw new AppException( ERROR_MSG_PARSE_JSON, e);
		} catch (IOException e) {
			throw new AppException( ERROR_MSG_PARSE_JSON, e);
		}
		
		if (!nodes.has("code")) {
			throw new AppException(ERROR_MSG_NO_ERROR_CODE, null);
		}
		
		if (nodes.has("code")) {
			if (nodes.get("code").asInt() == Integer.valueOf(ERROR_INVALID_TOKEN_CODE)) {				
				if (nodes.get("message").asText().equals(ERROR_MSG_INVALID_TOKEN_MESSAGE) ){
					return ERROR_MSG_INVALID_TOKEN_MESSAGE;					
				}						
			}
		}
		
		return null;
	}


}
