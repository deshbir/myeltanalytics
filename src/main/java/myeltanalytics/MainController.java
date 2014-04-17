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
    
    /**
     * Setting the string values as final String variable to reuse them
     */
    public static final String LAST_JOB_ID = "lastJobId";
    public static final String LAST_USER_ID_STR = "lastUserId";
    public static final String ID = "id";
    public static final String LAST_SUBMISSION_ID = "lastSubmissionId";
    public static final String SUCCESSFULL_RECORDS = "successfullRecords";
    public static final String FAILED_RECORDS = "failedRecords";
    public static final String TOTAL_RECORDS = "totalRecords";
    
    
    
    
    public static long LAST_USER_ID = -1;
    
    public static long LAST_ACTIVITY_SUBMISSION_ID = -1;
    
    public static String USERS_INDEX = "users";
    
    public static String USERS_TYPE = "user_info";
    
    public static String SUBMISSIONS_INDEX = "submissions";
    
    public static String SUBMISSIONS_TYPE = "submissions_info";
    
    public static String MYELT_ANALYTICS_INDEX = "myeltsync";
    
    public static String MYELT_USER_STATUS_TYPE = "usersyncjob";
    
    public static String MYELT_SUBMISSIONS_STATUS_TYPE = "status";
    
    public static int LAST_USER_JOB_ID = 0;
    
    public static Integer LAST_SUBMISSION_JOB_ID;
    
    public static final String BLANK = "";
    
    private final Logger LOGGER = Logger.getLogger(MainController.class);
    
    @Autowired
    private Client elasticSearchClient;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private EventBusService eventBusService;
    
    @Autowired
    private PushDataListener pushDataListener;

    
    
    @PostConstruct
    void initializeLastStatusParameters(){
        setLastSyncedUserJob();
        setLastSyncedSubmissionJob();
        createUsersIndex();
        createSubmissionIndex();
    }
    
    @RequestMapping("/")
    public String getIndex() {
        return "index.html";
    }
    
    
    
    @RequestMapping(value= "/startPushingUser")
    @ResponseBody String putUserDataIntoElasticSearch() throws JsonProcessingException{
        setTotalUserCount();
        if(isLastUserJobCompleted()){
            ++LAST_USER_JOB_ID;
            LAST_USER_ID = -1;
            String json = "{\"id\": " + LAST_USER_JOB_ID + "}";
            elasticSearchClient.prepareIndex(MainController.MYELT_ANALYTICS_INDEX, MainController.MYELT_USER_STATUS_TYPE, LAST_JOB_ID).setSource(json).execute().actionGet();
            pushDataListener.totalUserSucessFullRecords = 0;
            pushDataListener.totalUserFailedRecords = 0;
            pushDataListener.setLastUserStatus(LAST_USER_ID, LAST_USER_JOB_ID,0);
        }
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
                            PushUserEvent event = new PushUserEvent(USERS_INDEX, USERS_TYPE, rs.getLong("id"),LAST_USER_JOB_ID);
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
        setTotalSubmissionsCount();
        if(isLastSubmissionJobCompleted()){
            ++LAST_SUBMISSION_JOB_ID;
            LAST_ACTIVITY_SUBMISSION_ID = -1;
            String json = "{\"id\": " + LAST_SUBMISSION_JOB_ID + "}";
            elasticSearchClient.prepareIndex(MainController.MYELT_ANALYTICS_INDEX, MainController.MYELT_SUBMISSIONS_STATUS_TYPE, LAST_JOB_ID).setSource(json).execute().actionGet();
            pushDataListener.totalSubmissionSucessFullRecords = 0;
            pushDataListener.totalSubmissionFailedRecords = 0;
            pushDataListener.setLastActivitySubmissionStatus(LAST_ACTIVITY_SUBMISSION_ID, LAST_SUBMISSION_JOB_ID,0);
        }
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
                            PushSubmissionEvent event = new PushSubmissionEvent(SUBMISSIONS_INDEX,SUBMISSIONS_TYPE , currentId,LAST_SUBMISSION_JOB_ID);
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
    
    @RequestMapping(value= "/stopPushingUsers")
    @ResponseBody void stopPushingUsersIntoElasticSearch(){
        eventBusService.unRegisterSubscriber(pushDataListener);
    }
    
    @RequestMapping(value= "/getPushedUsersStatus")
    @ResponseBody String getPushedUsersIntoElasticSearchStatus(){
        return "User pushed successfully " + pushDataListener.totalUserSucessFullRecords + " \n total failed records " + pushDataListener.totalUserFailedRecords +
            " \n total records " + pushDataListener.totalUserRecords + " last User Job ID "  + LAST_USER_JOB_ID;
    }
    
    
    
    private boolean isLastUserJobCompleted()
    {
        if(LAST_USER_JOB_ID != 0){
            if((pushDataListener.totalUserFailedRecords + pushDataListener.totalUserSucessFullRecords) == pushDataListener.totalUserRecords){
                return true;
            }
            return false;
        }
        return true;
    }
    
    
    
    private boolean isLastSubmissionJobCompleted()
    {
        if(LAST_SUBMISSION_JOB_ID != null){
            if((pushDataListener.totalSubmissionFailedRecords + pushDataListener.totalSubmissionSucessFullRecords) == pushDataListener.totalSubmissionRecords){
                return true;
            }
            return false;
        }
        return true;
    }
    
    
    
    
    private void setLastSyncedUserJob() {
        try {
            GetResponse lastJobIdResponse = elasticSearchClient.prepareGet(MYELT_ANALYTICS_INDEX, MYELT_USER_STATUS_TYPE, LAST_JOB_ID).execute().actionGet();
            Map<String,Object> map = lastJobIdResponse.getSourceAsMap();
            LAST_USER_JOB_ID = (Integer) map.get(ID);
            if(LAST_USER_JOB_ID != 0){
                GetResponse lastJobResponse = elasticSearchClient.prepareGet(MYELT_ANALYTICS_INDEX, MYELT_USER_STATUS_TYPE, String.valueOf(LAST_USER_JOB_ID)).execute().actionGet();
                map  = lastJobResponse.getSourceAsMap();
                LAST_USER_ID = (Integer) map.get(LAST_USER_ID_STR);
                pushDataListener.totalUserSucessFullRecords = (Integer) map.get(SUCCESSFULL_RECORDS);
                pushDataListener.totalUserFailedRecords = (Integer) map.get(FAILED_RECORDS);
                pushDataListener.totalUserRecords = (Integer) map.get(TOTAL_RECORDS);
            }
        }
        catch (Exception e){
            //will come when application is started first time
            //ignore if comes once
            LOGGER.error("An error occured while reading last synced UserId from ElasticSearch" ,e);
        }
    }
    
    private void setLastSyncedSubmissionJob() {
        try {
            GetResponse lastJobIdResponse = elasticSearchClient.prepareGet(MYELT_ANALYTICS_INDEX, MYELT_SUBMISSIONS_STATUS_TYPE, LAST_JOB_ID).execute().actionGet();
            Map<String,Object> map = lastJobIdResponse.getSourceAsMap();
            LAST_SUBMISSION_JOB_ID = (Integer) map.get(ID);
            if(LAST_SUBMISSION_JOB_ID != null){
                GetResponse lastJobResponse = elasticSearchClient.prepareGet(MYELT_ANALYTICS_INDEX, MYELT_SUBMISSIONS_STATUS_TYPE, LAST_SUBMISSION_JOB_ID.toString()).execute().actionGet();
                map  = lastJobResponse.getSourceAsMap();
                LAST_ACTIVITY_SUBMISSION_ID = (Integer) map.get(LAST_SUBMISSION_ID);
                pushDataListener.totalSubmissionSucessFullRecords = (Integer) map.get(SUCCESSFULL_RECORDS);
                pushDataListener.totalSubmissionFailedRecords = (Integer) map.get(FAILED_RECORDS); 
                pushDataListener.totalSubmissionRecords = (Integer) map.get(TOTAL_RECORDS);
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
                   .endObject()
                   .startObject("institution")
                     .startObject("properties")
                         .startObject("name")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()
                         .endObject()
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
                 .startObject("institution")
                     .startObject("properties")
                         .startObject("name")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()  
                         .startObject("country")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()  
                     .endObject()      
                 .endObject()
           .endObject()
           .endObject();           
        } catch (Exception e) {
            LOGGER.error("An error occured while building mapping for user_info" , e);
        }
        return builder;
    }
    
    
    
    private void setTotalUserCount(){
        String sql = "SELECT COUNT(*) FROM users";
        pushDataListener.totalUserRecords = jdbcTemplate.queryForObject(sql, Long.class);
    }
    
    private void setTotalSubmissionsCount(){
        String sql = "SELECT COUNT(*) FROM users";
        pushDataListener.totalSubmissionRecords = jdbcTemplate.queryForObject(sql, Long.class);
    }
    
}
