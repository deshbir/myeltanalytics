package myeltanalytics.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;

import myeltanalytics.service.submissions.SubmissionsSyncService;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @PostConstruct
    void setup() throws IOException{
        submissionsSyncService.refreshJobStatusFromES();
        submissionsSyncService.createSubmissionsIndex();
    }   
         
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
            LOGGER.error("Error while fetching Users Sync Job Status: ", e);
            return "{\"status\":\"error\"}";
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
        } catch (Exception e) {
            LOGGER.error("Error while Startng Users Sync Job: ", e);
            return "{\"status\":\"error\"}";
        }
       
    }
    
    @RequestMapping(value= "/stopSync")
    @ResponseBody
    String pauseSync() throws JsonProcessingException, InterruptedException{
        try {
            submissionsSyncService.stopSync();        
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
            submissionsSyncService.resumeSync();
            JSONObject jobInfoJson = new JSONObject(SubmissionsSyncService.jobInfo);
            jobInfoJson.put("status", "success");
            return jobInfoJson.toString();
        } catch (Exception e) {
            LOGGER.error("Error while resuming Users Sync Job: ", e);
            return "{\"status\":\"error\"}";
        }
        
    } 
}
