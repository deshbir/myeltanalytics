package myeltanalytics.controller;

import myeltanalytics.service.users.UsersSyncService;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
@RequestMapping("/users")
public class UsersSyncController {
  
    @Autowired
    private UsersSyncService usersSyncService;
    
    private final Logger LOGGER = Logger.getLogger(UsersSyncController.class);
    
    @RequestMapping(value= "/getSyncStatus")
    @ResponseBody
    String getSyncStatus() { 
        try {
            JSONObject jobInfoJson = new JSONObject(UsersSyncService.jobInfo);
            long processedRecords = UsersSyncService.jobInfo.getSuccessRecords() + UsersSyncService.jobInfo.getErrorRecords();
            int percentageProcessed = (int)(((double)processedRecords / (double)UsersSyncService.jobInfo.getTotalRecords()) * 100);
            jobInfoJson.put("percent", percentageProcessed);
            jobInfoJson.put("status", "success");
            return jobInfoJson.toString();
        } catch (Exception e) {
            LOGGER.error("Error while fetching Users Sync Job Status: ", e);
            return "{\"status\":\"error\"}";
        }
    }  

    @RequestMapping(value= "/startSync")
    @ResponseBody
    String startFreshSync() throws JsonProcessingException{
        try {
            usersSyncService.startFreshSync();
            JSONObject jobInfoJson = new JSONObject(UsersSyncService.jobInfo);
            jobInfoJson.put("status", "success");
            return jobInfoJson.toString();
        } catch (Exception e) {
            LOGGER.error("Error while Startng Users Sync Job: ", e);
            return "{\"status\":\"error\"}";
        }
       
    }
    
    @RequestMapping(value= "/stopSync")
    @ResponseBody
    String pauseSync() throws JsonProcessingException, InterruptedException{
        try {
            usersSyncService.stopSync();        
            return "{\"status\":\"success\"}";
        } catch (Exception e) {
            LOGGER.error("Error while stopping Users Sync Job: ", e);
            return "{\"status\":\"error\"}";
        }
      
    }
    
    @RequestMapping(value= "/resumeSync")
    @ResponseBody
    String resumeSync() throws JsonProcessingException{ 
        try {
            usersSyncService.resumeSync();
            JSONObject jobInfoJson = new JSONObject(UsersSyncService.jobInfo);
            jobInfoJson.put("status", "success");
            return jobInfoJson.toString();
        } catch (Exception e) {
            LOGGER.error("Error while resuming Users Sync Job: ", e);
            return "{\"status\":\"error\"}";
        }
        
    } 
}
