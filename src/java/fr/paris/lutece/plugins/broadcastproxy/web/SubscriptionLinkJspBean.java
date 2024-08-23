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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.broadcastproxy.business.SubscriptionLink;
import fr.paris.lutece.plugins.broadcastproxy.business.SubscriptionLinkHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.FileServiceException;
import fr.paris.lutece.portal.service.fileimage.FileImagePublicService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage SubscriptionLink features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageSubscriptionLinks.jsp", controllerPath = "jsp/admin/plugins/broadcastproxy/", right = "BROADCASTPROXY_MANAGEMENT_NEWSLETTERS" )
public class SubscriptionLinkJspBean extends AbstractManageSubscriptionJspBean <Integer, SubscriptionLink>
{
    // Templates
    private static final String TEMPLATE_MANAGE_SUBSCRIPTIONLINKS = "/admin/plugins/broadcastproxy/manage_subscriptionlinks.html";
    private static final String TEMPLATE_CREATE_SUBSCRIPTIONLINK = "/admin/plugins/broadcastproxy/create_subscriptionlink.html";
    private static final String TEMPLATE_MODIFY_SUBSCRIPTIONLINK = "/admin/plugins/broadcastproxy/modify_subscriptionlink.html";

    // Parameters
    private static final String PARAMETER_ID_SUBSCRIPTIONLINK = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_SUBSCRIPTIONLINKS = "broadcastproxy.manage_subscriptionlinks.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_SUBSCRIPTIONLINK = "broadcastproxy.modify_subscriptionlink.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_SUBSCRIPTIONLINK = "broadcastproxy.create_subscriptionlink.pageTitle";

    // Markers
    private static final String MARK_SUBSCRIPTIONLINK_LIST = "subscriptionlink_list";
    private static final String MARK_SUBSCRIPTIONLINK = "subscriptionlink";
    private static final String MARK_PICTOGRAMME = "pictogramme";
    
    private static final String JSP_MANAGE_SUBSCRIPTIONLINKS = "jsp/admin/plugins/broadcastproxy/ManageSubscriptionLinks.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_SUBSCRIPTIONLINK = "broadcastproxy.message.confirmRemoveSubscriptionLink";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "broadcastproxy.model.entity.subscriptionlink.attribute.";

    // Views
    private static final String VIEW_MANAGE_SUBSCRIPTIONLINKS = "manageSubscriptionLinks";
    private static final String VIEW_CREATE_SUBSCRIPTIONLINK = "createSubscriptionLink";
    private static final String VIEW_MODIFY_SUBSCRIPTIONLINK = "modifySubscriptionLink";

    // Actions
    private static final String ACTION_CREATE_SUBSCRIPTIONLINK = "createSubscriptionLink";
    private static final String ACTION_MODIFY_SUBSCRIPTIONLINK = "modifySubscriptionLink";
    private static final String ACTION_REMOVE_SUBSCRIPTIONLINK = "removeSubscriptionLink";
    private static final String ACTION_CONFIRM_REMOVE_SUBSCRIPTIONLINK = "confirmRemoveSubscriptionLink";
    private static final String ACTION_DELETE_PICTOGRAMME = "deletePictogramme";
  

    // Infos
    private static final String INFO_SUBSCRIPTIONLINK_CREATED = "broadcastproxy.info.subscriptionlink.created";
    private static final String INFO_SUBSCRIPTIONLINK_UPDATED = "broadcastproxy.info.subscriptionlink.updated";
    private static final String INFO_SUBSCRIPTIONLINK_REMOVED = "broadcastproxy.info.subscriptionlink.removed";
    
