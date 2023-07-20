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


package fr.paris.lutece.plugins.broadcastproxy.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class provides Data Access methods for SubscriptionLink objects
 */
public final class SubscriptionLinkDAO implements ISubscriptionLinkDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_subscription_link, label, pictogramme, description, frequency, subscription_group, group_id, subscription_id, interest_id, enabled FROM broadcastproxy_subscription_link WHERE id_subscription_link = ?";
    private static final String SQL_QUERY_SELECT_SUBSCRIPTION_ID = "SELECT id_subscription_link, label, pictogramme, description, frequency, subscription_group, group_id, subscription_id, interest_id, enabled FROM broadcastproxy_subscription_link WHERE subscription_id = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO broadcastproxy_subscription_link ( label, pictogramme, description, frequency, subscription_group, group_id, subscription_id, interest_id, enabled ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM broadcastproxy_subscription_link WHERE id_subscription_link = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE broadcastproxy_subscription_link SET label = ?, pictogramme = ?, description = ?, frequency = ?, subscription_group = ?, group_id = ?, subscription_id = ?, interest_id = ?, enabled = ? WHERE id_subscription_link = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_subscription_link, label, pictogramme, description, frequency, subscription_group, group_id, subscription_id, interest_id, enabled FROM broadcastproxy_subscription_link ORDER BY label asc";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_subscription_link FROM broadcastproxy_subscription_link";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_subscription_link, label, pictogramme, description, frequency, subscription_group, group_id, subscription_id, interest_id, enabled FROM broadcastproxy_subscription_link WHERE id_subscription_link IN (  ";
    private static final String SQL_QUERY_DISABLE_NEWSLETTER = "SELECT id_subscription_link FROM broadcastproxy_subscription_link WHERE enabled = 0";
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( SubscriptionLink subscriptionLink, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++ , subscriptionLink.getLabel( ) );
            daoUtil.setString( nIndex++ , subscriptionLink.getPictogramme( ) );
            daoUtil.setString( nIndex++ , subscriptionLink.getDescription( ) );
            daoUtil.setString( nIndex++ , subscriptionLink.getFrequency( ) );
            daoUtil.setString( nIndex++ , subscriptionLink.getGroup( ) );
            daoUtil.setInt( nIndex++ , subscriptionLink.getGroupId( ) );
            daoUtil.setInt( nIndex++ , subscriptionLink.getSubscriptionId( ) );
            daoUtil.setInt( nIndex++ , subscriptionLink.getInterestId( ) );
            daoUtil.setBoolean( nIndex++, subscriptionLink.isEnabled( ) );
            
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) ) 
            {
                subscriptionLink.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<SubscriptionLink> load( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeQuery( );
	        SubscriptionLink subscriptionLink = null;
	
	        if ( daoUtil.next( ) )
	        {
	            subscriptionLink = new SubscriptionLink();
	            int nIndex = 1;
	            
	            subscriptionLink.setId( daoUtil.getInt( nIndex++ ) );
			    subscriptionLink.setLabel( daoUtil.getString( nIndex++ ) );
			    subscriptionLink.setPictogramme( daoUtil.getString( nIndex++ ) );
			    subscriptionLink.setDescription( daoUtil.getString( nIndex++ ) );
			    subscriptionLink.setFrequency( daoUtil.getString( nIndex++ ) );
			    subscriptionLink.setGroup( daoUtil.getString( nIndex++ ) );
	            subscriptionLink.setGroupId( daoUtil.getInt( nIndex++ ) );
			    subscriptionLink.setSubscriptionId( daoUtil.getInt( nIndex++ ) );
			    subscriptionLink.setInterestId( daoUtil.getInt( nIndex++ ) );
	            subscriptionLink.setEnabled( daoUtil.getBoolean( nIndex ) );
	        }
	
	        return Optional.ofNullable( subscriptionLink );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( SubscriptionLink subscriptionLink, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
	        int nIndex = 1;
	        
        	daoUtil.setString( nIndex++ , subscriptionLink.getLabel( ) );
        	daoUtil.setString( nIndex++ , subscriptionLink.getPictogramme( ) );
        	daoUtil.setString( nIndex++ , subscriptionLink.getDescription( ) );
        	daoUtil.setString( nIndex++ , subscriptionLink.getFrequency( ) );
        	daoUtil.setString( nIndex++ , subscriptionLink.getGroup( ) );
            daoUtil.setInt( nIndex++ , subscriptionLink.getGroupId( ) );
        	daoUtil.setInt( nIndex++ , subscriptionLink.getSubscriptionId( ) );
        	daoUtil.setInt( nIndex++ , subscriptionLink.getInterestId( ) );
            daoUtil.setBoolean( nIndex++, subscriptionLink.isEnabled( ) );
	        daoUtil.setInt( nIndex++ , subscriptionLink.getId( ) );
	
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<SubscriptionLink> selectSubscriptionLinksList( Plugin plugin )
    {
        List<SubscriptionLink> subscriptionLinkList = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            SubscriptionLink subscriptionLink = new SubscriptionLink(  );
	            int nIndex = 1;
	            
	            subscriptionLink.setId( daoUtil.getInt( nIndex++ ) );
			    subscriptionLink.setLabel( daoUtil.getString( nIndex++ ) );
			    subscriptionLink.setPictogramme( daoUtil.getString( nIndex++ ) );
			    subscriptionLink.setDescription( daoUtil.getString( nIndex++ ) );
			    subscriptionLink.setFrequency( daoUtil.getString( nIndex++ ) );
			    subscriptionLink.setGroup( daoUtil.getString( nIndex++ ) );
                subscriptionLink.setGroupId( daoUtil.getInt( nIndex++ ) );
			    subscriptionLink.setSubscriptionId( daoUtil.getInt( nIndex++ ) );
			    subscriptionLink.setInterestId( daoUtil.getInt( nIndex++ ) );
			    subscriptionLink.setEnabled( daoUtil.getBoolean( nIndex ) );
			    
	            subscriptionLinkList.add( subscriptionLink );
	        }
	
	        return subscriptionLinkList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdSubscriptionLinksList( Plugin plugin )
    {
        List<Integer> subscriptionLinkList = new ArrayList<>( );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            subscriptionLinkList.add( daoUtil.getInt( 1 ) );
	        }
	
	        return subscriptionLinkList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectSubscriptionLinksReferenceList( Plugin plugin )
    {
        ReferenceList subscriptionLinkList = new ReferenceList();
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            subscriptionLinkList.addItem( daoUtil.getInt( 1 ) , daoUtil.getString( 2 ) );
	        }
	
	        return subscriptionLinkList;
    	}
    }
    
    /**
     * {@inheritDoc }
     */
	@Override
	public List<SubscriptionLink> selectSubscriptionLinksListByIds( Plugin plugin, List<Integer> listIds ) {
		List<SubscriptionLink> subscriptionLinkList = new ArrayList<>(  );
		
		StringBuilder builder = new StringBuilder( );

		if ( !listIds.isEmpty( ) )
		{
			for( int i = 0 ; i < listIds.size(); i++ ) {
			    builder.append( "?," );
			}
	
			String placeHolders =  builder.deleteCharAt( builder.length( ) -1 ).toString( );
			String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + ")";
			
			
	        try ( DAOUtil daoUtil = new DAOUtil( stmt, plugin ) )
	        {
	        	int index = 1;
				for( Integer n : listIds ) {
					daoUtil.setInt(  index++, n ); 
				}
	        	
	        	daoUtil.executeQuery(  );
	        	while ( daoUtil.next(  ) )
		        {
		        	SubscriptionLink subscriptionLink = new SubscriptionLink(  );
		            int nIndex = 1;
		            
		            subscriptionLink.setId( daoUtil.getInt( nIndex++ ) );
				    subscriptionLink.setLabel( daoUtil.getString( nIndex++ ) );
				    subscriptionLink.setPictogramme( daoUtil.getString( nIndex++ ) );
				    subscriptionLink.setDescription( daoUtil.getString( nIndex++ ) );
				    subscriptionLink.setFrequency( daoUtil.getString( nIndex++ ) );
				    subscriptionLink.setGroup( daoUtil.getString( nIndex++ ) );
                    subscriptionLink.setGroupId( daoUtil.getInt( nIndex++ ) );
				    subscriptionLink.setSubscriptionId( daoUtil.getInt( nIndex++ ) );
				    subscriptionLink.setInterestId( daoUtil.getInt( nIndex++ ) );
				    subscriptionLink.setEnabled( daoUtil.getBoolean( nIndex ) );
				    
		            subscriptionLinkList.add( subscriptionLink );
		        }
		
		        daoUtil.free( );
		        
	        }
	    }
		return subscriptionLinkList;
		
	}

    @Override
    public Optional<SubscriptionLink> loadBySubscriptionId( int nSubscriptionId, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_SUBSCRIPTION_ID, plugin ) )
        {
            daoUtil.setInt( 1 , nSubscriptionId );
            daoUtil.executeQuery( );
            SubscriptionLink subscriptionLink = null;
    
            if ( daoUtil.next( ) )
            {
                subscriptionLink = new SubscriptionLink();
                int nIndex = 1;
                
                subscriptionLink.setId( daoUtil.getInt( nIndex++ ) );
                subscriptionLink.setLabel( daoUtil.getString( nIndex++ ) );
                subscriptionLink.setPictogramme( daoUtil.getString( nIndex++ ) );
                subscriptionLink.setDescription( daoUtil.getString( nIndex++ ) );
                subscriptionLink.setFrequency( daoUtil.getString( nIndex++ ) );
                subscriptionLink.setGroup( daoUtil.getString( nIndex++ ) );
                subscriptionLink.setGroupId( daoUtil.getInt( nIndex++ ) );
                subscriptionLink.setSubscriptionId( daoUtil.getInt( nIndex++ ) );
                subscriptionLink.setInterestId( daoUtil.getInt( nIndex++ ) );
                subscriptionLink.setEnabled( daoUtil.getBoolean( nIndex ) );
                
            }
    
            return Optional.ofNullable( subscriptionLink );
        }
    }

    @Override
    public boolean existDisabledNewsletter( Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DISABLE_NEWSLETTER, plugin ) )
        {
            daoUtil.executeQuery( );    
            return daoUtil.next( );
        }
    }
}
