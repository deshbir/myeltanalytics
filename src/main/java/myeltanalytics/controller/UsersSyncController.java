package myeltanalytics.controller;

import myeltanalytics.service.Helper;
import myeltanalytics.service.users.UsersSyncService;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
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
            LOGGER.error("Error fetching Users Sync Job Status: ", e);
            return Helper.constructErrorResponse(Helper.DEFAULT_ERROR_MESSAGE);
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
        } catch (CannotGetJdbcConnectionException e) {
            LOGGER.error("Error communicating with MySQL Server.", e);
            return Helper.constructErrorResponse(Helper.MYSQL_ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Error Startng Users Sync Job.", e);
            return Helper.constructErrorResponse(Helper.DEFAULT_ERROR_MESSAGE);
        }
    }
    
    @RequestMapping(value= "/stopSync")
    @ResponseBody
    String pauseSync() throws JsonProcessingException, InterruptedException{
        try {
            usersSyncService.stopSync();        
            return Helper.constructSuccessResponse();
        } catch (CannotGetJdbcConnectionException e) {
            LOGGER.error("Error communicating with MySQL Server.", e);
            return Helper.constructErrorResponse(Helper.MYSQL_ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Error stopping Users Sync Job.", e);
            return Helper.constructErrorResponse(Helper.DEFAULT_ERROR_MESSAGE);
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
        } catch (CannotGetJdbcConnectionException e) {
            LOGGER.error("Error communicating with MySQL Server.", e);
            return Helper.constructErrorResponse(Helper.MYSQL_ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Error resuming Users Sync Job.", e);
            return Helper.constructErrorResponse(Helper.DEFAULT_ERROR_MESSAGE);
        }
    } 
}
