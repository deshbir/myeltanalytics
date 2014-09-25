package myeltanalytics.controller;

import java.io.IOException;

import myeltanalytics.model.Constants;
import myeltanalytics.service.HelperService;
import myeltanalytics.service.submissions.SubmissionsSyncService;
import myeltanalytics.service.users.UsersSyncService;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
@RequestMapping("/sync")
public class SyncController {
  
    @Autowired
    private UsersSyncService usersSyncService;
    
    @Autowired
    private SubmissionsSyncService submissionsSyncService;
    
    @Autowired
    private HelperService helperService;
    
    private final Logger LOGGER = Logger.getLogger(SyncController.class);
    
    private static boolean isESSetup;
    
    @RequestMapping("")
    public String sync(Model model) throws IOException {
        
        //Setting up(One-Time) required indexes in ElasticSearch.
        if (!isESSetup) {
            usersSyncService.setup();
            submissionsSyncService.setup();
            isESSetup = true;
        }
        
        long usersProcessedRecords = UsersSyncService.jobInfo.getSuccessRecords() + UsersSyncService.jobInfo.getErrorRecords();
        int usersPercentProcessed = (int)(((double)usersProcessedRecords / (double)UsersSyncService.jobInfo.getTotalRecords()) * 100);
        
        model.addAttribute("usersJobPercent", usersPercentProcessed);
        model.addAttribute("usersJobInfo",UsersSyncService.jobInfo);
        
        long submissionsRecordsProcessed = SubmissionsSyncService.jobInfo.getSuccessRecords() + SubmissionsSyncService.jobInfo.getErrorRecords();
        int submissionsPercentProcessed = (int)(((double)submissionsRecordsProcessed / (double)SubmissionsSyncService.jobInfo.getTotalRecords()) * 100);        
        model.addAttribute("submissionsJobPercent", submissionsPercentProcessed);
        model.addAttribute("submissionsJobInfo", SubmissionsSyncService.jobInfo);
        
        return "sync";
    }   
   
    @RequestMapping(value= "/users/getSyncStatus")
    @ResponseBody
    String getUsersSyncStatus() { 
        try {
            JSONObject jobInfoJson = new JSONObject(UsersSyncService.jobInfo);
            long processedRecords = UsersSyncService.jobInfo.getSuccessRecords() + UsersSyncService.jobInfo.getErrorRecords();
            int percentageProcessed = (int)(((double)processedRecords / (double)UsersSyncService.jobInfo.getTotalRecords()) * 100);
            jobInfoJson.put("percent", percentageProcessed);
            jobInfoJson.put("status", "success");
            return jobInfoJson.toString();
        } catch (Exception e) {
            LOGGER.error("Error fetching Users Sync Job Status: ", e);
            return helperService.constructErrorResponse(Constants.DEFAULT_ERROR_MESSAGE);
        }
    }  

