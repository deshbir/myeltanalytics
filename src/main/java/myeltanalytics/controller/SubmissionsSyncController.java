package myeltanalytics.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.annotation.PostConstruct;

import myeltanalytics.model.SyncSubmissionEvent;
import myeltanalytics.service.EventBusService;
import myeltanalytics.service.Helper;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
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
@RequestMapping("/submissions")
public class SubmissionsSyncController {
  
    public static long LAST_ACTIVITY_SUBMISSION_ID = -1;  
    
    private final Logger LOGGER = Logger.getLogger(SubmissionsSyncController.class);
    
    @Autowired
    private Client elasticSearchClient;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private EventBusService eventBusService;
    
    @PostConstruct
    void initializeLastStatusParameters(){
        setLastSyncedSubmissionId();
        createSubmissionIndex();
    }
    
    @RequestMapping(value= "/startSync")
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
                            SyncSubmissionEvent event = new SyncSubmissionEvent(Helper.SUBMISSIONS_INDEX, Helper.SUBMISSIONS_TYPE , currentId);
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
    
    private void setLastSyncedSubmissionId() {
        try {
            GetResponse lastSubmissionStatusResponse = elasticSearchClient.prepareGet(Helper.MYELT_ANALYTICS_INDEX, Helper.SUBMISSIONS_SYNC_JOB_TYPE, "lastActivitySubmissionId")
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

    private void createSubmissionIndex() {
        if (!Helper.isIndexExist(Helper.SUBMISSIONS_INDEX, elasticSearchClient)) {
            elasticSearchClient.admin().indices().create(new CreateIndexRequest(Helper.SUBMISSIONS_INDEX)
                    .mapping(Helper.SUBMISSIONS_TYPE, buildSumissionTypeMappings())).actionGet();
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
}
