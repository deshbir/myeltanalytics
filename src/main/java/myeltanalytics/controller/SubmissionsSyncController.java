package myeltanalytics.controller;

import myeltanalytics.service.Helper;
import myeltanalytics.service.submissions.SubmissionsSyncService;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
@RequestMapping("/submissions")
public class SubmissionsSyncController {
  
    @Autowired
    private SubmissionsSyncService submissionsSyncService;
    
    private final Logger LOGGER = Logger.getLogger(SubmissionsSyncController.class);
    
    @RequestMapping(value= "/getSyncStatus")
    @ResponseBody
    String getSyncStatus() { 
        try {
            JSONObject jobInfoJson = new JSONObject(SubmissionsSyncService.jobInfo);
            long processedRecords = SubmissionsSyncService.jobInfo.getSuccessRecords() + SubmissionsSyncService.jobInfo.getErrorRecords();
            int percentageProcessed = (int)(((double)processedRecords / (double)SubmissionsSyncService.jobInfo.getTotalRecords()) * 100);
            jobInfoJson.put("percent", percentageProcessed);
            jobInfoJson.put("status", "success");
            return jobInfoJson.toString();
        } catch (Exception e) {
            LOGGER.error("Error fetching Submissions Sync Job Status: ", e);
            return Helper.constructErrorResponse(Helper.DEFAULT_ERROR_MESSAGE);
        }
    }  

    @RequestMapping(value= "/startSync")
    @ResponseBody
    String startFreshSync() throws JsonProcessingException{
        try {
            submissionsSyncService.startFreshSync();
            JSONObject jobInfoJson = new JSONObject(SubmissionsSyncService.jobInfo);
            jobInfoJson.put("status", "success");
            return jobInfoJson.toString();
        } catch (CannotGetJdbcConnectionException e) {
            LOGGER.error("Error communicating with MySQL Server.", e);
            return Helper.constructErrorResponse(Helper.MYSQL_ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Error Startng Submissions Sync Job.", e);
            return Helper.constructErrorResponse(Helper.DEFAULT_ERROR_MESSAGE);
        }
       
    }
    
    @RequestMapping(value= "/stopSync")
    @ResponseBody
    String pauseSync() throws JsonProcessingException, InterruptedException{
        try {
            submissionsSyncService.stopSync();        
            return Helper.constructSuccessResponse();
        } catch (CannotGetJdbcConnectionException e) {
            LOGGER.error("Error communicating with MySQL Server.", e);
            return Helper.constructErrorResponse(Helper.MYSQL_ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Error stopping Submissions Sync Job.", e);
            return Helper.constructErrorResponse(Helper.DEFAULT_ERROR_MESSAGE);
        }
      
    }
    
    @RequestMapping(value= "/resumeSync")
    @ResponseBody
    String resumeSync() throws JsonProcessingException{ 
        try {
            submissionsSyncService.resumeSync();
            JSONObject jobInfoJson = new JSONObject(SubmissionsSyncService.jobInfo);
            jobInfoJson.put("status", "success");
            return jobInfoJson.toString();
        } catch (CannotGetJdbcConnectionException e) {
            LOGGER.error("Error communicating with MySQL Server.", e);
            return Helper.constructErrorResponse(Helper.MYSQL_ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Error resuming Submissions Sync Job.", e);
            return Helper.constructErrorResponse(Helper.DEFAULT_ERROR_MESSAGE);
        }
        
    } 
}