    // Warnings
    private static final String WARNING_SUBSCRIPTIONLINK_ENABLED = "broadcastproxy.warning.subscriptionlink.enabled";
    
    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    
    // Session variable to store working values
    private SubscriptionLink _subscriptionlink;
    private List<Integer> _listIdSubscriptionLinks;
    
    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_SUBSCRIPTIONLINKS, defaultView = true )
    public String getManageSubscriptionLinks( HttpServletRequest request )
    {
        _subscriptionlink = null;
        
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX) == null || _listIdSubscriptionLinks.isEmpty( ) )
        {
        	_listIdSubscriptionLinks = SubscriptionLinkHome.getIdSubscriptionLinksList(  );
        }
        
        Map<String, Object> model = getPaginatedListModel( request, MARK_SUBSCRIPTIONLINK_LIST, _listIdSubscriptionLinks, JSP_MANAGE_SUBSCRIPTIONLINKS );
        
        if ( SubscriptionLinkHome.existDisabledNewsletter( ) )  
        {
            addWarning( WARNING_SUBSCRIPTIONLINK_ENABLED, getLocale( ) );
        }
        return getPage( PROPERTY_PAGE_TITLE_MANAGE_SUBSCRIPTIONLINKS, TEMPLATE_MANAGE_SUBSCRIPTIONLINKS, model );
    }

	/**
     * Get Items from Ids list
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
	@Override
	List<SubscriptionLink> getItemsFromIds( List<Integer> listIds ) 
	{
		List<SubscriptionLink> listSubscriptionLink = SubscriptionLinkHome.getSubscriptionLinksListByIds( listIds );
		
		// keep original order
        return listSubscriptionLink.stream()
                 .sorted(Comparator.comparingInt( notif -> listIds.indexOf( notif.getId())))
                 .collect(Collectors.toList());
	}
    
    /**
    * reset the _listIdSubscriptionLinks list
    */
    public void resetListId( )
    {
    	_listIdSubscriptionLinks = new ArrayList<>( );
    }

    /**
     * Returns the form to create a subscriptionlink
     *
     * @param request The Http request
     * @return the html code of the subscriptionlink form
     */
    @View( VIEW_CREATE_SUBSCRIPTIONLINK )
    public String getCreateSubscriptionLink( HttpServletRequest request )
    {
        _subscriptionlink = ( _subscriptionlink != null ) ? _subscriptionlink : new SubscriptionLink(  );

        Map<String, Object> model = getModel(  );
        model.put( MARK_SUBSCRIPTIONLINK, _subscriptionlink );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_SUBSCRIPTIONLINK ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_SUBSCRIPTIONLINK, TEMPLATE_CREATE_SUBSCRIPTIONLINK, model );
    }

    /**
     * Process the data capture form of a new subscriptionlink
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_SUBSCRIPTIONLINK )
    public String doCreateSubscriptionLink( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _subscriptionlink, request, getLocale( ) );
        
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_SUBSCRIPTIONLINK ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }
        // Check constraints
        if ( !validateBean( _subscriptionlink, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_SUBSCRIPTIONLINK );
        }
        
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        FileItem fileParameterBinaryValue = multipartRequest.getFile( "pictogramme" );

        FileImagePublicService.init( );
        if( fileParameterBinaryValue.getSize( ) > 0 )
        {
            _subscriptionlink.setPictogramme( FileImagePublicService.getInstance( ).addImageResource( fileParameterBinaryValue ));
        }
        
        SubscriptionLinkHome.create( _subscriptionlink );
        addInfo( INFO_SUBSCRIPTIONLINK_CREATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_SUBSCRIPTIONLINKS );
    }

    /**
     * Manages the removal form of a subscriptionlink whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_SUBSCRIPTIONLINK )
    public String getConfirmRemoveSubscriptionLink( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_SUBSCRIPTIONLINK ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_SUBSCRIPTIONLINK ) );
        url.addParameter( PARAMETER_ID_SUBSCRIPTIONLINK, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_SUBSCRIPTIONLINK, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a subscriptionlink
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage subscriptionlinks
     */
    @Action( ACTION_REMOVE_SUBSCRIPTIONLINK )
    public String doRemoveSubscriptionLink( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_SUBSCRIPTIONLINK ) );
        
        if ( _subscriptionlink == null || ( _subscriptionlink.getId(  ) != nId ) )
        {
            Optional<SubscriptionLink> optSubscriptionLink = SubscriptionLinkHome.findByPrimaryKey( nId );
            _subscriptionlink = optSubscriptionLink.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }
        
        if ( StringUtils.isNumeric( _subscriptionlink.getPictogramme( ) ) )
        {
            try {
				FileService.getInstance( ).getFileStoreServiceProvider( ).delete( _subscriptionlink.getPictogramme( ) );
			} catch (FileServiceException e) {
				AppLogService.error(e);
			}
        }
        
        SubscriptionLinkHome.remove( nId );
        addInfo( INFO_SUBSCRIPTIONLINK_REMOVED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_SUBSCRIPTIONLINKS );
    }

    /**
     * Returns the form to update info about a subscriptionlink
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_SUBSCRIPTIONLINK )
    public String getModifySubscriptionLink( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_SUBSCRIPTIONLINK ) );

        if ( _subscriptionlink == null || ( _subscriptionlink.getId(  ) != nId ) )
        {
            Optional<SubscriptionLink> optSubscriptionLink = SubscriptionLinkHome.findByPrimaryKey( nId );
            _subscriptionlink = optSubscriptionLink.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }

        FileImagePublicService.init( );
        Map<String, Object> model = getModel(  );
        model.put( MARK_SUBSCRIPTIONLINK, _subscriptionlink );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_SUBSCRIPTIONLINK ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_SUBSCRIPTIONLINK, TEMPLATE_MODIFY_SUBSCRIPTIONLINK, model );
    }

    /**
     * Process the change form of a subscriptionlink
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_SUBSCRIPTIONLINK )
    public String doModifySubscriptionLink( HttpServletRequest request ) throws AccessDeniedException
    {   
        populate( _subscriptionlink, request, getLocale( ) );
		
		
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_SUBSCRIPTIONLINK ) )
        {
            throw new AccessDeniedException ( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _subscriptionlink, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_SUBSCRIPTIONLINK, PARAMETER_ID_SUBSCRIPTIONLINK, _subscriptionlink.getId( ) );
        }
        
        FileImagePublicService.init( );
        if( StringUtils.isEmpty( _subscriptionlink.getPictogramme( ) ) 
                || FileImagePublicService.getInstance( ).getImageResource( Integer.parseInt( _subscriptionlink.getPictogramme( ) ) ) == null  )
        {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;   
            FileItem fileParameterBinaryValue = multipartRequest.getFile( "pictogramme" );
            
            if( fileParameterBinaryValue.getSize( ) > 0 )
            {
                _subscriptionlink.setPictogramme( FileImagePublicService.getInstance( ).addImageResource( fileParameterBinaryValue ));
            }
        }

        SubscriptionLinkHome.update( _subscriptionlink );
        addInfo( INFO_SUBSCRIPTIONLINK_UPDATED, getLocale(  ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_SUBSCRIPTIONLINKS );
    }
    
    @Action( ACTION_DELETE_PICTOGRAMME )
    public String doDeletePictogramme( HttpServletRequest request )
    {
        String strIdPictogramme = request.getParameter( MARK_PICTOGRAMME );
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_SUBSCRIPTIONLINK ) );

        if ( _subscriptionlink == null || ( _subscriptionlink.getId(  ) != nId ) )
        {
            Optional<SubscriptionLink> optSubscriptionLink = SubscriptionLinkHome.findByPrimaryKey( nId );
            _subscriptionlink = optSubscriptionLink.orElseThrow( ( ) -> new AppException(ERROR_RESOURCE_NOT_FOUND ) );
        }

        if ( StringUtils.isNumeric( strIdPictogramme ) )
        {
            try {
				FileService.getInstance( ).getFileStoreServiceProvider( ).delete( strIdPictogramme );
				_subscriptionlink.setPictogramme( StringUtils.EMPTY );
	            SubscriptionLinkHome.update( _subscriptionlink );
			} catch (FileServiceException e) {
				AppLogService.error(e);
				return "{\"status\":\"failed\"}";
			}            
        }

        return "{\"status\":\"success\"}";
    }
    
}
