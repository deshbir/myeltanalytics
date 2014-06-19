package myeltanalytics.controller;

import myeltanalytics.service.Helper;

import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rules")
public class RulesController {  
    
    @RequestMapping("/regionmap")
    @ResponseBody
    public String getRegionCountryMapping() throws JSONException {
        return Helper.getCountryDocumentAsJson().toString();
    }
    
}
