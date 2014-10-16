package myeltanalytics.service.usageReport;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import myeltanalytics.model.Constants;

import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="myELTUsageReportService")
public class MyELTUsageReportService {
	
	 @Autowired
	 private Client elasticSearchClient;
	 
	 
	 public Map<String,Long> getNewRegAllOtherReportData(int year){
			int month =6;
			Map<String,Long>  dataMap = new LinkedHashMap<String,Long>();
			if(year > 0){
				year = year+2000;
			}else{
				year = year + 1900;
			}
			long last12Month = 0;
			String startDate,endDate;
			long[] dataArray = new long[14];
			for(int i  = 0; i < dataArray.length; i++){
				if(month > 12){
					month = 1;
					startDate = year + "-" + month + "-01T00:00:00";
					endDate   = year + "-" + month + "-"+ getDaysInMonth(month-1, year) + "T23:59:59";
				}else if(month >= 6 && i != 12){
					startDate = year-1 + "-" + month + "-01T00:00:00";
					endDate   = year-1 + "-" + month + "-" + getDaysInMonth(month-1, year-1) + "T23:59:59";
				}else{
					startDate = year + "-" + month + "-01T00:00:00";
					endDate   = year + "-" + month + "-" + getDaysInMonth(month-1, year) + "T23:59:59";
				}
				month++;
				QueryBuilder qb = QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("userType", "STUDENT"))
						.mustNot(QueryBuilders.matchQuery("institution.id", "ICPNA"))
						.mustNot(QueryBuilders.matchQuery("studentType", "capes_model"))
						.must(QueryBuilders.rangeQuery("dateCreated").from(startDate).to(endDate));
				long count = elasticSearchClient.prepareCount(Constants.USERS_ALL_ALIAS)
					.setQuery(qb)
					.execute()
					.actionGet().getCount();
					 dataArray[i] = count;
					 if(i != 0){
						 last12Month = last12Month + count;
					 }
					 
			}
			dataArray[13] = last12Month;
			dataMap = convertReportDataArrayIntoMap(dataArray);
			return dataMap;
		}
		
		public Map<String,Long> getNewRegCapesReportData(int year){
			int month =6;
			Map<String,Long>  dataMap = new LinkedHashMap<String,Long>();
			if(year > 0){
				year = year+2000;
			}else{
				year = year + 1900;
			}
			long last12Month = 0;
			String startDate,endDate;
			long[] dataArray = new long[14];
			for(int i  = 0; i < dataArray.length; i++){
				if(month > 12){
					month = 1;
					startDate = year + "-" + month + "-01T00:00:00";
					endDate   = year + "-" + month + "-"+ getDaysInMonth(month-1, year) + "T23:59:59";
				}else if(month >= 6 && i != 12){
					startDate = year-1 + "-" + month + "-01T00:00:00";
					endDate   = year-1 + "-" + month + "-" + getDaysInMonth(month-1, year-1) + "T23:59:59";
				}else{
					startDate = year + "-" + month + "-01T00:00:00";
					endDate   = year + "-" + month + "-" + getDaysInMonth(month-1, year) + "T23:59:59";
				}
				month++;
				QueryBuilder qb = QueryBuilders.boolQuery()
					.must(QueryBuilders.matchQuery("userType", "STUDENT"))
					.must(QueryBuilders.matchQuery("milestones.PT.level", 0))
					.must(QueryBuilders.matchQuery("studentType", "capes_model"))
					.must(QueryBuilders.rangeQuery("milestones.PT.startedDate").from(startDate).to(endDate));
				long count = elasticSearchClient.prepareCount(Constants.USERS_ALL_ALIAS)
					.setQuery(qb)
					.execute()
					.actionGet().getCount();
					 dataArray[i] = count;
					 if(i != 0){
						 last12Month = last12Month + count;
					 }
					 
			}
			dataArray[13] = last12Month;
			dataMap = convertReportDataArrayIntoMap(dataArray);
			return dataMap;
		}
		
		private Map<String,Long> convertReportDataArrayIntoMap(long[] dataArray){
			Map<String,Long> dataMap = new LinkedHashMap<String,Long>();
			dataMap.put("Jun", dataArray[0]);
			dataMap.put("Jul", dataArray[1]);
			dataMap.put("Aug", dataArray[2]);
			dataMap.put("Sep", dataArray[3]);
			dataMap.put("Oct", dataArray[4]);
			dataMap.put("Nov", dataArray[5]);
			dataMap.put("Dec", dataArray[6]);
			dataMap.put("Jan", dataArray[7]);
			dataMap.put("Feb", dataArray[8]);
			dataMap.put("Mar", dataArray[9]);
			dataMap.put("Apr", dataArray[10]);
			dataMap.put("May", dataArray[11]);
			dataMap.put("Jun2", dataArray[12]);
			dataMap.put("Last12Month", dataArray[13]);
			return dataMap;
		}
		private int getDaysInMonth(int month, int year) {
		      Calendar cal = Calendar.getInstance();
		      cal.set(Calendar.MONTH, month);
		      cal.set(Calendar.DAY_OF_MONTH, 1);
		      cal.set(Calendar.YEAR, year);
		      Date startDate = cal.getTime();
		      int nextMonth = (month == Calendar.DECEMBER) ? Calendar.JANUARY : month + 1;
		      cal.set(Calendar.MONTH, nextMonth);
		      if (month == Calendar.DECEMBER) {
		         cal.set(Calendar.YEAR, year + 1);
		      }
		      Date endDate = cal.getTime();
		      return (int)((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000));
		   }
}


