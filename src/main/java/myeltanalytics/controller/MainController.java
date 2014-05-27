package myeltanalytics.controller;

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
    
    
    @RequestMapping("/")
    public String index() {        
        return "redirect:/reports";
    }
    
    @RequestMapping("/admin")
    public String admin(Model model) {
        
        long usersProcessedRecords = UsersSyncService.jobInfo.getSuccessRecords() + UsersSyncService.jobInfo.getErrorRecords();
        int usersPercentProcessed = (int)(((double)usersProcessedRecords / (double)UsersSyncService.jobInfo.getTotalRecords()) * 100);
        
        model.addAttribute("usersJobPercent", usersPercentProcessed);
        model.addAttribute("usersJobInfo",UsersSyncService.jobInfo);
        
        long submissionsRecordsProcessed = SubmissionsSyncService.jobInfo.getSuccessRecords() + SubmissionsSyncService.jobInfo.getErrorRecords();
        int submissionsPercentProcessed = (int)(((double)submissionsRecordsProcessed / (double)SubmissionsSyncService.jobInfo.getTotalRecords()) * 100);        
        model.addAttribute("submissionsJobPercent", submissionsPercentProcessed);
        model.addAttribute("submissionsJobInfo", SubmissionsSyncService.jobInfo);
        
        return "admin";
    }
    
    
}
