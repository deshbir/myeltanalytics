package myeltanalytics.controller;

import myeltanalytics.service.HelperService;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rules")
public class RulesController {  
    
    @Autowired
    private HelperService helperService;
    
    @RequestMapping("/regionmap")
    @ResponseBody
    public String getRegionCountryMapping() throws JSONException {
        return helperService.getRegionCountryMap().toString();
    }
    
    @RequestMapping("/ignoreinstitutions")
    @ResponseBody
    public String getIgnoreInstitutions() throws JSONException {
        return HelperService.ignoreInstitutionsJson.toString();
    }
    
}
