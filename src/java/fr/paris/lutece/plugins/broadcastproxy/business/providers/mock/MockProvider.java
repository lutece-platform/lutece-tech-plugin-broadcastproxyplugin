package fr.paris.lutece.plugins.broadcastproxy.business.providers.mock;

import java.util.HashMap;
import java.util.Map;


import fr.paris.lutece.plugins.broadcastproxy.business.IBroadcastProvider;

public class MockProvider implements IBroadcastProvider
{

    private static final String DEFAULT_USER_SUBSCRIPTIONS_JSON = "[{id:\"1\",name:\"sub1\"},{id:\"2\",name:\"sub2\"}]";
    private static final Map<String, String> DEFAULT_USER_SUBSCRIPTIONS_MAP = createDefaultMap( );

    /**
     * initialize default map
     * 
     * @return the map
     */
    private static Map<String, String> createDefaultMap( )
    {
        Map<String, String> myMap = new HashMap<>( );
        myMap.put( "1", "sub1" );
        myMap.put( "2", "sub2" );
        return myMap;
    }

    @Override
    public String getName( )
    {
        return "Mock";
    }


    @Override
    public String getUserSubscriptions( String userId, String typeSubsciption ) 
    {
        return DEFAULT_USER_SUBSCRIPTIONS_JSON;
    }

    @Override
    public boolean updateUserSubscribtions( String userId, Map<String, String> listSubscriptions, String typeSubsciption ) 
    {
        return true;
    }

    @Override
    public boolean subscribe(String userId, String subscriptionId, String typeSubsciption) 
    {
        return true;
    }

    @Override
    public boolean unsubscribe(String userId, String subscriptionId, String typeSubsciption) 
    {
        return true;
    }

    @Override
    public Map<String, String> getUserSubscriptionsAsMap(String userId, String typeSubsciption)  
    {
        return DEFAULT_USER_SUBSCRIPTIONS_MAP;
    }

}
