package myeltanalytics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
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
            GetResponse userIdStatusResponse = elasticSearchClient.prepareGet("myeltanalytics", "status", "lastUserId")
            .execute()
            .actionGet();
            Map<String,Object> map = userIdStatusResponse.getSourceAsMap();
            Integer userIdStatus = (Integer) map.get("id");
            if(userIdStatus!= null){
                LAST_USER_ID = userIdStatus; 
            }
        }
        catch (Exception ex){
            //will come when application is started first time
            //ignore if comes once
            LOGGER.error("User Status Index not found" ,ex);
        }
        try {
            GetResponse lastSubmissionStatusResponse = elasticSearchClient.prepareGet("myeltanalytics", "status", "lastActivitySubmissionId")
                .execute()
                .actionGet();
            
            Map<String,Object> map = lastSubmissionStatusResponse.getSourceAsMap();
            Integer lastSubmissionStatus = (Integer) map.get("id");
            if(lastSubmissionStatus!= null){
                LAST_ACTIVITY_SUBMISSION_ID = lastSubmissionStatus; 
            }
        }
        catch (Exception ex){
            //will come when application is started first time
            //ignore if comes once
            LOGGER.error("Last Activity Submission Status Index not found" ,ex);
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
                        try
                        {
                            long currentId = rs.getLong("id");
                            setLastUserStatus(currentId);
                            PushUserEvent event = new PushUserEvent("bca", "users", rs.getLong("id"));
                            PushDataListener.USER_POSTED_STATUS_MAP.put(rs.getLong("id"),Status.WAITING);
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
                "select id from assignmentresults where id > ?  and assignmentId IN (select assignmentId from assignments where AssignmentType =2 )",new Object[] { LAST_ACTIVITY_SUBMISSION_ID },
                new RowCallbackHandler()
                {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException
                    {
                        try {
                            long currentId = rs.getLong("id");
                            setLastActivitySubmissionStatus(currentId);
                            PushSubmissionEvent event = new PushSubmissionEvent("bca", "submissions", currentId);
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
    
    synchronized void setLastUserStatus(long userStatus) throws JsonProcessingException{
        LAST_USER_ID = userStatus;
        String json = "{\"id\": " + userStatus + "}";
        elasticSearchClient.prepareIndex("myeltanalytics", "status", "lastUserId").setSource(json).execute().actionGet();
    }
    
    synchronized void setLastActivitySubmissionStatus(long activitySubmissionStatus){
        LAST_ACTIVITY_SUBMISSION_ID = activitySubmissionStatus;
        String json = "{\"id\": " + activitySubmissionStatus + "}";
        elasticSearchClient.prepareIndex("myeltanalytics", "status", "lastActivitySubmissionId").setSource(json).execute().actionGet();
    }
}
