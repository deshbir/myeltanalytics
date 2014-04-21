package myeltanalytics.service;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;

public class Helper
{
    public static String USERS_INDEX = "users";
    
    public static String USERS_TYPE = "user_info";
    
    public static String SUBMISSIONS_INDEX = "submissions";
    
    public static String SUBMISSIONS_TYPE = "submissions_info";
    
    public static String MYELT_ANALYTICS_INDEX = "myeltanalytics";
    
    public static String MYELT_ANALYTICS_TYPE = "status";
    
    public static final String BLANK = "";
    
    public static boolean isIndexExist(String index, Client elasticSearchClient) {
        ActionFuture<IndicesExistsResponse> exists = elasticSearchClient.admin().indices().exists(new IndicesExistsRequest(index));
        IndicesExistsResponse actionGet = exists.actionGet();
        return actionGet.isExists();
    }

    public static String lookupCountry(String country)
    {
        // TODO read from properties
        return "US";
    }
}
