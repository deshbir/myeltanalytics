package myeltanalytics.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import myeltanalytics.service.HelperService;
import myeltanalytics.service.usageReport.MyELTUsageReportService;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/reports/myeltusage")
public class MyeltUsageReportsController
 {
	@Autowired
	private MyELTUsageReportService myELTUsageReportService;

	@Autowired
	private HelperService helperService;
	
	
	
	@RequestMapping(value= "/studentReg/{year}")
    @ResponseBody
    String getStudentRegData(@PathVariable("year") int year) throws JSONException{
		JSONObject stuRegJson = new JSONObject();
		Map<String,Long> allOtherDataMap = myELTUsageReportService.getNewRegAllOtherReportData(year);
		Map<String,Long> capesUserDataMap = myELTUsageReportService.getNewRegCapesReportData(year);
		stuRegJson.put("CAPES", capesUserDataMap);
		stuRegJson.put("AllOther", allOtherDataMap);
		return stuRegJson.toString();
	}
	
	@RequestMapping(value= "/productReg/{year}")
    @ResponseBody
    String getProductRegData(@PathVariable("year") int year) throws JSONException{
		JSONObject prodRegJson = new JSONObject();
		Map<String,Integer> capesProdReg = new LinkedHashMap<String,Integer>();
		capesProdReg.put("Jun", 121);
		capesProdReg.put("Jul", 122);
		capesProdReg.put("Aug", 123);
		capesProdReg.put("Sep", 124);
		capesProdReg.put("Oct", 125);
		capesProdReg.put("Nov", 126);
		capesProdReg.put("Dec", 127);
		capesProdReg.put("Jan", 128);
		capesProdReg.put("Feb", 129);
		capesProdReg.put("Mar", 130);
		capesProdReg.put("Apr", 131);
		capesProdReg.put("May", 132);
		capesProdReg.put("Jun2", 133);
		capesProdReg.put("Last12Month", 1000);
		prodRegJson.put("CAPES", capesProdReg);
		prodRegJson.put("AllOther", capesProdReg);
		return prodRegJson.toString();
	}
	
	@RequestMapping(value= "/activeUsers/{year}")
    @ResponseBody
    String getActiveUseersData(@PathVariable("year") int year) throws JSONException{

		JSONObject activeUserJson = new JSONObject();
		Map<String,Integer> activeCapesUser = new LinkedHashMap<String,Integer>();
		/*activeCapesUser.put("Jun", 121);
		activeCapesUser.put("Jul", 122);
		activeCapesUser.put("Aug", 123);
		activeCapesUser.put("Sep", 124);
		activeCapesUser.put("Oct", 125);
		activeCapesUser.put("Nov", 126);
		activeCapesUser.put("Dec", 127);
		activeCapesUser.put("Jan", 128);
		activeCapesUser.put("Feb", 129);
		activeCapesUser.put("Mar", 130);
		activeCapesUser.put("Apr", 131);
		activeCapesUser.put("May", 132);*/
		activeCapesUser.put("Jun2", 133);
		activeUserJson.put("CAPES", activeCapesUser);
		activeUserJson.put("AllOther", activeCapesUser);
		return activeUserJson.toString();
	}
    
}
