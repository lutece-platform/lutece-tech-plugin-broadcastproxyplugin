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
package fr.paris.lutece.plugins.broadcastproxy.web;

import fr.paris.lutece.plugins.broadcastproxy.business.Feed;
import fr.paris.lutece.plugins.broadcastproxy.business.Subscription;
import fr.paris.lutece.plugins.broadcastproxy.service.BroadcastService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Lobby features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageBroadcastProxy.jsp", controllerPath = "jsp/admin/plugins/broadcastproxy/", right = "BROADCASTPROXY_MANAGEMENT" )
public class BroadcastproxyJspBean extends MVCAdminJspBean
{
    // Templates
    private static final String TEMPLATE_TEST_BROADCASTPROXY = "/admin/plugins/broadcastproxy/managebroadcastproxy.html";

    // actions & views
    private static final String VIEW_TEST_BROADCAST = "testBroadCast";
    private static final String ACTION_UPDATE_USER_SUBSCRIPTIONS = "updateUserSubscriptions";
    private static final String ACTION_UNSUBSCRIBE = "unsubscribe";
    private static final String ACTION_SUBSCRIBE = "subscribe";

    // Parameters
    private static final String PARAMETER_USER_ID = "user_id";
    private static final String PARAMETER_SUBSCRIPTION_TYPE = "subscription_type";
    private static final String PARAMETER_SUBSCRIPTION_ID = "subscription_id";

    // Properties
    private static final String PROPERTY_PAGE_TITLE_BROADCASTPROXY = "broadcastproxy.pageTitle";

    // Markers
    private static final String MARK_SUBSCRIPTION_JSON = "subscription_json";
    private static final String MARK_SUBSCRIPTION_LIST = "subscription_list";
    private static final String MARK_SUBSCRIPTION_TYPE_LIST = "subscription_types";
    private static final String MARK_SUBSCRIPTION_FEED_LIST = "subscription_feeds";
    private static final String MARK_BROADCASTPROXY = "broadcastproxy";
    private static final String MARK_LAST_USER_ID = "last_user_id";
    private static final String MARK_LAST_SUBSCRIPTION_TYPE_ID = "last_subscription_type_id";

    // messages
    private static final String MSG_ERROR_GET_USER_SUBSCRIPTIONS = "Error while trying to get user Subscriptions";
    private static final String MSG_SUCCESS_UPDATE_USER_SUBSCRIPTIONS = "Update successful";

    // instance variables
    ReferenceList _subscriptionTypes = null;
    List<Feed> _subscriptionFeeds = null;
    String _currentFeedType = null;

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
        initSubscriptionFeeds( );

