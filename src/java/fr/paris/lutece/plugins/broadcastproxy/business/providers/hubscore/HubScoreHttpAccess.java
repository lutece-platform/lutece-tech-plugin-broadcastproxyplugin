package fr.paris.lutece.plugins.broadcastproxy.business.providers.hubscore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 *
 * @author Oughdi
 */
public class HubScoreHttpAccess {	

    // get proxy from HttpAccess properties
    private static final String PROXY_ADR = AppPropertiesService.getProperty( "httpAccess.proxyHost" );
    private static final int PROXY_PORT = AppPropertiesService.getPropertyInt( "httpAccess.proxyPort", 3128 );

	public HttpResponse doGet( String strUrl, Map<String, String> headers ) {
		
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet method = new HttpGet(strUrl);
		HttpResponse httpResponse = null;		

		try {
			// add request header
			if ( headers != null )
			{
			    for ( String headerType : headers.keySet( ) )
			    {
			        method.setHeader( headerType, headers.get( headerType ) );
			    }
			}	

			// add proxy
			HttpHost proxy = new HttpHost( PROXY_ADR, PROXY_PORT );
			RequestConfig config = RequestConfig.custom( ).setProxy( proxy ).build( );
			method.setConfig( config );
			
			// Execute method		
			httpResponse = client.execute(method);

		} catch (IOException e) {
		    String strError = "HttpGet - Error connecting to '" + strUrl + "' : ";
		    AppLogService.error( strError + e.getMessage( ), e );
		    throw new AppException("strError", e);
		}	
		finally
		{
		    // Release the connection.
		    method.releaseConnection( );
		}
		
        return httpResponse;
    }
	
	
	public HttpResponse doPost( String strUrl, List<BasicNameValuePair> params, Map<String, String> headers ) {
		
		CloseableHttpClient client = HttpClientBuilder.create( ).build( );
		HttpResponse httpResponse = null;
		HttpPost method = new HttpPost( strUrl );
		
		try
		{
			// Add headers
			if ( headers != null )
			{
			    for ( String headerType : headers.keySet( ) )
			    {
			        method.setHeader( headerType, headers.get( headerType ) );
			    }
			}
			
			// Add parameters
			List<NameValuePair> listDatas = new ArrayList<>( );
			for (BasicNameValuePair param : params) {
				listDatas.add(param);
			}		
			
			method.setHeader( "Content-Type", "application/x-www-form-urlencoded" );
			method.setEntity( new UrlEncodedFormEntity( listDatas ) );
				
		    // add proxy
		    HttpHost proxy = new HttpHost( PROXY_ADR, PROXY_PORT );
		    RequestConfig config = RequestConfig.custom( ).setProxy( proxy ).build( );
		    method.setConfig( config );
		
		    httpResponse = client.execute( method );
		}
		catch( IOException e )
		{
		    String strError = "HttpPost - Error connecting to '" + strUrl + "' : ";
		    AppLogService.error( strError + e.getMessage( ), e );
		    throw new AppException("strError", e);
		}
		finally
		{
		    // Release the connection.
		    method.releaseConnection( );
		}
				
		return httpResponse;
    }

	public HttpResponse doPatch( String strUrl, List<BasicNameValuePair> params, Map<String, String> headers ) {
		
		CloseableHttpClient client = HttpClientBuilder.create( ).build( );
		HttpResponse httpResponse = null;
		HttpPatch method = new HttpPatch( strUrl );

		try
		{
			if ( headers != null )
			{
			    for ( String headerType : headers.keySet( ) )
			    {
			        method.setHeader( headerType, headers.get( headerType ) );
			    }
			}

			// Add parameters
			List<NameValuePair> listDatas = new ArrayList<>( );
			for (BasicNameValuePair param : params) {
				listDatas.add(param);
			}		
			
			method.setHeader( "Content-Type", "application/x-www-form-urlencoded" );
			method.setEntity( new UrlEncodedFormEntity( listDatas ) );
		
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
		    throw new AppException("strError", e);
		}
		finally
		{
		    // Release the connection.
		    method.releaseConnection( );
		}
		
		return httpResponse;
	}

	public HttpResponse doDelete( String strUrl, Map<String, String> headers ) {
		
		CloseableHttpClient client = HttpClientBuilder.create( ).build( );
		HttpResponse httpResponse = null;
		HttpDelete method = new HttpDelete( strUrl );
		
		try
		{
			// Add headers
			if ( headers != null )
			{
			    for ( String headerType : headers.keySet( ) )
			    {
			        method.setHeader( headerType, headers.get( headerType ) );
			    }
			}
						
			method.setHeader( "Content-Type", "application/x-www-form-urlencoded" );
					
		    // add proxy
		    HttpHost proxy = new HttpHost( PROXY_ADR, PROXY_PORT );
		    RequestConfig config = RequestConfig.custom( ).setProxy( proxy ).build( );
		    method.setConfig( config );
		
		    httpResponse = client.execute( method );
		}
		catch( IOException e )
		{
		    String strError = "HttpPost - Error connecting to '" + strUrl + "' : ";
		    AppLogService.error( strError + e.getMessage( ), e );
		    throw new AppException("strError", e);
		}
		finally
		{
		    // Release the connection.
		    method.releaseConnection( );
		}
				
		return httpResponse;
    }


	
}
