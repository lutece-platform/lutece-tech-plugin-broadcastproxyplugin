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
package fr.paris.lutece.plugins.broadcastproxy.web;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.broadcastproxy.service.BroadcastService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.l10n.LocaleService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.json.ErrorJsonResponse;
import fr.paris.lutece.util.json.JsonResponse;
import fr.paris.lutece.util.json.JsonUtil;

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

    private static final String KEY_USER_INFO_MAIL = "broadcastproxy.userInfoKeys.mail";

    private static final String ACTION_UPDATE_USER_SUBSCRIPTIONS = "updateUserSubscriptions";
    private static final String ACTION_GET_USER_SUBSCRIPTIONS = "getUserSubscriptions";

    private static final String PROPERTY_MSG_ERROR_GET_USER_SUBSCRIPTIONS = "broadcastproxy.msg.ERROR_GET_USER_SUBSCRIPTIONS";

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
                String userMail = user.getEmail( );
                if ( StringUtils.isBlank( userMail ) )
                {
                    String mailUserInfoKey = AppPropertiesService.getProperty( KEY_USER_INFO_MAIL );
                    if ( !StringUtils.isBlank( mailUserInfoKey ) )
                    {
                        userMail = user.getUserInfo( mailUserInfoKey );
                    }
                }
                return userMail;
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
     * Do get user subscriptions using the AJAX mode
     * 
     * @param request
     *            The request
     * @return
     */
    @Action( ACTION_GET_USER_SUBSCRIPTIONS )
    public XPage doGetUserSubscriptions( HttpServletRequest request )
    {
        ObjectMapper mapper = new ObjectMapper( );
        JsonNode userSubscriptionsJsonNode = null;

        String mailUser = getMailUserAuthenticated( request );

        if ( StringUtils.isBlank( mailUser ) )
        {
            return responseJSON( JsonUtil.buildJsonResponse( new ErrorJsonResponse( "User not authentified." ) ) );
        }

        try
        {
            BroadcastService broadcastService = BroadcastService.getInstance( );
            String userSubscriptions = broadcastService.getUserSubscriptionsAsJson( mailUser );
            if ( userSubscriptions == null )
            {
            	String returnedMsg = "Vos newsletters sont momentanément indisponibles.";
            	return responseJSON( JsonUtil.buildJsonResponse( new ErrorJsonResponse( returnedMsg ) ) );
            }

            userSubscriptionsJsonNode = mapper.readTree( userSubscriptions );
        }
        catch( Exception e )
        {
            addInfo( I18nService.getLocalizedString( PROPERTY_MSG_ERROR_GET_USER_SUBSCRIPTIONS, LocaleService.getDefault( ) ) );
            AppLogService.error( e.getMessage( ) );
            return responseJSON( JsonUtil.buildJsonResponse( new ErrorJsonResponse( e.getMessage( ) ) ) );
        }

        XPage xpage = responseJSON( JsonUtil.buildJsonResponse( new JsonResponse( userSubscriptionsJsonNode ) ) );

        return xpage;
    }

    /**
     * Do update user subscriptions using the AJAX mode
     * 
     * json data should be like : { "userSubscriptions": {"typeName": "ALERT", "groupName": "Alertes", "subscriptionsList": [ {"id": "", "active": true} ] } }
     * 
     * IF there is not "groupName" (exemple for newsletter), do ["groupName": "NONE"]
     * 
     * @param request
     *            The request
     * @return
     */
    @Action( ACTION_UPDATE_USER_SUBSCRIPTIONS )
    public XPage doUpdateUserSubscriptions( HttpServletRequest request )
    {

        String mailUser = getMailUserAuthenticated( request );

        if ( StringUtils.isBlank( mailUser ) )
            return responseJSON( JsonUtil.buildJsonResponse( new ErrorJsonResponse( "User not authentified." ) ) );

        String strJson;
        try
        {
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

        if ( updateSubscriptions( strJson, mailUser ) != true )
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
    private boolean updateSubscriptions( String jsonResponse, String userId )
    {
        try
        {
            // update subscriptions by feed type
            BroadcastService.getInstance( ).updateSubscribtions( userId, jsonResponse );
        }
        catch( Exception e )
        {
            AppLogService.error( "An error occured while updating subscriptions : " + e.getMessage( ) );
            return false;
        }

        return true;
    }

}