        if ( request.getParameter( PARAMETER_USER_ID ) != null )
        {
            String userId = request.getParameter( PARAMETER_USER_ID );
            int subscriptionTypeId = -1;
            try
            {
                subscriptionTypeId = Integer.parseInt( request.getParameter( PARAMETER_SUBSCRIPTION_TYPE ) );
                _currentFeedType = _subscriptionTypes.get( subscriptionTypeId ).getName( );
            }
            catch( NumberFormatException e )
            {
                addError( "Invalid subscription type" );
            }

            try
            {
                // List<Subscription> list = BroadcastService.getInstance( ).getUserSubscriptionsAsList( userId,
                // _subscriptionTypes.get( subscriptionTypeId ).getName( ) );

                List<Subscription> list = null;
                model.put( MARK_SUBSCRIPTION_LIST, list );

                String json = BroadcastService.getInstance( ).getUserSubscriptionsAsJson( userId );
                model.put( MARK_SUBSCRIPTION_JSON, json );

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
        model.put( MARK_SUBSCRIPTION_FEED_LIST, _subscriptionFeeds );

        return getPage( PROPERTY_PAGE_TITLE_BROADCASTPROXY, TEMPLATE_TEST_BROADCASTPROXY, model );
    }

    /**
     * Update action
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @Action( value = ACTION_UPDATE_USER_SUBSCRIPTIONS )
    public String doUpdateUserSubscribtions( HttpServletRequest request )
    {
        Map<String, Object> model = getModel( );
        initSubscriptionFeeds( );

        List<Subscription> subscriptionsList = new ArrayList<>( );

        if ( request.getParameter( PARAMETER_USER_ID ) != null )
        {
            String userId = request.getParameter( PARAMETER_USER_ID );
            int subscriptionTypeId = -1;
            try
            {
                subscriptionTypeId = Integer.parseInt( request.getParameter( PARAMETER_SUBSCRIPTION_TYPE ) );
                _currentFeedType = _subscriptionTypes.get( subscriptionTypeId ).getName( );
            }
            catch( NumberFormatException e )
            {
                addError( "Invalid subscription type" );
            }

            // Init subscription list with all feeds (checkbox unchecked are not present in request)
            for ( Feed feed : _subscriptionFeeds )
            {
                if ( feed.getType( ).equals( _currentFeedType ) )
                {
                    // init sub
                    Subscription sub = new Subscription( );
                    sub.setId( feed.getId( ) );
                    sub.setActive( false );
                    sub.setUserId( userId );
                    sub.setType( feed.getType( ) );
                    for ( String data : feed.getData( ).keySet( ) )
                    {
                        sub.addDataItem( data, "0" );
                    }

                    subscriptionsList.add( sub );
                }
            }

            // Update states of subscription
            Enumeration enum1 = request.getParameterNames( );
            while ( enum1.hasMoreElements( ) )
            {
                Object obj = enum1.nextElement( );
                String fieldName = (String) obj;
                String fieldValue = request.getParameter( fieldName );

                // set subscription states
                if ( fieldName.startsWith( "SUB_" + _currentFeedType + "_" ) )
                {
                    String feedId = fieldName.substring( _currentFeedType.length( ) + 5 );
                    for ( Subscription sub : subscriptionsList )
                    {
                        if ( sub.getId( ).equals( feedId ) )
                            sub.setActive( true );
                    }
                }

                // set subscription data state
                if ( fieldName.startsWith( "DATA_" + _currentFeedType + "_" ) )
                {
                    String feedIdAndData = fieldName.substring( _currentFeedType.length( ) + 6 );
                    String feedId = feedIdAndData.substring( 0, feedIdAndData.indexOf( "_" ) ); // feed id MUST NOT contain underscores
                    String data = feedIdAndData.substring( feedId.length( ) + 1 );

                    for ( Subscription sub : subscriptionsList )
                    {
                        if ( sub.getId( ).equals( feedId ) )
                        {
                            sub.addDataItem( data, "1" );
                        }
                    }
                }
            }

            // update user subscriptions
            try
            {
                boolean result = BroadcastService.getInstance( ).updateSubscribtions( userId, subscriptionsList );

                if ( result )
                {
                    addInfo( MSG_SUCCESS_UPDATE_USER_SUBSCRIPTIONS );
                }
                else
                {
                    addError( MSG_ERROR_GET_USER_SUBSCRIPTIONS );
                }

            }
            catch( Exception esub )
            {
                addError( MSG_ERROR_GET_USER_SUBSCRIPTIONS );
                AppLogService.error( esub.getMessage( ) );
            }
        }

        return getTestBroadCastProxy( request );
    }

    /**
     * Subscribe
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @Action( value = ACTION_SUBSCRIBE )
    public String doSubscribe( HttpServletRequest request )
    {
        Map<String, Object> model = getModel( );
        initSubscriptionFeeds( );

        if ( request.getParameter( PARAMETER_USER_ID ) != null )
        {
            String userId = request.getParameter( PARAMETER_USER_ID );
            int subscriptionTypeId = -1;
            try
            {
                subscriptionTypeId = Integer.parseInt( request.getParameter( PARAMETER_SUBSCRIPTION_TYPE ) );
                _currentFeedType = _subscriptionTypes.get( subscriptionTypeId ).getName( );
            }
            catch( NumberFormatException e )
            {
                addError( "Invalid subscription type" );
            }

            String subscriptionId = request.getParameter( PARAMETER_SUBSCRIPTION_ID );

            Subscription sub = new Subscription( );
            sub.setId( subscriptionId );
            sub.setActive( true );
            sub.setUserId( userId );
            sub.setType( _subscriptionTypes.get( subscriptionTypeId ).getName( ) );

            // update user subscription
            try
            {
                boolean result = BroadcastService.getInstance( ).update( sub );

                if ( result )
                {
                    addInfo( MSG_SUCCESS_UPDATE_USER_SUBSCRIPTIONS );
                }
                else
                {
                    addError( MSG_ERROR_GET_USER_SUBSCRIPTIONS );
                }

            }
            catch( Exception esub )
            {
                addError( MSG_ERROR_GET_USER_SUBSCRIPTIONS );
                AppLogService.error( esub.getMessage( ) );
            }
        }

        return getTestBroadCastProxy( request );
    }

    /**
     * Unsubscribe
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @Action( value = ACTION_UNSUBSCRIBE )
    public String doUnsubscribe( HttpServletRequest request )
    {
        Map<String, Object> model = getModel( );
        initSubscriptionFeeds( );

        if ( request.getParameter( PARAMETER_USER_ID ) != null )
        {
            String userId = request.getParameter( PARAMETER_USER_ID );
            int subscriptionTypeId = -1;
            try
            {
                subscriptionTypeId = Integer.parseInt( request.getParameter( PARAMETER_SUBSCRIPTION_TYPE ) );
                _currentFeedType = _subscriptionTypes.get( subscriptionTypeId ).getName( );
            }
            catch( NumberFormatException e )
            {
                addError( "Invalid subscription type" );
            }

            String subscriptionId = request.getParameter( PARAMETER_SUBSCRIPTION_ID );

            Subscription sub = new Subscription( );
            sub.setId( subscriptionId );
            sub.setActive( false );
            sub.setUserId( userId );
            sub.setType( _subscriptionTypes.get( subscriptionTypeId ).getName( ) );

            // update user subscriptions
            try
            {
                boolean result = BroadcastService.getInstance( ).update( sub );

                if ( result )
                {
                    addInfo( MSG_SUCCESS_UPDATE_USER_SUBSCRIPTIONS );
                }
                else
                {
                    addError( MSG_ERROR_GET_USER_SUBSCRIPTIONS );
                }

            }
            catch( Exception esub )
            {
                addError( MSG_ERROR_GET_USER_SUBSCRIPTIONS );
                AppLogService.error( esub.getMessage( ) );
            }
        }

        return getTestBroadCastProxy( request );
    }

    /**
     * init
     */
    private void initSubscriptionFeeds( )
    {
        if ( _subscriptionFeeds == null )
        {
            _subscriptionTypes = new ReferenceList( );
            _subscriptionFeeds = BroadcastService.getInstance( ).getFeeds( );

            Map<String, String> mapTypes = new HashMap<>( );
            for ( Feed feed : _subscriptionFeeds )
            {
                mapTypes.put( feed.getType( ), feed.getType( ) );
            }

            int i = 0;
            for ( String feedType : mapTypes.keySet( ) )
            {
                if ( _currentFeedType == null )
                    _currentFeedType = feedType;

                _subscriptionTypes.addItem( String.valueOf( i++ ), feedType );
            }
        }
    }

}
