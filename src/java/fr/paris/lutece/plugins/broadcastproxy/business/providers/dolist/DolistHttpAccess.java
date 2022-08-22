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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class DolistHttpAccess
{
    // get proxy from HttpAccess properties
    private static final String PROXY_ADR = AppPropertiesService.getProperty( "broadcastproxy.proxyHost" );
    private static final int PROXY_PORT = AppPropertiesService.getPropertyInt( "broadcastproxy.proxyPort", 3128 );

    public HttpResponse doGet( String strUrl, Map<String, String> headers )
    {
        CloseableHttpClient client = HttpClientBuilder.create( ).build( );
        HttpGet method = new HttpGet( strUrl );
        HttpResponse httpResponse = null;

        try
        {
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
            httpResponse = client.execute( method );

        }
        catch( IOException e )
        {
            String strError = "HttpGet - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage( ), e );
            throw new AppException( "strError", e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection( );
        }

        return httpResponse;
    }

    public String doPost( String strUrl, String jsonParams, Map<String, String> headers )
    {
        CloseableHttpClient client = HttpClientBuilder.create( ).build( );
        HttpResponse httpResponse = null;
        String strResponse = null;
        HttpPost method = new HttpPost( strUrl );

        try
        {
            // Add headershttpPost
            if ( headers != null )
            {
                for ( String headerType : headers.keySet( ) )
                {
                    method.setHeader( headerType, headers.get( headerType ) );
                }
            }

            StringEntity entity = new StringEntity( jsonParams );
            method.setEntity( entity );

            // add proxy
            HttpHost proxy = new HttpHost( PROXY_ADR, PROXY_PORT );
            RequestConfig config = RequestConfig.custom( ).setProxy( proxy ).build( );
            method.setConfig( config );

            httpResponse = client.execute( method );

            // If error
            if ( httpResponse != null && httpResponse.getStatusLine( ).getStatusCode( ) != 200 )
            {
                AppLogService.error( "Returned Dolist error : " + strResponse );
            }
            
            // Get response in String
            strResponse = httpToStrResponse( httpResponse );
        }
        catch( IOException e )
        {
            String strError = "HttpPost - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage( ), e );
            throw new AppException( "strError", e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection( );
        }

        return strResponse;
    }

    public String doPut( String strUrl, String jsonParams, Map<String, String> headers )
    {
        CloseableHttpClient client = HttpClientBuilder.create( ).build( );
        HttpResponse httpResponse = null;
        String strResponse = null;
        HttpPut method = new HttpPut( strUrl );

        try
        {
            // Add headershttpPut
            if ( headers != null )
            {
                for ( String headerType : headers.keySet( ) )
                {
                    method.setHeader( headerType, headers.get( headerType ) );
                }
            }

            StringEntity entity = new StringEntity( jsonParams );
            method.setEntity( entity );

            // add proxy
            HttpHost proxy = new HttpHost( PROXY_ADR, PROXY_PORT );
            RequestConfig config = RequestConfig.custom( ).setProxy( proxy ).build( );
            method.setConfig( config );

            httpResponse = client.execute( method );            

            // If error
            if ( httpResponse != null && httpResponse.getStatusLine( ).getStatusCode( ) != 200 )
            {
                AppLogService.error( "Returned Dolist error : " + strResponse );
            }

            // Get response in String
            strResponse = httpToStrResponse( httpResponse );
        }
        catch( IOException e )
        {
            String strError = "HttpPut - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage( ), e );
            throw new AppException( "strError", e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection( );
        }
        
        return strResponse;
    }

    public HttpResponse doDelete( String strUrl, Map<String, String> headers )
    {
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
            String strError = "HttpDelete - Error connecting to '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage( ), e );
            throw new AppException( "strError", e );
        }
        finally
        {
            // Release the connection.
            method.releaseConnection( );
        }

        return httpResponse;
    }
    

    /**
     * Stringify httpResponse
     * 
     * @param httpResponse
     * @return the response as string
     * @throws IOException
     */
    private String httpToStrResponse( HttpResponse httpResponse ) throws IOException
    {
        StringBuilder strResponse = new StringBuilder( );

        // Get response data in string
        if ( httpResponse != null && httpResponse.getEntity( ) != null && httpResponse.getEntity( ).getContent( ) != null )
        {
            BufferedReader bufferedreader = new BufferedReader( new InputStreamReader( httpResponse.getEntity( ).getContent( ) ) );
            String line = "";
            while ( ( line = bufferedreader.readLine( ) ) != null )
            {
                strResponse.append( line );
            }
        }

        return strResponse.toString( );
    }
}
