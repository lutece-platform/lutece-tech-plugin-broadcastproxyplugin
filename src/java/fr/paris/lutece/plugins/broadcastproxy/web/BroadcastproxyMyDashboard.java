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

import fr.paris.lutece.plugins.mydashboard.service.MyDashboardComponent;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.l10n.LocaleService;
import fr.paris.lutece.util.html.HtmlTemplate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class BroadcastproxyMyDashboard extends MyDashboardComponent
{
    // PROPERTIES
    private static final String PROPERTY_MYDASHBOARD_DESCRIPTION = "broadcastproxy.myDashboard.description";

    // MARKERS
    private static final String MARK_LUTECE_USER = "user";
    private static final String MARK_USER_GROUP_LABEL = "group";

    // Templates
    private static final String TEMPLATE_DASHBOARD = "skin/plugins/broadcastproxy/broadcastproxy_mydashboard.html";

    // SESSION DATA
    private LuteceUser _luteceUser;
    private String _plugin = "broadcastproxy";

    @Override
    public String getDashboardData( HttpServletRequest request )
    {

        Map<String, Object> model = new HashMap<>( );

        if ( SecurityService.isAuthenticationEnable( ) )
        {
            _luteceUser = SecurityService.getInstance( ).getRegisteredUser( request );

            if ( _luteceUser != null )
            {
                model.put( MARK_LUTECE_USER, _luteceUser );

            }
        }

        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_DASHBOARD, LocaleService.getUserSelectedLocale( request ), model );

        return t.getHtml( );
    }

    @Override
    public String getComponentId( )
    {
        return "MYDASHBOARD_BROADCASTPROXY";
    }

    @Override
    public String getComponentDescription( Locale locale )
    {
        return I18nService.getLocalizedString( PROPERTY_MYDASHBOARD_DESCRIPTION, locale );
    }

}
