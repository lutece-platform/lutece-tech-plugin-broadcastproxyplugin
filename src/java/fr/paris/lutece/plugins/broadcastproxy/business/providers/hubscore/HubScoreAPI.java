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

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;

import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
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

    public HubScoreAPI( )
    {
    }

    /**
     * get UserID
     * 
     * @param eMail
     * @param typeSubsciption
     * @return the response message
     */
    public String getUserId( String eMail, String typeSubsciption )
    {

        String strResponse = null;
        String _strUserId = "";
        ObjectMapper mapper = new ObjectMapper( );

        strResponse = getUserSubscriptions( eMail, typeSubsciption );

        try
        {
            JsonNode nodes = mapper.readTree( strResponse );
            _strUserId = nodes.get( "records" ).findValue( "id" ).asText( );
        }
        catch( IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
            return null;
        }

        return _strUserId;
    }

    /**
     * Hubscore user api
     * 
     * @param eMail
     * @param typeSubsciption
     * @param action
     * @return the response message
     * @throws java.lang.Exception
     */
    public String manageUser( String eMail, String typeSubsciption, String action ) throws Exception
    {

        try
        {
            ObjectMapper mapper = new ObjectMapper( );
            HttpAccess httpAccess = new HttpAccess( );

            String strUrl = URL_SITE_HUB_SCORE + URL_PATH_DATABASE_HUB_SCORE + PATH_MANAGE_USR;

            Map<String, String> hmHeaders = new HashMap<>( );
            hmHeaders.put( MARK_HEADER_AUTHORIZATION, MARK_HEADER_BEARER + HubScoreAPI.this.getToken( typeSubsciption ) );

            Map<String, String> hmUser = new HashMap<>( );
            hmUser.put( "Email", eMail );

            Map<String, String> params = new HashMap<>( );
            params.put( "datas", mapper.writeValueAsString( hmUser ) );

            if ( Constants.ACTION_ADD.equals( action ) )
            {
                return httpAccess.doPost( strUrl, params, null, null, hmHeaders );
            }

            if ( Constants.ACTION_DELETE.equals( action ) )
            {
                return httpAccess.doDelete( strUrl, null, null, hmHeaders, null );
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

    public String getUserSubscriptions( String userId, String typeSubsciption )
    {

        String strResponse = StringUtils.EMPTY;
        HttpAccess httpAccess = new HttpAccess( );

        String strUrl = URL_SITE_HUB_SCORE + URL_PATH_DATABASE_HUB_SCORE + PATH_GET_USR_SUBSRIPTIONS_PART1 + userId + PATH_GET_USR_SUBSRIPTIONS_PART2;

        Map<String, String> hmHeaders = new HashMap<>( );

        try
        {
            String token = getToken( typeSubsciption );

            hmHeaders.put( MARK_HEADER_AUTHORIZATION, MARK_HEADER_BEARER + token );

            return httpAccess.doGet( strUrl, null, null, hmHeaders );

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
     * @param userId
     * @param listSubscriptions
     * @param typeSubsciption
     * @return the response
     */
    public HttpResponse updateSubscribtions( String userId, Map<String, String> listSubscriptions, String typeSubsciption )
    {
        String strUrl = URL_SITE_HUB_SCORE + URL_PATH_DATABASE_HUB_SCORE + PATH_USR_SUBSCRIBE;

        try
        {
            // Get user ID
            String strUserId = getUserId( userId, typeSubsciption );

            Map<String, String> htmlHeaders = new HashMap<>( );
            htmlHeaders.put( MARK_HEADER_AUTHORIZATION, MARK_HEADER_BEARER + HubScoreAPI.this.getToken( typeSubsciption ) );

            HttpResponse response = doPatch( strUrl + "/" + strUserId, listSubscriptions, htmlHeaders );
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
        HttpResponse strResponse = null;
        HttpPatch method = new HttpPatch( strUrl );

        if ( headers != null )
        {
            for ( String headerType : headers.keySet( ) )
            {
                method.setHeader( headerType, headers.get( headerType ) );
            }
        }

        // ******************** Add parameters ****************//
        String strParamsInJson = mapper.writeValueAsString( params );
        String datas = "{\"datas\":\"" + strParamsInJson + "\"}";
        // StringEntity stringEntity = new StringEntity(datas, ContentType.APPLICATION_FORM_URLENCODED);
        StringEntity stringEntity = new StringEntity( datas );
        stringEntity.setContentType( new BasicHeader( "Content-Type", "application/json;charset=UTF-8" ) );
        // stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));

        method.setEntity( new StringEntity( datas, Charset.forName( "UTF-8" ) ) );

        // stringEntity.setContentEncoding("UTF-8");
        // StringEntity stringEntity = new StringEntity(datas);
        // StringEntity stringEntity = new StringEntity(ContentType.parse(datas));

        // method.setEntity(stringEntity);

        try
        {
            CloseableHttpClient client = HttpClientBuilder.create( ).build( );
            HttpHost proxy = new HttpHost( PROXY_ADR, PROXY_PORT );
            RequestConfig config = RequestConfig.custom( ).setProxy( proxy ).build( );
            method.setConfig( config );

            strResponse = client.execute( method );
        }
        catch( HttpException e )
        {
            String strError = "HttpAccess - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage( ), e );
            throw new HttpAccessException( strError + e.getMessage( ), e );
        }
        catch( IOException e )
        {
            String strError = "HttpAccess - Error downloading '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage( ), e );
            throw new HttpAccessException( strError + e.getMessage( ), e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection( );
        }

        return strResponse;
    }

    /*** TOKEN MANAGEMENT ***/

    /**
     * get Token by subscription type
     * 
     */
    private String getToken( String typeSubscription ) throws HttpAccessException, IOException
    {
        if ( Constants.TYPE_NEWSLETTER.equals( typeSubscription ) )
        {
            return getNewsletterToken( false );
        }

        if ( Constants.TYPE_ALERT.equals( typeSubscription ) )
        {
            return getAlertToken( false );
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
        if ( _strNewsletterToken == null || forceRefresh )
        {
            _strNewsletterToken = getToken( LOGIN_AUTH_NEWSLETTER, PASSWORD_AUTH_NEWSLETTER );
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
        if ( _strAlertToken == null || forceRefresh )
        {
            _strAlertToken = getToken( LOGIN_AUTH_ALERT, PASSWORD_AUTH_ALERT );
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
