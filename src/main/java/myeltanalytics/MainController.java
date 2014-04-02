package myeltanalytics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
public class MainController {
  
    public static long LAST_USER_ID = -1;
    
    public static long LAST_ACTIVITY_SUBMISSION_ID = -1;
    
    public static String USERS_INDEX = "users";
    
    public static String USERS_TYPE = "user_info";
    
    public static String SUBMISSIONS_INDEX = "submissions";
    
    public static String SUBMISSIONS_TYPE = "submissions_info";
    
    public static String MYELT_ANALYTICS_INDEX = "myeltanalytics";
    
    public static String MYELT_ANALYTICS_TYPE = "status";
    
    public static final String BLANK = "";
    
    private final Logger LOGGER = Logger.getLogger(MainController.class);
    
    @Autowired
    private Client elasticSearchClient;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private EventBusService eventBusService;
    
    @PostConstruct
    void initializeLastStatusParameters(){
        setLastSyncedUserId();
        setLastSyncedSubmissionId();
        createUsersIndex();
        createSubmissionIndex();
    }
    
    @RequestMapping("/")
    public String getIndex() {
        return "index.html";
    }
    
    @RequestMapping(value= "/startPushingUser")
    @ResponseBody String putUserDataIntoElasticSearch() throws JsonProcessingException{       
        try {
            jdbcTemplate.query(
                "select id from users where type=0 and id > ? order by id", new Object[] { LAST_USER_ID },
                new RowCallbackHandler()
                {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException
                    {
                        try
                        {
                            PushUserEvent event = new PushUserEvent(USERS_INDEX, USERS_TYPE, rs.getLong("id"));
                            eventBusService.postEvent(event);
                        } catch(Exception e){
                            LOGGER.error("Error while processing User row" ,e);
                        }
                        
                    }
                });
            LOGGER.debug("Started User sync process");
            return "Started User sync process";
        } catch (Exception e) {
            LOGGER.error("Error starting User sync process", e);
            return "Error starting User sync process";
        }
        
    }  
    
    
    @RequestMapping(value= "/startPushingSubmissions")
    @ResponseBody String putSubmissionDataIntoElasticSearch() throws JsonProcessingException{
        //To-Do move this logic in eventBus if required 
        try {
            jdbcTemplate.query(
                "select (ar.id) from assignmentresults as ar where ar.id> ? LIMIT 200000",new Object[] { LAST_ACTIVITY_SUBMISSION_ID },
                new RowCallbackHandler()
                {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException
                    {
                        try {
                            long currentId = rs.getLong("ar.id");
                            PushSubmissionEvent event = new PushSubmissionEvent(SUBMISSIONS_INDEX,SUBMISSIONS_TYPE , currentId);
                            eventBusService.postEvent(event);
                        } catch (Exception e) {
                            LOGGER.error("Error while processing Activity Submission row" ,e);
                        }
                        
                        
                    }
                });
            LOGGER.debug("Started Submission sync process");
            return "Started Submission sync process";
        } catch (Exception e) {
            LOGGER.error("Error starting Submission sync process", e);
            return "Error starting Submission sync process";
        }
        
    }
    
    private void setLastSyncedUserId() {
        try {
            GetResponse userIdStatusResponse = elasticSearchClient.prepareGet(MYELT_ANALYTICS_INDEX, MYELT_ANALYTICS_TYPE, "lastUserId").execute().actionGet();
            Map<String,Object> map = userIdStatusResponse.getSourceAsMap();
            Integer userIdStatus = (Integer) map.get("id");
            if(userIdStatus != null){
                LAST_USER_ID = userIdStatus; 
            }
        }
        catch (Exception e){
            //will come when application is started first time
            //ignore if comes once
            LOGGER.error("An error occured while reading last synced UserId from ElasticSearch" ,e);
        }
    }
    
    private void setLastSyncedSubmissionId() {
        try {
            GetResponse lastSubmissionStatusResponse = elasticSearchClient.prepareGet(MYELT_ANALYTICS_INDEX, MYELT_ANALYTICS_TYPE, "lastActivitySubmissionId")
                .execute()
                .actionGet();
            
            Map<String,Object> map = lastSubmissionStatusResponse.getSourceAsMap();
            Integer lastSubmissionStatus = (Integer) map.get("id");
            if(lastSubmissionStatus != null){
                LAST_ACTIVITY_SUBMISSION_ID = lastSubmissionStatus; 
            }
        }
        catch (Exception e){
            //will come when application is started first time
            //ignore if comes once
            LOGGER.error("An error occured while reading last synced ActivitySubmissionId from ElasticSearch" , e);
        }
    }
    
    private void createUsersIndex() {
        if (!isIndexExist(USERS_INDEX)) {
            elasticSearchClient.admin().indices().create(new CreateIndexRequest(USERS_INDEX)
                    .mapping(USERS_TYPE, buildUserTypeMappings())).actionGet();
        }      
    }
    private void createSubmissionIndex() {
        if (!isIndexExist(SUBMISSIONS_INDEX)) {
            elasticSearchClient.admin().indices().create(new CreateIndexRequest(SUBMISSIONS_INDEX)
                    .mapping(SUBMISSIONS_TYPE, buildSumissionTypeMappings())).actionGet();
        }      
    }
    
    private XContentBuilder buildSumissionTypeMappings()
    {
        XContentBuilder builder = null; 
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject()
            .startObject("properties")
                .startObject("book")
                    .startObject("properties")
                    .startObject("discipline")
                        .field("type", "string")                      
                        .field("index", "not_analyzed")
                 .endObject()
               .endObject()
           .endObject();           
        } catch (Exception e) {
            LOGGER.error("An error occured while building mapping for submission_info" , e);
        }
        return builder;
    }

    private boolean isIndexExist(String index) {
        ActionFuture<IndicesExistsResponse> exists = elasticSearchClient.admin().indices().exists(new IndicesExistsRequest(index));
        IndicesExistsResponse actionGet = exists.actionGet();
        return actionGet.isExists();
    }
    
    private XContentBuilder buildUserTypeMappings(){
        XContentBuilder builder = null; 
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject()
            .startObject("properties")
                .startObject("disciplines")
                    .field("type", "string")                      
                    .field("index", "not_analyzed")
                 .endObject()
               .endObject()
           .endObject();           
        } catch (Exception e) {
            LOGGER.error("An error occured while building mapping for user_info" , e);
        }
        return builder;
    }
    
    
}
