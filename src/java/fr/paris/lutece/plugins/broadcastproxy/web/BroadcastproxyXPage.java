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
package fr.paris.lutece.plugins.broadcastproxy.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.paris.lutece.plugins.broadcastproxy.business.Subscription;
import fr.paris.lutece.plugins.broadcastproxy.service.BroadcastService;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.json.ErrorJsonResponse;
import fr.paris.lutece.util.json.JsonResponse;
import fr.paris.lutece.util.json.JsonUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MyLuteceParisConnectXPage
 *
 */
@Controller( xpageName = BroadcastproxyXPage.PAGE_BROADCAST_MYDASHBOARD, pageTitleI18nKey = "broadcastproxy.xpage.pageTitle", pagePathI18nKey = "broadcastproxy.xpage.pagePathLabel" )
public class BroadcastproxyXPage extends MVCApplication
{
    /**
     * Name of this application
     */
    public static final String PAGE_BROADCAST_MYDASHBOARD = "broadcastproxyMyDashboard";
    private static final long serialVersionUID = -4316691400124512414L;

    private static final String ACTION_UPDATE_USER_SUBSCRIPTIONS = "updateUserSubscriptions";

    /**
     * Check if the current (front) user is authenticated
     * 
     * @param request
     *            The request
     * @return true if authenticated
     */
    private String getMailUserAuthenticated( HttpServletRequest request )
    {
        LuteceUser user = null;

        if ( SecurityService.isAuthenticationEnable( ) )
        {
            user = SecurityService.getInstance( ).getRegisteredUser( request );
            if ( user != null )
            {
                return user.getEmail( );
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Do update user subscriptions using the AJAX mode
     * 
     * json data should be like : { "userId": "xxxx.yyyyy@gmail.com", "feedTypes": [ {"id": "ALERT", "subscriptions": [ {"id": "Paris", "type": "ALERT",
     * "active": "0", "data": [] }, {"id": "Alerte", "type": "ALERT", "active": "1", "data": [ {"id": "bmo", "active": "1" }, {"id": "CMA", "active": "0" } ] }
     * ] } ] }
     * 
     * @param request
     *            The request
     * @return
     */
    @Action( ACTION_UPDATE_USER_SUBSCRIPTIONS )
    public XPage doUpdateUserSubscriptions( HttpServletRequest request )
    {

        String mailUser = getMailUserAuthenticated( request );

        if ( mailUser == null )
            return responseJSON( JsonUtil.buildJsonResponse( new ErrorJsonResponse( "User not authentified." ) ) );

        String strJson;
        try
        {
            // strJson = request.getReader().lines().collect(Collectors.joining());

            StringBuilder sb = new StringBuilder( );
            String line = null;

            BufferedReader reader = request.getReader( );
            while ( ( line = reader.readLine( ) ) != null )
                sb.append( line );

            strJson = sb.toString( );
        }
        catch( IOException e )
        {
            return responseJSON( JsonUtil.buildJsonResponse( new ErrorJsonResponse( "An error occured while receiving the response" ) ) );
        }

        if ( updateSubscriptions( strJson ) != true )
        {
            responseJSON( JsonUtil.buildJsonResponse( new ErrorJsonResponse( "An error occured while receiving the response" ) ) );
        }

        return responseJSON( JsonUtil.buildJsonResponse( new JsonResponse( "ok" ) ) );
    }

    /**
     * update Subscriptions
     * 
     * @param jsonResponse
     * @return true if successful
     */
    private boolean updateSubscriptions( String jsonResponse )
    {

        ObjectMapper mapper = new ObjectMapper( );

        try
        {
            JsonNode jsonNode = mapper.readTree( jsonResponse );

            String userId = jsonNode.get( "userId" ).asText( );
            ArrayNode arrayFeedTypesNode = (ArrayNode) jsonNode.get( "feedTypes" );

            if ( arrayFeedTypesNode != null && arrayFeedTypesNode.size( ) > 0 )
            {
                Iterator<JsonNode> feedTypeIterator = arrayFeedTypesNode.elements( );

                while ( feedTypeIterator.hasNext( ) )
                {
                    JsonNode feedNode = feedTypeIterator.next( );
                    String feedTypeId = feedNode.get( "id" ).asText( );

                    List<Subscription> subscriptionList = new ArrayList<>( );

                    ArrayNode arraySubscriptionsNode = (ArrayNode) feedNode.get( "subscriptions" );

                    if ( arraySubscriptionsNode != null && arraySubscriptionsNode.size( ) > 0 )
                    {
                        Iterator<JsonNode> subscriptionIterator = arraySubscriptionsNode.elements( );

                        while ( subscriptionIterator.hasNext( ) )
                        {
                            JsonNode subNode = subscriptionIterator.next( );
                            String subId = subNode.get( "id" ).asText( );
                            String active = subNode.get( "active" ).asText( );
                            String type = subNode.get( "type" ).asText( );
                            ArrayNode arrayDataNode = (ArrayNode) subNode.get( "data" );

                            Subscription sub = new Subscription( );
                            sub.setId( subId );
                            sub.setUserId( userId );
                            sub.setType( feedTypeId );
                            sub.setActive( active.equals( "1" ) );

                            if ( arrayDataNode != null && arrayDataNode.size( ) > 0 )
                            {
                                Map<String, String> mapData = new HashMap<>( );

                                Iterator<JsonNode> dataIterator = arrayDataNode.elements( );
                                while ( dataIterator.hasNext( ) )
                                {
                                    JsonNode dataNode = dataIterator.next( );
                                    mapData.put( dataNode.get( "id" ).asText( ), dataNode.get( "active" ).asText( ) );
                                }

                                sub.setData( mapData );
                            }

                            subscriptionList.add( sub );
                        }
                    }

                    // update subscriptions by feed type
                    BroadcastService.getInstance( ).updateSubscribtions( subscriptionList );
                }
            }

        }
        catch( Exception e )
        {
            AppLogService.error( "An error occured while updating subscriptions : " + e.getMessage( ) );
            return false;
        }

        return true;
    }

}
