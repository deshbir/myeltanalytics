package myeltanalytics.controller;

import java.io.IOException;

import javax.annotation.PostConstruct;

import myeltanalytics.service.users.UsersSyncService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
@RequestMapping("/myeltanalytics/admin/users")
public class UsersSyncController {
  
    @Autowired
    private UsersSyncService usersSyncService;
    
    @PostConstruct
    void setup() throws IOException{
        usersSyncService.refreshJobStatusFromES();
        usersSyncService.createUsersIndex();
    }   
    
    @RequestMapping(value= "/getSyncStatus")
    String getSyncStatus(Model model) throws JsonProcessingException{
        model.addAttribute("jobInfo",usersSyncService.jobInfo);
        return "userJob";
    }  

    @RequestMapping(value= "/startSync")
    String startFreshSync() throws JsonProcessingException{
        usersSyncService.startFreshSync();
        return "redirect:getSyncStatus";
    }
    
    @RequestMapping(value= "/stopSync")
    String pauseSync() throws JsonProcessingException, InterruptedException{
        usersSyncService.stopSync();        
        return "redirect:getSyncStatus";
    }
    
    @RequestMapping(value= "/resumeSync")
    String resumeSync() throws JsonProcessingException{ 
        usersSyncService.resumeSync();
        return "redirect:getSyncStatus";
    } 
}
