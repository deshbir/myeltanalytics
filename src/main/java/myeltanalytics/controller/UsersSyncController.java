package myeltanalytics.controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import myeltanalytics.model.JobStatus;
import myeltanalytics.model.SyncUserEvent;
import myeltanalytics.service.EventBusService;
import myeltanalytics.service.Helper;
import myeltanalytics.service.listener.UsersSyncListener;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/users")
public class UsersSyncController {
  

    private static final String LAST_ID = "lastId";


    private static int USER_QUERY_LIMIT = 0;

    
    private final Logger LOGGER = Logger.getLogger(UsersSyncController.class);
    
    @Autowired
    private Client elasticSearchClient;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private UsersSyncListener usersSyncListener;
    
    @Autowired
    private EventBusService eventBusService;


    
    @PostConstruct
    void initializeLastStatusParameters() throws IOException{
        setUserSyncJobStatus();
        createUsersIndex();
    }  
    
    @RequestMapping(value= "/startNewSync")
    String startNewUserJob() throws JsonProcessingException {
        deletePreviousJobData();
        startFreshUserJob();
        return "redirect:getSyncStatus";
    }   
    

    @RequestMapping(value= "/startFreshSync")
    String startFreshUserJob() throws JsonProcessingException{
        long lastUserJob =  usersSyncListener.jobStatus.getJobId();
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put(Helper.ID,++lastUserJob);
        jsonMap.put(Helper.JOB_STATUS_PAUSED,false);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonMap);
        elasticSearchClient.prepareIndex(Helper.MYELT_ANALYTICS_INDEX, Helper.USERS_JOB_STATUS, Helper.LAST_JOB_ID).setSource(json).execute().actionGet();
        usersSyncListener.jobStatus.setJobId(lastUserJob);
        usersSyncListener.jobStatus.setLastId(-1);
        usersSyncListener.jobStatus.setSuccessRecords(0);
        usersSyncListener.jobStatus.setErrorRecords(0);
        usersSyncListener.jobStatus.setTotalRecords(getTotalUsersCount());
        usersSyncListener.setLastUserStatus(0);
        startSyncUsers();
        return "redirect:getSyncStatus";
    }
    
    @RequestMapping(value= "/getSyncStatus")
    String getLastUserJobStatus(Model model) throws JsonProcessingException{
        boolean isCompleted;
        JobStatus jobStatus = usersSyncListener.jobStatus;
        model.addAttribute("jobStatus",jobStatus);
        if(jobStatus.getSuccessRecords() + jobStatus.getErrorRecords() >= jobStatus.getTotalRecords()){
            isCompleted = true;
            usersSyncListener.isPaused = true;
        } else {
            isCompleted = false;
        }
        model.addAttribute("isCompleted",isCompleted);
        model.addAttribute("isPaused",usersSyncListener.isPaused);
        return "userJob";
    }
    
    
    @RequestMapping(value= "/stopSync")
    String stopJob() throws JsonProcessingException{
        usersSyncListener.isPaused = true;
        long lastUserJob =  usersSyncListener.jobStatus.getJobId();
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put(Helper.ID,lastUserJob);
        jsonMap.put(Helper.JOB_STATUS_PAUSED,true);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonMap);
        elasticSearchClient.prepareIndex(Helper.MYELT_ANALYTICS_INDEX, Helper.USERS_JOB_STATUS, Helper.LAST_JOB_ID).setSource(json).execute().actionGet();
        return "redirect:getSyncStatus";
    }
    
    @RequestMapping(value= "/deleteLastSync")
    String deleteLastJob() throws JsonProcessingException{
        deletePreviousJobData();
        usersSyncListener.jobStatus.setSuccessRecords(0);
        usersSyncListener.jobStatus.setErrorRecords(0);
        return "redirect:getSyncStatus";
    }
    
    @RequestMapping(value= "/resumeSync")
    String startSyncUsers() throws JsonProcessingException{  
        usersSyncListener.isPaused = false;
        long totalLeftRecords =  usersSyncListener.jobStatus.getTotalRecords() - usersSyncListener.jobStatus.getSuccessRecords() - usersSyncListener.jobStatus.getErrorRecords();
        try {
            jdbcTemplate.query(
                "select id from users where type=0 and id > ? order by id LIMIT ?", new Object[] { usersSyncListener.jobStatus.getLastId(),totalLeftRecords },
                new RowCallbackHandler()
                {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException
                    {
                        try
                        {
                            SyncUserEvent event = new SyncUserEvent(Helper.USERS_INDEX, Helper.USERS_TYPE, rs.getLong("id"));
                            eventBusService.postEvent(event);
                        } catch(Exception e){
                            LOGGER.error("Error while processing User row" ,e);
                        }
                        
                    }
                });
            LOGGER.debug("Started User sync process");
            return "redirect:getSyncStatus";
        } catch (Exception e) {
            LOGGER.error("Error starting User sync process", e);
            return "";
        }
        
    }      
    
    
    public void setUserSyncJobStatus() {
        try {
            GetResponse lastJobIdResponse = elasticSearchClient.prepareGet(Helper.MYELT_ANALYTICS_INDEX, Helper.USERS_JOB_STATUS, Helper.LAST_JOB_ID).execute().actionGet();
            Map<String,Object> map = lastJobIdResponse.getSourceAsMap();
            long lastJobId = (Integer) map.get(Helper.ID);
            if(lastJobId != 0){
                usersSyncListener.jobStatus.setJobId(lastJobId);
                GetResponse lastJobResponse = elasticSearchClient.prepareGet(Helper.MYELT_ANALYTICS_INDEX, Helper.USERS_JOB_STATUS, String.valueOf(lastJobId)).execute().actionGet();
                map  = lastJobResponse.getSourceAsMap();
                usersSyncListener.jobStatus.setLastId((Integer) map.get(LAST_ID));
                usersSyncListener.jobStatus.setSuccessRecords((Integer) map.get(Helper.SUCCESSFULL_RECORDS));
                usersSyncListener.jobStatus.setErrorRecords((Integer) map.get(Helper.ERROR_RECORDS));
                usersSyncListener.jobStatus.setTotalRecords((Integer) map.get(Helper.TOTAL_RECORDS));
            }
        }
        catch (Exception e){
            //will come when application is started first time
            //ignore if comes once
            usersSyncListener.jobStatus.setJobId(0);
            LOGGER.error("An error occured while reading last synced UserId from ElasticSearch" ,e);
        }
    }
    
    
    private void deletePreviousJobData()
    {
        //delete the user_type
        elasticSearchClient.prepareDeleteByQuery(Helper.USERS_INDEX)
            .setQuery(QueryBuilders.termQuery("_type", Helper.USERS_TYPE))
            .execute()
            .actionGet();
        
    }
  
    private void createUsersIndex() throws IOException {
        if (!Helper.isIndexExist(Helper.USERS_INDEX, elasticSearchClient)) {
            
            elasticSearchClient.admin().indices().create(new CreateIndexRequest(Helper.USERS_INDEX)
                    .mapping(Helper.USERS_TYPE, buildUserTypeMappings())).actionGet();      
            
            TermsFilterBuilder usersOnlyFilter = FilterBuilders.termsFilter("recordType", Helper.USER_WITH_ACCESSCODE, Helper.USER_WITHOUT_ACCESSCODE);
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Helper.USERS_INDEX, Helper.USERS_ONLY_ALIAS, usersOnlyFilter).execute().actionGet();
            
            TermsFilterBuilder accessCodesOnlyFilter = FilterBuilders.termsFilter("recordType", Helper.USER_WITH_ACCESSCODE, Helper.ADDITIONAL_ACCESSCODE);
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Helper.USERS_INDEX, Helper.ACCESS_CODES_ONLY_ALIAS, accessCodesOnlyFilter).execute().actionGet();
        }      
    }
    
    private long getTotalUsersCount() throws JsonProcessingException {
        String sql = "SELECT COUNT(*) FROM users";
        long usersCount = jdbcTemplate.queryForObject(sql, Long.class);
        if (USER_QUERY_LIMIT > 0 && usersCount > USER_QUERY_LIMIT) {
            return USER_QUERY_LIMIT;
        }
        return usersCount;
    }
    
    private XContentBuilder buildUserTypeMappings(){
        XContentBuilder builder = null; 
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject()
            .startObject("properties")
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
    
    
}
