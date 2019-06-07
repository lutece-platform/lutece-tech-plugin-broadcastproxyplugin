package fr.paris.lutece.plugins.broadcastproxy.business;

import java.util.Map;

public interface IBroadcastProvider
{

    /**
     * get provider name
     * 
     * @return
     */
    String getName( );

    /**
     * subscribe
     * 
     * @param userId
     * @param subscriptionId
     * @param typeSubsciption
     * @return true if success
     * @throws java.lang.Exception
     */
    boolean subscribe( String userId, String subscriptionId, String typeSubsciption ) throws Exception;

    /**
     * unsubscribe
     * 
     * @param userId
     * @param subscriptionId
     * @param typeSubsciption
     * @return true if success
     * @throws java.lang.Exception
     */
    boolean unsubscribe( String userId, String subscriptionId, String typeSubsciption ) throws Exception;

    /**
     * returns the user subscribtions list as a JSON string like : [{id:"1",name:"sub1"},{id:"2",name:"sub2"}]
     * 
     * @param userId
     * @param typeSubsciption
     * @return a JSON string
     * @throws java.lang.Exception
     */
    String getUserSubscriptions( String userId, String typeSubsciption ) throws Exception;

    /**
     * returns the user subscribtions list as a map of pairs (id,name)
     * 
     * @param userId
     * @param typeSubsciption
     * @return the map
     * @throws java.lang.Exception
     */
    Map<String, String> getUserSubscriptionsAsMap( String userId, String typeSubsciption ) throws Exception;

    /**
     * update user subscriptions to the specified subscription list
     * 
     * @param userId
     * @param listSubscriptions
     * @param typeSubscription
     * @return true if success
     * @throws java.lang.Exception
     */
    boolean updateUserSubscribtions( String userId, Map<String, String> listSubscriptions, String typeSubscription ) throws Exception;

}
