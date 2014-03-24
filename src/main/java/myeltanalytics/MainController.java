package myeltanalytics;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
public class MainController {
    
    private final Logger LOGGER = Logger.getLogger(MainController.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private EventBusService eventBusService;
    
    @RequestMapping("/")
    public String getIndex() {
        return "Welcome to MyElt Analytics";
    }
    
    @RequestMapping(value= "/startPushingUser")
    @ResponseBody String putUserDataIntoElasticSearch() throws JsonProcessingException{
        //To-Do move this logic in eventBus if required 
        try {
            jdbcTemplate.query(
                "select id from users where type=0",
                new RowCallbackHandler()
                {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException
                    {
                        PushUserEvent event = new PushUserEvent("bca", "users", rs.getLong("id"));
                        PushDataListener.USER_POSTED_STATUS_MAP.put(rs.getLong("id"),Status.WAITING);
                        eventBusService.postEvent(event);
                        
                    }
                });
            LOGGER.debug("Started sync process");
            return "Started User sync process";
        } catch (Exception e) {
            LOGGER.error("Error starting sync process", e);
            return "Error starting User sync process";
        }
        
    }  
    
    
    @RequestMapping(value= "/startPushingSubmissions")
    @ResponseBody String putSubmissionDataIntoElasticSearch() throws JsonProcessingException{
        //To-Do move this logic in eventBus if required 
        try {
            jdbcTemplate.query(
                "select id from assignmentresults where assignmentId IN (select assignmentId from assignments where AssignmentType =2 )",
                new RowCallbackHandler()
                {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException
                    {
                        PushSubmissionEvent event = new PushSubmissionEvent("bca", "submissions", rs.getLong("id"));
                        eventBusService.postEvent(event);
                        
                    }
                });
            LOGGER.debug("Started Submission sync process");
            return "Started sync process";
        } catch (Exception e) {
            LOGGER.error("Error starting sync process", e);
            return "Error starting Submission sync process";
        }
        
    }  
}
