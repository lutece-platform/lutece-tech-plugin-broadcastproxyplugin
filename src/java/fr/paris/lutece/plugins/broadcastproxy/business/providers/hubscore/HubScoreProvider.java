package fr.paris.lutece.plugins.broadcastproxy.business.providers.hubscore;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashMap;
import java.util.Map;


import fr.paris.lutece.plugins.broadcastproxy.business.IBroadcastProvider;
import fr.paris.lutece.plugins.broadcastproxy.service.Constants;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

public class HubScoreProvider implements IBroadcastProvider
{

    // Constants
    private static final String PROVIDER_NAME = "HubScore" ;
    private static final String CONSTANT_KEY_RECORDS = "records";
    private static final String CONSTANT_KEY_SUBSCRIPTION_ID = "id";
    private static final String CONSTANT_KEY_SUBSCRIPTION_NAME = "name";
    
    // instance variables
    private HubScoreAPI _hubScoreAPI ;

    /**
     * Constructor
     */
    public HubScoreProvider() {
        _hubScoreAPI = new HubScoreAPI();
    }

    
    @Override
    public String getName( )
    {
        return PROVIDER_NAME;
    }

    @Override
    public boolean subscribe(String userId, String subscriptionId, String typeSubsciption) throws Exception {
        
        String result =  _hubScoreAPI.manageUser(userId, Constants.TYPE_NEWSLETTER, Constants.ACTION_ADD );
        
        return true;
    }

    @Override
    public boolean unsubscribe(String userId, String subscriptionId, String typeSubsciption) throws Exception {
        
        String result = _hubScoreAPI.manageUser(userId, Constants.TYPE_NEWSLETTER, Constants.ACTION_DELETE );
        
        return true;
    }

    @Override
    public String getUserSubscriptions(String userId, String typeSubsciption) throws Exception {
        
        String userSubscriptionsList = _hubScoreAPI.getUserSubscriptions( userId, typeSubsciption );
        
        return buildJson( userSubscriptionsList );
    }

    @Override
    public Map<String, String> getUserSubscriptionsAsMap(String userId, String typeSubsciption) throws Exception {
        String userSubscriptionsList = _hubScoreAPI.getUserSubscriptions( userId, typeSubsciption );
        
        return buildMap( userSubscriptionsList );
    }

    @Override
    public boolean updateUserSubscribtions(String userId, Map<String, String> listSubscriptions, String typeSubscription) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Parse JSON response and build generic JSON response as 
     *       [{id:"1",name:"sub1"},{id:"2",name:"sub2"}]
     * @param jsonResponse
     * @return a JSON String
     */
    private String buildJson( String jsonResponse ) throws IOException
    {
        
        ObjectMapper mapper = new ObjectMapper( );
        ObjectNode jsonResult = mapper.createObjectNode( );
        ArrayNode jsonSubscriptions = mapper.createArrayNode( );
        
        JsonNode jsonNode =  mapper.readTree( jsonResponse );
        ArrayNode arrayRecordsNode =  (ArrayNode)jsonNode.get( CONSTANT_KEY_RECORDS );
        
        if ( arrayRecordsNode != null && arrayRecordsNode.size( ) > 0 ) 
        {
            Iterator<Map.Entry<String,JsonNode>> fieldsIterator = arrayRecordsNode.get(0).fields( );
            while ( fieldsIterator.hasNext( ) )
            {
                Map.Entry<String,JsonNode> field = fieldsIterator.next( );

                String key = field.getKey( );
                
                // subscription name key must start with "Optin_"
                if ( key.startsWith("Optin_") ) 
                {
                    String subName = key.substring(6);
                    String subState = field.getValue( ).asText( ) ;
                    
                    ObjectNode node = mapper.createObjectNode();
                    node.put( "name", subName );
                    node.put( "active", subState );
                    
                    if  ( arrayRecordsNode.get(0).get( subName ) != null )
                    {
                        String data  = arrayRecordsNode.get(0).get( subName ).asText( );
                        String[] themes = data.split(",");
                        
                        ArrayNode themeArray = mapper.createArrayNode();
                        for (String theme : themes )
                        {
                            themeArray.add( theme );
                        }
                        
                        node.putPOJO("data", themeArray);                        
                    }
                    
                    jsonSubscriptions.add( node );
                }
            }
        }
        
        jsonResult.putPOJO( "subscriptions", jsonSubscriptions );
                    
        return jsonResult.toString( );
    }
    
    
    /**
     *  Parse JSON response like :
     * 
     * {"count":"1","records":[{"id":"123456","Email":"XXX.YYY@ZZZ.com","Optin_Paris":"1","Date_Consentement_Paris":"2019-06-07 14:34:45","Optin_QFAP":"1","Date_Consentement_QFAP":"2019-06-07 14:34:45","Optin_Alerte":"1","Alerte":"parcs_et_jardins,Paris_sport_vacances","Date_Consentement_Alerte":"2019-06-07 14:34:45","hubBlacklist":false}]}
     
     * and build generic MAP response as pairs of (id,name)
     * 
     * @param jsonResponse
     * @return a JSON String
     */
    private Map<String,String> buildMap( String jsonResponse ) throws IOException
    {
        Map<String,String> map = new HashMap<>( );
        ObjectMapper mapper = new ObjectMapper( );
        JsonNode jsonNode = null;

        jsonNode = mapper.readTree( jsonResponse );

        ArrayNode arrayNode =  (ArrayNode)jsonNode.get( CONSTANT_KEY_RECORDS );
        
        if ( arrayNode != null && arrayNode.size( ) > 0 ) 
        {
            Iterator<Map.Entry<String,JsonNode>> fieldsIterator = arrayNode.get(0).fields( );
            while ( fieldsIterator.hasNext( ) )
            {
                Map.Entry<String,JsonNode> field = fieldsIterator.next( );

                String key = field.getKey( );
                
                // subscription name key must start with "Optin_"
                if ( key.startsWith("Optin_") ) 
                {
                    String subName = key;
                    String subState = field.getValue( ).asText( ) ;
                    map.put(subName, subState);
                }
                
                // Patricular case : "Alerte" subscription has datas 
                if ( key.equals("Alerte") ) 
                {
                    String subName = key;
                    String subState = field.getValue( ).asText( ) ;
                    map.put(subName, subState);
                }

            }
        }
        
        return map;
    }
    
}
