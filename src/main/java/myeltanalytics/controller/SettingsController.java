package myeltanalytics.controller;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/settings")
public class SettingsController {  
    
    @Value("${spring.datasource.url}")
    private String dbURL; 
    
    @RequestMapping("/mysqlinfo")
    @ResponseBody
    public String mysqlInfo() throws IOException, JSONException {
        JSONObject sqlInfoJSON = new JSONObject();
        sqlInfoJSON.put("dbURL", dbURL.substring(0, dbURL.indexOf("?")));
        return sqlInfoJSON.toString();
    } 
    
}
