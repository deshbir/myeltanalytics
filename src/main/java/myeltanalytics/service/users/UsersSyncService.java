package myeltanalytics.service.users;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import myeltanalytics.model.JobInfo;
import myeltanalytics.service.Helper;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service(value="usersSyncService")
public class UsersSyncService
{
    @Autowired
    private Client elasticSearchClient;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Value("${users.threadpoolsize}")
    private int userSyncThreadPoolSize;
    
    public static final String USERS_INDEX = "users";
    
    public static final String USERS_TYPE = "users_info";
    
    public static final String USERS_ONLY_ALIAS = "users_only";
    
    public static final String ACCESS_CODES_ONLY_ALIAS = "accesscodes_only";
    
    public static final String USER_WITH_ACCESSCODE = "user_with_accesscode";
    
    public static final String USER_WITHOUT_ACCESSCODE = "user_without_accesscode";
    
    public static final String ADDITIONAL_ACCESSCODE = "additional_accesscode";
    
    private final Logger LOGGER = Logger.getLogger(UsersSyncService.class);
    
    private ExecutorService userSyncExecutor = null;
    
    public static JobInfo jobInfo = new JobInfo();
    
    public void startFreshSync() throws JsonProcessingException {
        
        String newJobId = UUID.randomUUID().toString();  
        LOGGER.info("Starting a fresh UsersSyncJob with syncJobId=" + newJobId);
        
        updateLastJobInfoInES(newJobId);
        
        jobInfo.setJobId(newJobId);
        jobInfo.setLastId("");
        jobInfo.setSuccessRecords(0);
        jobInfo.setErrorRecords(0);
        jobInfo.setTotalRecords(getTotalUsersCount());
        jobInfo.setJobStatus(Helper.STATUS_INPROGRESS);        
        updateLastSyncedUserStatus();
        
        startSyncJob();
    }
    
    public void stopSync() throws InterruptedException, JsonProcessingException {
        LOGGER.info("Aborting UsersSyncJob with syncJobId=" + jobInfo.getJobId());
        jobInfo.setJobStatus(Helper.STATUS_PAUSED);
        updateLastSyncedUserStatus();
        
        userSyncExecutor.shutdown();
        userSyncExecutor.awaitTermination(1, TimeUnit.MINUTES);    
    }
    
    public void resumeSync() throws JsonProcessingException {
        LOGGER.info("Resuming old UsersSyncJob with syncJobId=" + jobInfo.getJobId());
        jobInfo.setJobStatus(Helper.STATUS_INPROGRESS);
        updateLastSyncedUserStatus();
       
        startSyncJob();
    }
    
    private void startSyncJob() {
        
        userSyncExecutor = Executors.newFixedThreadPool(userSyncThreadPoolSize);
        
        List<Map<String,Object>> usersList = jdbcTemplate.queryForList("select id from users where type=0 and InstitutionID NOT IN " + Helper.IGNORE_INSTITUTIONS);
        
        Iterator<Map<String,Object>> usersListIter = usersList.iterator();
        
        while(usersListIter.hasNext()) {
            Map<String,Object> userMap = usersListIter.next();
            String userId = String.valueOf(userMap.get("id"));
            Runnable worker = new UsersSyncThread(userId);
            userSyncExecutor.execute(worker);            
        }
    }
    
