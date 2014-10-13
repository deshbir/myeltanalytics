package myeltanalytics.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/reports/myeltusage")
public class MyeltUsageReportsController
 {
	@RequestMapping(value= "/studentReg/{year}")
    @ResponseBody
    String getStudentRegData(@PathVariable("year") int year) throws JSONException{
		JSONObject stuRegJson = new JSONObject();
		Map<String,Integer> capesUserReg = new LinkedHashMap<String,Integer>();
		capesUserReg.put("Jun"+(year - 1 ), 121);
		capesUserReg.put("Jul"+(year - 1 ), 122);
		capesUserReg.put("Aug"+(year - 1 ), 123);
		capesUserReg.put("Sep"+(year - 1 ), 124);
		capesUserReg.put("Oct"+(year - 1 ), 125);
		capesUserReg.put("Nov"+(year - 1 ), 126);
		capesUserReg.put("Dec"+(year - 1 ), 127);
		capesUserReg.put("Jan"+year, 128);
		capesUserReg.put("Feb"+year, 129);
		capesUserReg.put("Mar"+year, 130);
		capesUserReg.put("Apr"+year, 131);
		capesUserReg.put("May"+year, 132);
		capesUserReg.put("Jun"+year, 133);
		capesUserReg.put("Last12Month", 1000);
		stuRegJson.put("CAPES", capesUserReg);
		stuRegJson.put("ICPNA", capesUserReg);
		stuRegJson.put("AllOther", capesUserReg);
		return stuRegJson.toString();
	}
	
	@RequestMapping(value= "/productReg/{year}")
    @ResponseBody
    String getProductRegData(@PathVariable("year") int year) throws JSONException{
		JSONObject prodRegJson = new JSONObject();
		Map<String,Integer> capesProdReg = new LinkedHashMap<String,Integer>();
		capesProdReg.put("Jun"+(year - 1 ), 121);
		capesProdReg.put("Jul"+(year - 1 ), 122);
		capesProdReg.put("Aug"+(year - 1 ), 123);
		capesProdReg.put("Sep"+(year - 1 ), 124);
		capesProdReg.put("Oct"+(year - 1 ), 125);
		capesProdReg.put("Nov"+(year - 1 ), 126);
		capesProdReg.put("Dec"+(year - 1 ), 127);
		capesProdReg.put("Jan"+year, 128);
		capesProdReg.put("Feb"+year, 129);
		capesProdReg.put("Mar"+year, 130);
		capesProdReg.put("Apr"+year, 131);
		capesProdReg.put("May"+year, 132);
		capesProdReg.put("Jun"+year, 133);
		capesProdReg.put("Last12Month", 1000);
		prodRegJson.put("CAPES", capesProdReg);
		prodRegJson.put("ICPNA", capesProdReg);
		prodRegJson.put("AllOther", capesProdReg);
		return prodRegJson.toString();
	}
	
	@RequestMapping(value= "/activeUsers/{year}")
    @ResponseBody
    String getActiveUseersData(@PathVariable("year") int year) throws JSONException{

		JSONObject activeUserJson = new JSONObject();
		Map<String,Integer> activeCapesUser = new LinkedHashMap<String,Integer>();
		activeCapesUser.put("Jun"+(year - 1 ), 121);
		activeCapesUser.put("Jul"+(year - 1 ), 122);
		activeCapesUser.put("Aug"+(year - 1 ), 123);
		activeCapesUser.put("Sep"+(year - 1 ), 124);
		activeCapesUser.put("Oct"+(year - 1 ), 125);
		activeCapesUser.put("Nov"+(year - 1 ), 126);
		activeCapesUser.put("Dec"+(year - 1 ), 127);
		activeCapesUser.put("Jan"+year, 128);
		activeCapesUser.put("Feb"+year, 129);
		activeCapesUser.put("Mar"+year, 130);
		activeCapesUser.put("Apr"+year, 131);
		activeCapesUser.put("May"+year, 132);
		activeCapesUser.put("Jun"+year, 133);
		activeUserJson.put("CAPES", activeCapesUser);
		activeUserJson.put("ICPNA", activeCapesUser);
		activeUserJson.put("AllOther", activeCapesUser);
		return activeUserJson.toString();
	}
    
}
