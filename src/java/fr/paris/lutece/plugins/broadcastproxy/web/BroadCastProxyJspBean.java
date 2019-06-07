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

import fr.paris.lutece.plugins.broadcastproxy.service.BroadcastService;
import fr.paris.lutece.plugins.broadcastproxy.service.Constants;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Lobby features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageBroadcastProxy.jsp", controllerPath = "jsp/admin/plugins/broadcastproxy/", right = "BROADCASTPROXY_MANAGEMENT" )
public class BroadCastProxyJspBean extends MVCAdminJspBean
{
    // Templates
    private static final String TEMPLATE_TEST_BROADCASTPROXY = "/admin/plugins/broadcastproxy/managebroadcastproxy.html";
    private static final String JSP_TEST_BROADCASTPROXY = "jsp/admin/plugins/broadcastproxy/ManageBroadcastProxy.jsp";

    // actions & views
    private static final String VIEW_TEST_BROADCAST = "testBroadCast";

    // Parameters
    private static final String PARAMETER_USER_ID = "user_id";
    private static final String PARAMETER_SUBSCRIPTION_TYPE = "subscription_type";

    // Properties
    private static final String PROPERTY_PAGE_TITLE_BROADCASTPROXY = "broadcastproxy.pageTitle";

    // Markers
    private static final String MARK_SUBSCRIPTION_LIST = "subscription_list";
    private static final String MARK_SUBSCRIPTION_LIST_MAP = "subscription_list_map";
    private static final String MARK_SUBSCRIPTION_TYPE_LIST = "subscription_types";
    private static final String MARK_BROADCASTPROXY = "broadcastproxy";
    private static final String MARK_LAST_USER_ID = "last_user_id";
    private static final String MARK_LAST_SUBSCRIPTION_TYPE_ID = "last_subscription_type_id";

    // messages
    private static final String MSG_ERROR_GET_USER_SUBSCRIPTIONS = "Error while trying to get user Subscriptions";

    // instance variables
    ReferenceList _subscriptionTypes = null;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_TEST_BROADCAST, defaultView = true )
    public String getTestBroadCastProxy( HttpServletRequest request )
    {
        Map<String, Object> model = getModel( );
        initSubscriptionTypes( );

        if ( request.getParameter( PARAMETER_USER_ID ) != null )
        {
            String userId = request.getParameter( PARAMETER_USER_ID );
            int subscriptionTypeId = -1;
            try
            {
                subscriptionTypeId = Integer.parseInt( request.getParameter( PARAMETER_SUBSCRIPTION_TYPE ) );
            }
            catch( NumberFormatException e )
            {
                addError( "Invalid subscription type" );
            }

            try
            {
                Map<String, String> map = BroadcastService.getInstance( ).getUserSubscriptionsAsMap( userId,
                        _subscriptionTypes.get( subscriptionTypeId ).getName( ) );
                model.put( MARK_SUBSCRIPTION_LIST_MAP, map );

                String json = BroadcastService.getInstance( ).getUserSubscriptions( userId, _subscriptionTypes.get( subscriptionTypeId ).getName( ) );
                model.put( MARK_SUBSCRIPTION_LIST, json );

                model.put( MARK_BROADCASTPROXY, BroadcastService.getInstance( ).getName( ) );
                model.put( MARK_LAST_USER_ID, userId );
                model.put( MARK_LAST_SUBSCRIPTION_TYPE_ID, subscriptionTypeId );

            }
            catch( Exception e )
            {
                addError( MSG_ERROR_GET_USER_SUBSCRIPTIONS );
                AppLogService.error( e.getMessage( ) );
            }
        }

        model.put( MARK_SUBSCRIPTION_TYPE_LIST, _subscriptionTypes );

        return getPage( PROPERTY_PAGE_TITLE_BROADCASTPROXY, TEMPLATE_TEST_BROADCASTPROXY, model );
    }

    /**
     * init
     */
    private void initSubscriptionTypes( )
    {
        if ( _subscriptionTypes == null )
        {
            _subscriptionTypes = new ReferenceList( );
            _subscriptionTypes.addItem( 0, Constants.TYPE_ALERT );
            _subscriptionTypes.addItem( 1, Constants.TYPE_NEWSLETTER );
        }
    }

}
