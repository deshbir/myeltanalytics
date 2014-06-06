package myeltanalytics.controller;

import java.io.IOException;

import myeltanalytics.service.submissions.SubmissionsSyncService;
import myeltanalytics.service.users.UsersSyncService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {  
    
    @Autowired
    private UsersSyncService usersSyncService;
    
    @Autowired
    private SubmissionsSyncService submissionsSyncService;
    
    private static boolean isESSetup;
    
    
    @RequestMapping("/")
    public String index() {        
        return "redirect:/app/index.html";
    }
    
    @RequestMapping("/sync")
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
    
    @RequestMapping("/admin")
    public String admin(Model model) throws IOException {
        return "redirect:/app/index.html";
    }
    
    
}
