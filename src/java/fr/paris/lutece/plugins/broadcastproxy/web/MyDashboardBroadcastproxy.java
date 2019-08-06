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
import fr.paris.lutece.plugins.broadcastproxy.service.Constants;
import fr.paris.lutece.plugins.mydashboard.service.MyDashboardComponent;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.portal.web.l10n.LocaleService;
import fr.paris.lutece.util.ErrorMessage;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.codehaus.plexus.util.StringUtils;

public class MyDashboardBroadcastproxy extends MyDashboardComponent
{
    // PROPERTIES
    private static final String PROPERTY_MYDASHBOARD_DESCRIPTION = "broadcastproxy.myDashboard.description";
    private static final String PROPERTY_MSG_ERROR_GET_USER_SUBSCRIPTIONS = "broadcastproxy.msg.ERROR_GET_USER_SUBSCRIPTIONS";

    // Markers
    private static final String MARK_SUBSCRIPTION_LIST_BY_TYPE = "subscription_list_by_type";
    private static final String MARK_SUBSCRIPTION_TYPE_LIST = "subscription_types";
    private static final String MARK_SUBSCRIPTION_FEED_LIST_BY_TYPE = "subscription_feeds_by_type";
    private static final String MARK_BROADCASTPROXY = "broadcastproxy";
    private static final String MARK_LUTECE_USER = "user";
    private static final String MARK_INFOS = "infos";

    // instance variables
    ReferenceList _subscriptionTypes = null;
    Map<String, List<Feed>> _subscriptionFeedsByType = null;
    String [ ] _feedTypes = null;
    private List<ErrorMessage> _listInfos = new ArrayList<>( );

    // constants
    private static final String DS_KEY_FEEDTYPES = "broadcastproxy.site_property.mydashboard.feedtypes";
    private static final String MYDASHBOARD_BROADCASTPROXY_ID = "broadcastproxy.myDashboard";
    private static final String KEY_USER_INFO_MAIL = "broadcastproxy.userInfoKeys.mail";

    // Templates
    private static final String TEMPLATE_DASHBOARD = "skin/plugins/mydashboard/modules/broadcastproxy/broadcastproxy_mydashboard.html";

    // SESSION DATA
    private LuteceUser _luteceUser;
    private String _plugin = "broadcastproxy";

    @Override
    public String getDashboardData( HttpServletRequest request )
    {
        _listInfos.clear( );
        Map<String, Object> model = new HashMap<>( );

        if ( SecurityService.isAuthenticationEnable( ) )
        {
            _luteceUser = SecurityService.getInstance( ).getRegisteredUser( request );
            if ( _luteceUser != null )
            {
                model.put( MARK_LUTECE_USER, _luteceUser );
            }
        }

        if ( _luteceUser != null )
        {

            initSubscriptionFeeds( );

            Map<String, List<Subscription>> listSubscriptionsByType = new HashMap<>( );
            String userMail = _luteceUser.getEmail( );
            if ( StringUtils.isBlank( userMail ) )
            {
                String mailUserInfoKey = AppPropertiesService.getProperty( KEY_USER_INFO_MAIL );
                if ( !StringUtils.isBlank( mailUserInfoKey ) )
                {
                    userMail = _luteceUser.getUserInfo( mailUserInfoKey );
                }
            }

            for ( String feedType : _feedTypes )
            {
                if ( StringUtils.isBlank( feedType ) )
                    continue;

                try
                {
                    List<Subscription> list = BroadcastService.getInstance( ).getUserSubscriptionsAsList( userMail, feedType );
                    listSubscriptionsByType.put( feedType, list );

                    model.put( MARK_BROADCASTPROXY, BroadcastService.getInstance( ).getName( ) );
                }
                catch( Exception e )
                {
                    addInfo( I18nService.getLocalizedString( PROPERTY_MSG_ERROR_GET_USER_SUBSCRIPTIONS, LocaleService.getDefault( ) ) );
                    AppLogService.error( e.getMessage( ) );
                }
            }

            model.put( MARK_SUBSCRIPTION_LIST_BY_TYPE, listSubscriptionsByType );

            model.put( MARK_SUBSCRIPTION_TYPE_LIST, _subscriptionTypes );
            model.put( MARK_SUBSCRIPTION_FEED_LIST_BY_TYPE, _subscriptionFeedsByType );
            model.put( MARK_INFOS, _listInfos );
        }

        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_DASHBOARD, LocaleService.getUserSelectedLocale( request ), model );

        return t.getHtml( );
    }

    @Override
    public String getComponentId( )
    {
        return MYDASHBOARD_BROADCASTPROXY_ID;
    }

    @Override
    public String getComponentDescription( Locale locale )
    {
        return I18nService.getLocalizedString( PROPERTY_MYDASHBOARD_DESCRIPTION, locale );
    }

    /**
     * init
     */
    private void initSubscriptionFeeds( )
    {
        if ( _subscriptionFeedsByType == null )
        {
            String strFeedTypes = DatastoreService.getDataValue( DS_KEY_FEEDTYPES, Constants.TYPE_NEWSLETTER );
            if ( !StringUtils.isBlank( strFeedTypes ) )
            {
                _feedTypes = strFeedTypes.split( "," );
                _subscriptionFeedsByType = new HashMap<>( );
                _subscriptionTypes = new ReferenceList( );

                for ( String feedType : _feedTypes )
                {
                    _subscriptionFeedsByType.put( feedType, new ArrayList<>( ) );
                    _subscriptionTypes.addItem( feedType, "Description of " + feedType );
                }

                List<Feed> listFeeds = BroadcastService.getInstance( ).getFeeds( );
                for ( Feed feed : listFeeds )
                {
                    if ( _subscriptionFeedsByType.get( feed.getType( ) ) != null )
                    {
                        _subscriptionFeedsByType.get( feed.getType( ) ).add( feed );
                    }
                }
            }
        }
    }

    /**
     * Add an info message
     * 
     * @param strMessage
     *            The message
     */
    protected void addInfo( String strMessage )
    {
        _listInfos.add( new MVCMessage( strMessage ) );
    }

    /**
     * Add an info message
     * 
     * @param strMessageKey
     *            The message key
     * @param locale
     *            The locale
     */
    protected void addInfo( String strMessageKey, Locale locale )
    {
        _listInfos.add( new MVCMessage( I18nService.getLocalizedString( strMessageKey, locale ) ) );
    }
}