    public synchronized void updateLastSyncedUserStatus() throws JsonProcessingException{
        if (jobInfo.getErrorRecords() + jobInfo.getSuccessRecords() == jobInfo.getTotalRecords()) {
            jobInfo.setJobStatus(Helper.STATUS_COMPLETED);
        }
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jobInfo);
        elasticSearchClient.prepareIndex(Helper.MYELT_ANALYTICS_INDEX, Helper.USERS_JOB_STATUS, String.valueOf(jobInfo.getJobId())).setSource(json).execute().actionGet();
    }
    
    public void createUsersIndex() throws IOException {
        if (!Helper.isIndexExist(USERS_INDEX, elasticSearchClient)) {
            
            elasticSearchClient.admin().indices().create(new CreateIndexRequest(USERS_INDEX)
                    .mapping(USERS_TYPE, buildUserTypeMappings())).actionGet();      
            
            TermsFilterBuilder usersOnlyFilter = FilterBuilders.termsFilter("recordType", USER_WITH_ACCESSCODE, USER_WITHOUT_ACCESSCODE);
            elasticSearchClient.admin().indices().prepareAliases().addAlias(USERS_INDEX, USERS_ONLY_ALIAS, usersOnlyFilter).execute().actionGet();
            
            TermsFilterBuilder accessCodesOnlyFilter = FilterBuilders.termsFilter("recordType", USER_WITH_ACCESSCODE, ADDITIONAL_ACCESSCODE);
            elasticSearchClient.admin().indices().prepareAliases().addAlias(USERS_INDEX, ACCESS_CODES_ONLY_ALIAS, accessCodesOnlyFilter).execute().actionGet();
        }      
    }
    
    public long getTotalUsersCount() throws JsonProcessingException {
        String sql = "select count(*) from users where type=0 and InstitutionID NOT IN " + Helper.IGNORE_INSTITUTIONS;
        long usersCount = jdbcTemplate.queryForObject(sql, Long.class);
        return usersCount;
    }
    
    public void updateLastJobInfoInES(String jobId) throws JsonProcessingException {       
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put(Helper.ID, jobId);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonMap);
        elasticSearchClient.prepareIndex(Helper.MYELT_ANALYTICS_INDEX, Helper.USERS_JOB_STATUS, Helper.LAST_JOB_ID).setSource(json).execute().actionGet();
    }
    
    public void refreshJobStatusFromES() throws JsonProcessingException {
        if (Helper.isIndexExist(Helper.MYELT_ANALYTICS_INDEX, elasticSearchClient) && Helper.isTypeExist(Helper.MYELT_ANALYTICS_INDEX, Helper.USERS_JOB_STATUS, elasticSearchClient)) {
            GetResponse lastJobInfoResponse = elasticSearchClient.prepareGet(Helper.MYELT_ANALYTICS_INDEX, Helper.USERS_JOB_STATUS, Helper.LAST_JOB_ID).execute().actionGet();
            Map<String,Object> lastJobInfoMap = lastJobInfoResponse.getSourceAsMap();
            String lastJobId = (String) lastJobInfoMap.get(Helper.ID);
            if(lastJobId != null){
                jobInfo.setJobId(lastJobId);
                GetResponse lastJobResponse = elasticSearchClient.prepareGet(Helper.MYELT_ANALYTICS_INDEX, Helper.USERS_JOB_STATUS, String.valueOf(lastJobId)).execute().actionGet();
                Map<String,Object> map  = lastJobResponse.getSourceAsMap();
                jobInfo.setLastId((String) map.get(Helper.LAST_ID));
                jobInfo.setSuccessRecords((Integer) map.get(Helper.SUCCESSFULL_RECORDS));
                jobInfo.setErrorRecords((Integer) map.get(Helper.ERROR_RECORDS));
                jobInfo.setTotalRecords((Integer) map.get(Helper.TOTAL_RECORDS));
                String jobStatus = (String) map.get(Helper.JOB_STATUS);
                //If server shut-down while job is running, status is still "InProgress" in Database, but the job is actually terminated/paused
                if (jobStatus.equals(Helper.STATUS_INPROGRESS)) {
                    jobInfo.setJobStatus(Helper.STATUS_PAUSED);
                    updateLastSyncedUserStatus();
                } else {
                    jobInfo.setJobStatus((String) map.get(Helper.JOB_STATUS));
                }
                
            }
        }
    }
    
    public XContentBuilder buildUserTypeMappings(){
        XContentBuilder builder = null; 
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject()
            .startObject("properties")
                .startObject("dateCreated")
                    .field("type", "date")                      
                .endObject()
                .startObject("dateLastLogin")
                    .field("type", "date")                      
                .endObject()
                .startObject("accessCode")
                    .startObject("properties")
                        .startObject("discipline")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()
                         .startObject("productName")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()
                         .startObject("dateCreated")
                            .field("type", "date")                      
                        .endObject()
                     .endObject()
                .endObject()
                .startObject("country")
                    .startObject("properties")
                        .startObject("name")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()
                     .endObject()
                .endObject()
                .startObject("userCountry")
                    .startObject("properties")
                        .startObject("name")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()
                     .endObject()
                .endObject()
                .startObject("region")
                    .field("type", "string")                      
                    .field("index", "not_analyzed")
                 .endObject()
                .startObject("courses")
                    .field("type", "string")                      
                    .field("index", "not_analyzed")
                 .endObject()
                 .startObject("productNames")
                    .field("type", "string")                      
                    .field("index", "not_analyzed")
                 .endObject()
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
                            .startObject("properties")
                                .startObject("name")
                                     .field("type", "string")                      
                                     .field("index", "not_analyzed")
                                 .endObject()
                             .endObject()
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
    
//  private void deletePreviousJobData()
//  {
//      //delete the user_type
//      elasticSearchClient.prepareDeleteByQuery(Helper.USERS_INDEX)
//          .setQuery(QueryBuilders.termQuery("_type", Helper.USERS_TYPE))
//          .execute()
//          .actionGet();
//      
//  }

}
