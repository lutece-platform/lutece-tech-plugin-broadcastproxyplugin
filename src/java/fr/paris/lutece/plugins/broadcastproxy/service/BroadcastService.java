package fr.paris.lutece.plugins.broadcastproxy.service;

import java.util.Map;


import fr.paris.lutece.plugins.broadcastproxy.business.IBroadcastProvider;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

public class BroadcastService
{

    private static final String BEAN_BROADCAST_PROVIDER = "broadcastproxy.provider";

    private static IBroadcastProvider _broadcastProvider;
    private static BroadcastService _instance;

    /**
     * Private constructor
     */
    private BroadcastService( )
    {
    }

    /**
     * get provider name
     * 
     * @return the name of the provider
     */
    public String getName( )
    {
        return _broadcastProvider.getName( );
    }
    
    /**
     * Get the unique instance of the Security Service
     *
     * @return The instance
     */
    public static synchronized BroadcastService getInstance( ) 
    {
        if ( _instance == null )
        {
            _instance = new BroadcastService( );
            _instance.init( );
        }

        return _instance;
    }

    /**
     * Initialize service
     *
     * @throws LuteceInitException
     *             If error while initialization
     */
    private synchronized void init( ) 
    {
        if ( _broadcastProvider == null )
        {
            
            _broadcastProvider = (IBroadcastProvider) SpringContextService.getBean( BEAN_BROADCAST_PROVIDER );
            AppLogService.info( "BroadcastProvider loaded : " + _broadcastProvider.getName( ) );
                        
        }
    }


    /**
     * get user subscriptions
     *   returns the user subscription list as a JSON string like :
     *       [{id:"1",name:"sub1"},{id:"2",name:"sub2"}]
     * 
     * @param userId
     * @param typeSubsciption
     * @return a JSON String
     */
    public String getUserSubscriptions( String userId, String typeSubsciption ) throws Exception
    {
        return _broadcastProvider.getUserSubscriptions( userId, typeSubsciption );
    }

    /**
     * update user subscriptions to the specified subscription list
     * 
     * @param userId
     * @param listSubscriptions
     * @param typeSubsciption
     * @return true if success
     * @throws Exception 
     */
    public boolean updateUserSubscribtions( String userId, Map<String, String> listSubscriptions, String typeSubsciption ) throws Exception
    {
        return _broadcastProvider.updateUserSubscribtions( userId, listSubscriptions, typeSubsciption );
    }

    /**
     * Subscribe
     * 
     * @param userId
     * @param subscriptionId
     * @param typeSubsciption
     * @return true if success
     * @throws Exception 
     */
    public boolean subscribe(String userId, String subscriptionId, String typeSubsciption) throws Exception 
    {
        return _broadcastProvider.subscribe( userId, subscriptionId, typeSubsciption );
    }

    /**
     * unsubscribe 
     * 
     * @param userId
     * @param subscriptionId
     * @param typeSubsciption
     * @return true if success
     * @throws Exception 
     */
    public boolean unsubscribe(String userId, String subscriptionId, String typeSubsciption) throws Exception 
    {
        return _broadcastProvider.unsubscribe( userId, subscriptionId, typeSubsciption );
    }

    /**
     * returns the user subscribtions list as a map of pairs (id,name)
     * 
     * @param userId
     * @param typeSubsciption
     * @return the map
     * @throws java.lang.Exception
     */
    public Map<String, String> getUserSubscriptionsAsMap(String userId, String typeSubsciption) throws Exception 
    {
        return _broadcastProvider.getUserSubscriptionsAsMap( userId, typeSubsciption );
    }
    
    
 



}
