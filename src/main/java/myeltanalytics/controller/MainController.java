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
    public String getIndex(Model model) {
        
        long processedRecords = usersSyncService.jobInfo.getSuccessRecords() + usersSyncService.jobInfo.getErrorRecords();
        int percentageProcessed = (int)(((double)processedRecords / (double)usersSyncService.jobInfo.getTotalRecords()) * 100);
        
        model.addAttribute("usersJobPercent", percentageProcessed);
        model.addAttribute("usersJobInfo",usersSyncService.jobInfo);
        
        model.addAttribute("submissionsJobPercent", 0);
        model.addAttribute("submissionsJobInfo", submissionsSyncService.jobInfo);
        
        return "welcome";
    }
    
}
