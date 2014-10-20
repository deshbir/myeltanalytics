package myeltanalytics.controller;

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
	
	
	
	@RequestMapping(value= "/{year}/{month}")
    @ResponseBody
    String getStudentRegData(@PathVariable("year") int year,@PathVariable("month") int month) throws JSONException{
		JSONObject jsonObj = new JSONObject();
		JSONObject newUsersRegObj = new JSONObject();
		JSONObject newProdRegObj = new JSONObject();
		JSONObject activeUsersAccountDataObj = new JSONObject();
		Map<String,Long> newUsersRegCapesDataMap = myELTUsageReportService.getNewUsersRegCapesReportData(year,month);
		Map<String,Long> newUsersRegAllOtherDataMap = myELTUsageReportService.getNewUsersRegAllOtherReportData(year,month);
		Map<String,Long> newProdRegCapesDataMap = myELTUsageReportService.getNewProdRegCapesReportData(year, month);
		Map<String,Long> newProdRegAllOtherDataMap = myELTUsageReportService.getNewProdRegAllOtherReportData(year, month);
		Map<String,Long> activeUsersCapesDataMap = myELTUsageReportService.getactiveUsersCapesReportData(year, month);
		Map<String,Long> activeUsersAllOtherDataMap = myELTUsageReportService.getactiveUsersAllOtherReportData(year, month);
		newUsersRegObj.put("CAPES", newUsersRegCapesDataMap);
		newUsersRegObj.put("AllOther", newUsersRegAllOtherDataMap);
		newProdRegObj.put("CAPES", newProdRegCapesDataMap);
		newProdRegObj.put("AllOther", newProdRegAllOtherDataMap);
		activeUsersAccountDataObj.put("CAPES", activeUsersCapesDataMap);
		activeUsersAccountDataObj.put("AllOther", activeUsersAllOtherDataMap);
		jsonObj.put("new_student_registrations",newUsersRegObj);
		jsonObj.put("new_product_registrations", newProdRegObj);
		jsonObj.put("active_user_accounts", activeUsersAccountDataObj);
		return jsonObj.toString();
	}
}
