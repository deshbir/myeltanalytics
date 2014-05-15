package myeltanalytics.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import myeltanalytics.service.submissions.SubmissionsSyncService;
import myeltanalytics.service.users.UsersSyncService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import au.com.bytecode.opencsv.CSVWriter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class MainController {  
    
    @Autowired
    private UsersSyncService usersSyncService;
    
    @Autowired
    private SubmissionsSyncService submissionsSyncService;
    
    
    @RequestMapping("/")
    public String index() {        
        return "redirect:/admin";
    }
    
    @RequestMapping("/admin")
    public String admin(Model model) {
        
        long usersProcessedRecords = usersSyncService.jobInfo.getSuccessRecords() + usersSyncService.jobInfo.getErrorRecords();
        int usersPercentProcessed = (int)(((double)usersProcessedRecords / (double)usersSyncService.jobInfo.getTotalRecords()) * 100);
        
        model.addAttribute("usersJobPercent", usersPercentProcessed);
        model.addAttribute("usersJobInfo",usersSyncService.jobInfo);
        
        long submissionsRecordsProcessed = submissionsSyncService.jobInfo.getSuccessRecords() + submissionsSyncService.jobInfo.getErrorRecords();
        int submissionsPercentProcessed = (int)(((double)submissionsRecordsProcessed / (double)submissionsSyncService.jobInfo.getTotalRecords()) * 100);        
        model.addAttribute("submissionsJobPercent", submissionsPercentProcessed);
        model.addAttribute("submissionsJobInfo", submissionsSyncService.jobInfo);
        
        return "welcome";
    }
    
    @RequestMapping("/reports")
    public String reports(Model model) {        
        return "redirect:/reports/index.html";
    }
    
    @RequestMapping(value="/converToCSV",method=RequestMethod.POST)
    public void convertJSONToCSV(@RequestBody String resultData,HttpServletResponse response) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory(); // since 2.1 use mapper.getFactory() instead
        resultData = URLDecoder.decode(resultData, "UTF-8");
        resultData = resultData.substring(0,resultData.indexOf("="));
        JsonParser jp = factory.createParser(resultData);
        CSVWriter writer = new CSVWriter(response.getWriter());
        response.setContentType("application/csv");
     // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                "users.csv");
        response.setHeader(headerKey, headerValue);
        Cookie cookie = new Cookie("fileDownload","true");;
        response.addCookie(cookie);
        List<String[]> data = new ArrayList<String[]>();
        JsonNode actualObj = mapper.readTree(jp);
        if(actualObj.isArray()){
            Iterator<JsonNode> iterator = actualObj.iterator();
            while(iterator.hasNext()){
                JsonNode node = iterator.next();
                JsonNode dataObject = node.get("_source");
                List<String> list = new ArrayList<String>();
                list.add(dataObject.get("firstName").asText());
                list.add(dataObject.get("lastName").asText());
                list.add(dataObject.get("email").asText());
                String[] strArray = new String[list.size()];
                list.toArray(strArray);
                data.add(strArray);
            }
        } else {
            JsonNode node = actualObj;
            JsonNode dataObject = node.get("_source");
            List<String> list = new ArrayList<String>();
            list.add(dataObject.get("firstName").asText());
            list.add(dataObject.get("lastName").asText());
            //list.add(dataObject.get("email").asText());
            String[] strArray = new String[list.size()];
            list.toArray(strArray);
            data.add(strArray);
        }
        writer.writeAll(data);
        writer.close();
       
    }
    
}
