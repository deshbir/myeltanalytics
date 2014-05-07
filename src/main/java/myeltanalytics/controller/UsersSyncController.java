package myeltanalytics.controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

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
@RequestMapping("/myeltanalytics/admin/users")
public class UsersSyncController {
  

    private static final String LAST_ID = "lastId";

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
    
    @RequestMapping(value= "/getSyncStatus")
    String getSyncStatus(Model model) throws JsonProcessingException{
        model.addAttribute("jobInfo",usersSyncListener.jobInfo);
        return "userJob";
    }  

    @RequestMapping(value= "/startSync")
    String startFreshSync() throws JsonProcessingException{
        
        String newJobId = UUID.randomUUID().toString();
        
        updateLastJobInfo(newJobId);
        
        usersSyncListener.jobInfo.setJobId(newJobId);
        usersSyncListener.jobInfo.setLastId(0);
        usersSyncListener.jobInfo.setSuccessRecords(0);
        usersSyncListener.jobInfo.setErrorRecords(0);
        usersSyncListener.jobInfo.setTotalRecords(getTotalUsersCount());
        usersSyncListener.jobInfo.setJobStatus(Helper.STATUS_INPROGRESS);        
        
        startSyncJob();
        
        return "redirect:getSyncStatus";
    }
    
    @RequestMapping(value= "/pauseSync")
    String pauseSync() throws JsonProcessingException{
        usersSyncListener.jobInfo.setJobStatus(Helper.STATUS_PAUSED);
        usersSyncListener.updateLastSyncedUserStatus();
        return "redirect:getSyncStatus";
    }
    
    @RequestMapping(value= "/resumeSync")
    String resumeSync() throws JsonProcessingException{  
        usersSyncListener.jobInfo.setJobStatus(Helper.STATUS_INPROGRESS);
        startSyncJob();
        return "redirect:getSyncStatus";
    } 
    
//    @RequestMapping(value= "/startNewSync")
//    String startNewUserJob() throws JsonProcessingException {
//        deletePreviousJobData();
//        startFreshUserJob();
//        return "redirect:getSyncStatus";
//    }   
    
    
    @RequestMapping(value= "/abortSync")
    String abortSync() throws JsonProcessingException{
        usersSyncListener.jobInfo.setJobStatus(Helper.STATUS_ABORTED);
        usersSyncListener.updateLastSyncedUserStatus();
        return "redirect:getSyncStatus";
    }
    
     
    private void updateLastJobInfo(String jobId) throws JsonProcessingException {       
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put(Helper.ID, jobId);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonMap);
        elasticSearchClient.prepareIndex(Helper.MYELT_ANALYTICS_INDEX, Helper.USERS_JOB_STATUS, Helper.LAST_JOB_ID).setSource(json).execute().actionGet();
    }
    
    private void startSyncJob() {
        try {
            jdbcTemplate.query(
                "select id from users where type=0 and InstitutionID NOT IN ('COMPROTEST','MYELT','TLTELT' ,'TLIBERO' ,'TLUS' ,'TEST' ,'TLEMEA' ,'TLASI') and id > ? order by id", new Object[] { usersSyncListener.jobInfo.getLastId() },
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
        } catch (Exception e) {
            LOGGER.error("Error starting User sync process", e);
        }
    }
    
    public void setUserSyncJobStatus() {
        if (Helper.isIndexExist(Helper.MYELT_ANALYTICS_INDEX, elasticSearchClient) && Helper.isTypeExist(Helper.MYELT_ANALYTICS_INDEX, Helper.USERS_JOB_STATUS, elasticSearchClient)) {
            GetResponse lastJobInfoResponse = elasticSearchClient.prepareGet(Helper.MYELT_ANALYTICS_INDEX, Helper.USERS_JOB_STATUS, Helper.LAST_JOB_ID).execute().actionGet();
            Map<String,Object> lastJobInfoMap = lastJobInfoResponse.getSourceAsMap();
            String lastJobId = (String) lastJobInfoMap.get(Helper.ID);
            if(lastJobId != null){
                usersSyncListener.jobInfo.setJobId(lastJobId);
                GetResponse lastJobResponse = elasticSearchClient.prepareGet(Helper.MYELT_ANALYTICS_INDEX, Helper.USERS_JOB_STATUS, String.valueOf(lastJobId)).execute().actionGet();
                Map<String,Object> map  = lastJobResponse.getSourceAsMap();
                usersSyncListener.jobInfo.setLastId((Integer) map.get(LAST_ID));
                usersSyncListener.jobInfo.setSuccessRecords((Integer) map.get(Helper.SUCCESSFULL_RECORDS));
                usersSyncListener.jobInfo.setErrorRecords((Integer) map.get(Helper.ERROR_RECORDS));
                usersSyncListener.jobInfo.setTotalRecords((Integer) map.get(Helper.TOTAL_RECORDS));
                usersSyncListener.jobInfo.setJobStatus((String) map.get(Helper.JOB_STATUS));
            }
        }
    }
    
    
//    private void deletePreviousJobData()
//    {
//        //delete the user_type
//        elasticSearchClient.prepareDeleteByQuery(Helper.USERS_INDEX)
//            .setQuery(QueryBuilders.termQuery("_type", Helper.USERS_TYPE))
//            .execute()
//            .actionGet();
//        
//    }
  
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
        return usersCount;
    }
    
    private XContentBuilder buildUserTypeMappings(){
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
    
    
}