    @RequestMapping(value= "/users/startSync")
    @ResponseBody
    String startFreshUsersSync() throws JsonProcessingException{
        try {
            usersSyncService.startFreshSync(false);
            JSONObject jobInfoJson = new JSONObject(UsersSyncService.jobInfo);
            jobInfoJson.put("status", "success");
            return jobInfoJson.toString();
        } catch (CannotGetJdbcConnectionException e) {
            LOGGER.error("Error communicating with MySQL Server.", e);
            return helperService.constructErrorResponse(Constants.MYSQL_ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Error Startng Users Sync Job.", e);
            return helperService.constructErrorResponse(Constants.DEFAULT_ERROR_MESSAGE);
        }
    }
    
    @RequestMapping(value= "/users/stopSync")
    @ResponseBody
    String pauseUsersSync() throws JsonProcessingException, InterruptedException{
        try {
            usersSyncService.stopSync();        
            JSONObject jobInfoJson = new JSONObject(UsersSyncService.jobInfo);
            jobInfoJson.put("status", "success");
            return jobInfoJson.toString();
        } catch (CannotGetJdbcConnectionException e) {
            LOGGER.error("Error communicating with MySQL Server.", e);
            return helperService.constructErrorResponse(Constants.MYSQL_ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Error stopping Users Sync Job.", e);
            return helperService.constructErrorResponse(Constants.DEFAULT_ERROR_MESSAGE);
        }
    }
    
    @RequestMapping(value= "/users/resumeSync")
    @ResponseBody
    String resumeUsersSync() throws JsonProcessingException{ 
        try {
            usersSyncService.resumeSync();
            JSONObject jobInfoJson = new JSONObject(UsersSyncService.jobInfo);
            jobInfoJson.put("status", "success");
            return jobInfoJson.toString();
        } catch (CannotGetJdbcConnectionException e) {
            LOGGER.error("Error communicating with MySQL Server.", e);
            return helperService.constructErrorResponse(Constants.MYSQL_ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Error resuming Users Sync Job.", e);
            return helperService.constructErrorResponse(Constants.DEFAULT_ERROR_MESSAGE);
        }
    } 
    
    @RequestMapping(value= "/submissions/getSyncStatus")
    @ResponseBody
    String getSubmissionsSyncStatus() { 
        try {
            JSONObject jobInfoJson = new JSONObject(SubmissionsSyncService.jobInfo);
            long processedRecords = SubmissionsSyncService.jobInfo.getSuccessRecords() + SubmissionsSyncService.jobInfo.getErrorRecords();
            int percentageProcessed = (int)(((double)processedRecords / (double)SubmissionsSyncService.jobInfo.getTotalRecords()) * 100);
            jobInfoJson.put("percent", percentageProcessed);
            jobInfoJson.put("status", "success");
            return jobInfoJson.toString();
        } catch (Exception e) {
            LOGGER.error("Error fetching Submissions Sync Job Status: ", e);
            return helperService.constructErrorResponse(Constants.DEFAULT_ERROR_MESSAGE);
        }
    }  

    @RequestMapping(value= "/submissions/startSync")
    @ResponseBody
    String startFreshSubmissionsSync() throws JsonProcessingException{
        try {
            submissionsSyncService.startFreshSync();
            JSONObject jobInfoJson = new JSONObject(SubmissionsSyncService.jobInfo);
            jobInfoJson.put("status", "success");
            return jobInfoJson.toString();
        } catch (CannotGetJdbcConnectionException e) {
            LOGGER.error("Error communicating with MySQL Server.", e);
            return helperService.constructErrorResponse(Constants.MYSQL_ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Error Startng Submissions Sync Job.", e);
            return helperService.constructErrorResponse(Constants.DEFAULT_ERROR_MESSAGE);
        }
       
    }
    
    @RequestMapping(value= "/submissions/stopSync")
    @ResponseBody
    String pauseSubmissionsSync() throws JsonProcessingException, InterruptedException{
        try {
            submissionsSyncService.stopSync();        
            return helperService.constructSuccessResponse();
        } catch (CannotGetJdbcConnectionException e) {
            LOGGER.error("Error communicating with MySQL Server.", e);
            return helperService.constructErrorResponse(Constants.MYSQL_ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Error stopping Submissions Sync Job.", e);
            return helperService.constructErrorResponse(Constants.DEFAULT_ERROR_MESSAGE);
        }
      
    }
    
    @RequestMapping(value= "/submissions/resumeSync")
    @ResponseBody
    String resumeSubmissionsSync() throws JsonProcessingException{ 
        try {
            submissionsSyncService.resumeSync();
            JSONObject jobInfoJson = new JSONObject(SubmissionsSyncService.jobInfo);
            jobInfoJson.put("status", "success");
            return jobInfoJson.toString();
        } catch (CannotGetJdbcConnectionException e) {
            LOGGER.error("Error communicating with MySQL Server.", e);
            return helperService.constructErrorResponse(Constants.MYSQL_ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Error resuming Submissions Sync Job.", e);
            return helperService.constructErrorResponse(Constants.DEFAULT_ERROR_MESSAGE);
        }
        
    }

    @RequestMapping(value= "/users/failedUserSync")
    @ResponseBody
    public String failedUserSync() throws JsonProcessingException{ 
    	try {
            usersSyncService.startFreshSync(true);
            JSONObject jobInfoJson = new JSONObject(UsersSyncService.jobInfo);
            jobInfoJson.put("status", "success");
            return jobInfoJson.toString();
        } catch (CannotGetJdbcConnectionException e) {
            LOGGER.error("Error communicating with MySQL Server.", e);
            return helperService.constructErrorResponse(Constants.MYSQL_ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Error Startng Users Sync Job.", e);
            return helperService.constructErrorResponse(Constants.DEFAULT_ERROR_MESSAGE);
        }
    }
}
