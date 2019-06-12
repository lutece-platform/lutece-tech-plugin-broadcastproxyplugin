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

import fr.paris.lutece.plugins.broadcastproxy.business.Feed;
import fr.paris.lutece.plugins.broadcastproxy.business.Subscription;
import fr.paris.lutece.plugins.broadcastproxy.service.BroadcastService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceItem;
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
    private static final String JSP_TEST_BROADCASTPROXY = "jsp/admin/plugins/broadcastproxy/ManageBroadcastProxy.jsp";

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
                List<Subscription> list = BroadcastService.getInstance( ).getUserSubscriptionsAsList( userId,
                        _subscriptionTypes.get( subscriptionTypeId ).getName( ) );
                model.put( MARK_SUBSCRIPTION_LIST, list );

                String json = BroadcastService.getInstance( ).getUserSubscriptionsAsJson( userId, _subscriptionTypes.get( subscriptionTypeId ).getName( ) );
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
            String userName = request.getParameter( PARAMETER_USER_ID );
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

            // list of subscriptions (like Optin_*)
            Enumeration enum1 = request.getParameterNames( );
            while ( enum1.hasMoreElements( ) )
            {
                Object obj = enum1.nextElement( );
                String fieldName = (String) obj;
                String fieldValue = request.getParameter( fieldName );

                if ( fieldName.startsWith( "SUB_" ) )
                {
                    Subscription sub = new Subscription( );
                    sub.setName( fieldName );
                    sub.setActive( true );
                    sub.setUserName( userName );
                    sub.setType( _subscriptionTypes.get( subscriptionTypeId ).getName( ) );

                    // search additionnal data
                    Enumeration enum2 = request.getParameterNames( );
                    Map<String, String>  data = new HashMap<>();

                    while ( enum2.hasMoreElements( ) )
                    {
                        Object obj2 = enum2.nextElement( );
                        String fieldName2 = (String) obj2;
                        String fieldValue2 = request.getParameter( fieldName2 );

                        if ( fieldName2.startsWith( "data_" + sub.getName( ).substring( 6 ) + "_" ) )
                        {
                            String theme = fieldName2.substring( sub.getName( ).substring( 6 ).length( ) + 7 );
                            data.put( theme, theme );
                        }
                    }

                    if ( data.size( ) > 0 )
                        sub.setData( data );

                    subscriptionsList.add( sub );
                }
            }

            // update user subscriptions
            try
            {
                boolean result = BroadcastService.getInstance( ).updateSubscribtions( subscriptionsList );

                if ( result )
                {
                    addInfo( MSG_SUCCESS_UPDATE_USER_SUBSCRIPTIONS );
                }
                else
                {
                    addError( MSG_ERROR_GET_USER_SUBSCRIPTIONS );
                }

                model.put( MARK_BROADCASTPROXY, BroadcastService.getInstance( ).getName( ) );
                model.put( MARK_LAST_USER_ID, userName );
                model.put( MARK_LAST_SUBSCRIPTION_TYPE_ID, subscriptionTypeId );

            }
            catch( Exception esub )
            {
                addError( MSG_ERROR_GET_USER_SUBSCRIPTIONS );
                AppLogService.error( esub.getMessage( ) );
            }
        }

        model.put( MARK_SUBSCRIPTION_TYPE_LIST, _subscriptionTypes );        
        model.put( MARK_SUBSCRIPTION_FEED_LIST, _subscriptionFeeds );

        return getPage( PROPERTY_PAGE_TITLE_BROADCASTPROXY, TEMPLATE_TEST_BROADCASTPROXY, model );
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
            String userName = request.getParameter( PARAMETER_USER_ID );
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
            sub.setName( subscriptionId );
            sub.setActive( true );
            sub.setUserName( userName );
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

                model.put( MARK_BROADCASTPROXY, BroadcastService.getInstance( ).getName( ) );
                model.put( MARK_LAST_USER_ID, userName );
                model.put( MARK_LAST_SUBSCRIPTION_TYPE_ID, subscriptionTypeId );

            }
            catch( Exception esub )
            {
                addError( MSG_ERROR_GET_USER_SUBSCRIPTIONS );
                AppLogService.error( esub.getMessage( ) );
            }
        }

        model.put( MARK_SUBSCRIPTION_TYPE_LIST, _subscriptionTypes );        
        model.put( MARK_SUBSCRIPTION_FEED_LIST, _subscriptionFeeds );

        return getPage( PROPERTY_PAGE_TITLE_BROADCASTPROXY, TEMPLATE_TEST_BROADCASTPROXY, model );
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
            String userName = request.getParameter( PARAMETER_USER_ID );
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
            sub.setName( subscriptionId );
            sub.setActive( false );
            sub.setUserName( userName );
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

                model.put( MARK_BROADCASTPROXY, BroadcastService.getInstance( ).getName( ) );
                model.put( MARK_LAST_USER_ID, userName );
                model.put( MARK_LAST_SUBSCRIPTION_TYPE_ID, subscriptionTypeId );

            }
            catch( Exception esub )
            {
                addError( MSG_ERROR_GET_USER_SUBSCRIPTIONS );
                AppLogService.error( esub.getMessage( ) );
            }
        }

        model.put( MARK_SUBSCRIPTION_TYPE_LIST, _subscriptionTypes );        
        model.put( MARK_SUBSCRIPTION_FEED_LIST, _subscriptionFeeds );

        return getPage( PROPERTY_PAGE_TITLE_BROADCASTPROXY, TEMPLATE_TEST_BROADCASTPROXY, model );
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
            
            Map<String,String> mapTypes = new HashMap<>();
            for ( Feed feed : _subscriptionFeeds ) 
            {
                mapTypes.put(feed.getType( ), feed.getType( ) );
            }
            
            int i=0;
            for (String feedType : mapTypes.keySet( ) )
            {
                if (_currentFeedType == null ) _currentFeedType = feedType;
                
                _subscriptionTypes.addItem( String.valueOf(i++), feedType );
            }
        }        
    }
            

}
