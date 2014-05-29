package myeltanalytics.service.users;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
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
    
    private final Logger LOGGER = Logger.getLogger(UsersSyncService.class);
    
    private ExecutorService userSyncExecutor = null;
    
    public static JobInfo jobInfo = new JobInfo();
    
    private static long recordsProcessed = 0l;
    
    public void startFreshSync() throws JsonProcessingException {
        
        String newJobId = UUID.randomUUID().toString();  
        LOGGER.info("Starting a fresh UsersSyncJob with syncJobId=" + newJobId);
        
        updateLastJobInfoInES(newJobId);
        
        jobInfo.setJobId(newJobId);
        jobInfo.setLastId("");
        jobInfo.setSuccessRecords(0);
        jobInfo.setErrorRecords(0);
        jobInfo.setTotalRecords(getTotalUsersCount());
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        jobInfo.setStartDateTime(dateFormat.format(date));
        
        jobInfo.setJobStatus(Helper.STATUS_INPROGRESS);        
        updateUserStatus();
        
        startSyncJob();
    }
    
    public void stopSync() throws InterruptedException, JsonProcessingException {
        LOGGER.info("Aborting UsersSyncJob with syncJobId=" + jobInfo.getJobId());
        jobInfo.setJobStatus(Helper.STATUS_PAUSED);
        updateUserStatus();
        
        userSyncExecutor.shutdown();
        userSyncExecutor.awaitTermination(1, TimeUnit.MINUTES);    
    }
    
    public void resumeSync() throws JsonProcessingException {
        LOGGER.info("Resuming old UsersSyncJob with syncJobId=" + jobInfo.getJobId());
        jobInfo.setJobStatus(Helper.STATUS_INPROGRESS);
        updateUserStatus();
       
        startSyncJob();
    }
    
    private void startSyncJob() {
        
        recordsProcessed = 0;
        userSyncExecutor = Executors.newFixedThreadPool(userSyncThreadPoolSize);
        
        String query = "select id from users where type=0 and InstitutionID NOT IN " + Helper.IGNORE_INSTITUTIONS;
        if (jobInfo.getLastId().equals("")) {
            query = query + " order by id limit " + Helper.SQL_RECORDS_LIMIT;
        } else {
            query = query + " and id > " + jobInfo.getLastId() + " order by id limit " + Helper.SQL_RECORDS_LIMIT;
        }
        
        /**
         * UserInstitutionMap handling
         * 1. Change query to use loginName instead of id  --- and change table to UserInstitutionMap
         * 2. Additinally select instId and pass it as a argument to Thread.
         */
        jdbcTemplate.query(query,
            new RowCallbackHandler()
            {
                @Override
                public void processRow(ResultSet rs) throws SQLException
                {
                    try {
                        String currentId = rs.getString("id");
                        Runnable worker = new UsersSyncThread(currentId);
                        userSyncExecutor.execute(worker);    
                    } catch (Exception e) {
                        LOGGER.error("Error while processing User row" ,e);
                    }
                }
            });
    }
    
    public synchronized void updateLastSyncedUserStatus() throws JsonProcessingException{
        
        boolean isCompleted = false;
        if (jobInfo.getErrorRecords() + jobInfo.getSuccessRecords() == jobInfo.getTotalRecords()) {
            isCompleted = true;
            jobInfo.setJobStatus(Helper.STATUS_COMPLETED);
        }
        
        updateUserStatus();
        recordsProcessed++;
        
        if (isCompleted) {
           //delete the records that have not been update/synced; they are records that have been deleted in database
            Helper.deleteUnsyncedRecords(elasticSearchClient, Helper.USERS_INDEX, Helper.USERS_TYPE, jobInfo.getJobId());
        } else {
            if (recordsProcessed == Helper.SQL_RECORDS_LIMIT) {
                startSyncJob();
            }
        }
    }
    
    public synchronized void updateUserStatus() throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jobInfo);
        elasticSearchClient.prepareIndex(Helper.MYELT_ANALYTICS_INDEX, Helper.USERS_JOB_STATUS, String.valueOf(jobInfo.getJobId())).setSource(json).execute().actionGet();
    }
    
   

    public void createUsersIndex() throws IOException {
        if (!Helper.isIndexExist(Helper.USERS_INDEX, elasticSearchClient)) {
            
            elasticSearchClient.admin().indices().create(new CreateIndexRequest(Helper.USERS_INDEX)
                    .mapping(Helper.USERS_TYPE, buildUserTypeMappings())).actionGet();      
            
            TermsFilterBuilder usersOnlyFilter = FilterBuilders.termsFilter("recordType", Helper.USER_WITH_ACCESSCODE, Helper.USER_WITHOUT_ACCESSCODE);
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Helper.USERS_INDEX, Helper.USERS_ALL_ALIAS, usersOnlyFilter).execute().actionGet();
            
            TermsFilterBuilder accessCodesOnlyFilter = FilterBuilders.termsFilter("recordType", Helper.USER_WITH_ACCESSCODE, Helper.ADDITIONAL_ACCESSCODE);
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Helper.USERS_INDEX, Helper.ACCESS_CODES_ALL_ALIAS, accessCodesOnlyFilter).execute().actionGet();
            
            AndFilterBuilder capesModelUsersFilter = FilterBuilders.andFilter(FilterBuilders.termsFilter("recordType", Helper.USER_WITH_ACCESSCODE, Helper.USER_WITHOUT_ACCESSCODE), FilterBuilders.termsFilter("studentType", Helper.CAPES_MODEL));
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Helper.USERS_INDEX, Helper.USERS_CAPES_ALIAS, capesModelUsersFilter).execute().actionGet();
        }      
    }
    
    public long getTotalUsersCount() throws JsonProcessingException {
        String sql = "select count(*) from users where type=0 and InstitutionID NOT IN " + Helper.IGNORE_INSTITUTIONS;
        long usersCount = jdbcTemplate.queryForObject(sql, Long.class);
        LOGGER.info("Total users to sync= " + usersCount + " for syncJobId= " + UsersSyncService.jobInfo.getJobId());
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
                jobInfo.setStartDateTime((String) map.get(Helper.START_DATETIME));
                String jobStatus = (String) map.get(Helper.JOB_STATUS);
                //If server shut-down while job is running, status is still "InProgress" in Database, but the job is actually terminated/paused
                if (jobStatus.equals(Helper.STATUS_INPROGRESS)) {
                    jobInfo.setJobStatus(Helper.STATUS_PAUSED);
                    updateUserStatus();
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
