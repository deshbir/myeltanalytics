package myeltanalytics;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.indices.IndexMissingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
public class MainController {
    public static final String BLANK = "";
  
    private long LAST_USER_ID = -1;
    
    @Autowired
    private Client elasticSearchClient;
    
    private long LAST_ACTIVITY_SUBMISSION_ID = -1;

    
    private final Logger LOGGER = Logger.getLogger(MainController.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private EventBusService eventBusService;
    
    @PostConstruct
    void initializeLastStatusParameters(){
        try {
            GetResponse userIdStatusResponse = elasticSearchClient.prepareGet("myEltAnalytics", "status", "lastUserId")
            .execute()
            .actionGet();
            GetResponse lastSubmissionStatusResponse = elasticSearchClient.prepareGet("myEltAnalytics", "status", "lastActivitySubmissionId")
                .execute()
                .actionGet();
            String userIdStatus = userIdStatusResponse.getSourceAsString();
            if(userIdStatus!= null && !userIdStatus.equals(BLANK)){
                LAST_USER_ID = Long.parseLong(userIdStatus); 
            }
            
            String lastSubmissionStatus = lastSubmissionStatusResponse.getSourceAsString();
            if(lastSubmissionStatus!= null && !lastSubmissionStatus.equals(BLANK)){
                LAST_ACTIVITY_SUBMISSION_ID = Long.parseLong(lastSubmissionStatus); 
            }
        } catch (IndexMissingException ex){
            //will come when application is started first time
            //ignore if comes once
            LOGGER.error("Status Index not found" ,ex);
        }
    }
    
    @RequestMapping("/")
    @ResponseBody public String getIndex() {
        return "Welcome to MyElt Analytics";
    }
    
    @RequestMapping(value= "/startPushingUser")
    @ResponseBody String putUserDataIntoElasticSearch() throws JsonProcessingException{
        //To-Do move this logic in eventBus if required 
        try {
            jdbcTemplate.query(
                "select id from users where type=0 and id > ? order by id", new Object[] { LAST_USER_ID },
                new RowCallbackHandler()
                {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException
                    {
                        setLastUserStatus(++LAST_USER_ID);
                        PushUserEvent event = new PushUserEvent("bca", "users", rs.getLong("id"));
                        PushDataListener.USER_POSTED_STATUS_MAP.put(rs.getLong("id"),Status.WAITING);
                        eventBusService.postEvent(event);
                        
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
                "select id from assignmentresults where id > ?  and assignmentId IN (select assignmentId from assignments where AssignmentType =2 )",new Object[] { LAST_ACTIVITY_SUBMISSION_ID },
                new RowCallbackHandler()
                {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException
                    {
                        setLastActivitySubmissionStatus(++LAST_ACTIVITY_SUBMISSION_ID);
                        PushSubmissionEvent event = new PushSubmissionEvent("bca", "submissions", rs.getLong("id"));
                        eventBusService.postEvent(event);
                        
                    }
                });
            LOGGER.debug("Started Submission sync process");
            return "Started Submission sync process";
        } catch (Exception e) {
            LOGGER.error("Error starting Submission sync process", e);
            return "Error starting Submission sync process";
        }
        
    }
    
    synchronized void setLastUserStatus(long userStatus){
        System.out.println();
        elasticSearchClient.prepareIndex("myEltAnalytics", "status", "lastUserId").setSource(userStatus).execute().actionGet();
    }
    
    synchronized void setLastActivitySubmissionStatus(long activitySubmissionStatus){
        elasticSearchClient.prepareIndex("myEltAnalytics", "status", "lastActivitySubmissionId").setSource(activitySubmissionStatus).execute().actionGet();
    }
}
