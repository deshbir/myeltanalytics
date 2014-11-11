package myeltanalytics.service.usageReport;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

	 public Map<String,Long> getNewUsersRegCapesReportData(int year,int month){
		Map<String,Long>  dataMap = new LinkedHashMap<String,Long>();
		Calendar date = new GregorianCalendar();
		date.set(Calendar.DATE, 1);
		date.set(Calendar.MONTH, month-1);
		date.set(Calendar.YEAR,year);
		long last12Month = 0;
		String startDate,endDate;
		for(int i  = 0; i < 13 ; i++){
			startDate = date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-01T00:00:00";
			endDate   = date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-"+ date.getActualMaximum(Calendar.DAY_OF_MONTH) + "T23:59:59";
			QueryBuilder query = QueryBuilders.boolQuery()
					.must(QueryBuilders.matchQuery("userType", "STUDENT"))
					.must(QueryBuilders.matchQuery("milestones.PT.level", 0))
					.must(QueryBuilders.matchQuery("userModel", "capes_model"))
					.must(QueryBuilders.rangeQuery("milestones.PT.startedDate").from(startDate).to(endDate));
			long count = elasticSearchClient.prepareCount(Constants.USERS_ALL_ALIAS)
					.setQuery(query)
					.execute()
					.actionGet()
					.getCount();
			if(i < 12 ){
				last12Month = last12Month + count;
			}
			String monthName=new DateFormatSymbols().getMonths()[date.get(Calendar.MONTH)];
			dataMap.put(monthName.substring(0, Math.min(monthName.length(), 3)) +" '"+ String.valueOf(date.get(Calendar.YEAR)).substring(2), count);
			date.add(Calendar.MONTH, -1);
		}
		dataMap.put("Last 12 Months", last12Month);
		return dataMap;
	}
	
	 public Map<String,Long> getNewUsersRegAllOtherReportData(int year, int month){
			Map<String,Long>  dataMap = new LinkedHashMap<String,Long>();
			Calendar date = new GregorianCalendar();
			date.set(Calendar.DATE, 1);
			date.set(Calendar.MONTH, month-1);
			date.set(Calendar.YEAR,year);
			long last12Month = 0;
			String startDate,endDate;
			for(int i  = 0; i < 13 ; i++){
				startDate = date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-01T00:00:00";
				endDate   = date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-"+ date.getActualMaximum(Calendar.DAY_OF_MONTH) + "T23:59:59";
				QueryBuilder query = QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("userType", "STUDENT"))
						.mustNot(QueryBuilders.matchQuery("institution.id", "ICPNA"))
						.mustNot(QueryBuilders.matchQuery("userModel", "capes_model"))
						.must(QueryBuilders.rangeQuery("dateCreated").from(startDate).to(endDate));
				long count = elasticSearchClient.prepareCount(Constants.USERS_ALL_ALIAS)
						.setQuery(query)
						.execute()
						.actionGet()
						.getCount();
				if(i < 12 ){
					last12Month = last12Month + count;
				}
				String monthName=new DateFormatSymbols().getMonths()[date.get(Calendar.MONTH)];
				dataMap.put(monthName.substring(0, Math.min(monthName.length(), 3)) +" '"+ String.valueOf(date.get(Calendar.YEAR)).substring(2), count);
				date.add(Calendar.MONTH, -1);
			}
			dataMap.put("Last 12 Months", last12Month);
			return dataMap;
		}
	 
	 public Map<String,Long> getNewProdRegCapesReportData(int year, int month){
			Map<String,Long>  dataMap = new LinkedHashMap<String,Long>();
			Calendar date = new GregorianCalendar();
			date.set(Calendar.DATE, 1);
			date.set(Calendar.MONTH, month-1);
			date.set(Calendar.YEAR,year);
			long last12Month = 0;
			String startDate,endDate;
			for(int i  = 0; i < 13 ; i++){
				startDate = date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-01T00:00:00";
				endDate   = date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-"+ date.getActualMaximum(Calendar.DAY_OF_MONTH) + "T23:59:59";
				QueryBuilder query = QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("userType", "STUDENT"))
						.must(QueryBuilders.matchQuery("userModel", "capes_model"))
						.must(QueryBuilders.rangeQuery("dateCreated").from(startDate).to(endDate));
				long count = elasticSearchClient.prepareCount(Constants.USERS_ALL_ALIAS)
						.setQuery(query)
						.execute()
						.actionGet()
						.getCount();
				if(i < 12 ){
					last12Month = last12Month + count;
				}
				String monthName=new DateFormatSymbols().getMonths()[date.get(Calendar.MONTH)];
				dataMap.put(monthName.substring(0, Math.min(monthName.length(), 3)) +" '"+ String.valueOf(date.get(Calendar.YEAR)).substring(2), count);
				date.add(Calendar.MONTH, -1);
			}
			dataMap.put("Last 12 Months", last12Month);
			return dataMap;
		}
	 
	 public Map<String,Long> getNewProdRegAllOtherReportData(int year, int month){
			Map<String,Long>  dataMap = new LinkedHashMap<String,Long>();
			Calendar date = new GregorianCalendar();
			date.set(Calendar.DATE, 1);
			date.set(Calendar.MONTH, month-1);
			date.set(Calendar.YEAR,year);
			long last12Month = 0;
			String startDate,endDate;
			for(int i  = 0; i < 13 ; i++){
				startDate = date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-01T00:00:00";
				endDate   = date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-"+ date.getActualMaximum(Calendar.DAY_OF_MONTH) + "T23:59:59";
				QueryBuilder query = QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("userType", "STUDENT"))
						.mustNot(QueryBuilders.matchQuery("institution.id", "ICPNA"))
						.mustNot(QueryBuilders.matchQuery("userModel", "capes_model"))
						.should(QueryBuilders.matchQuery("access.accessType", Constants.ACCESSTYPE_ACCESSCODE))
						.should(QueryBuilders.matchQuery("access.accessType", Constants.ACCESSTYPE_ACCESSRIGHT))
						.minimumNumberShouldMatch(1)
						.must(QueryBuilders.rangeQuery("dateCreated").from(startDate).to(endDate));
				long count = elasticSearchClient.prepareCount(Constants.USERS_INDEX)
						.setQuery(query)
						.execute()
						.actionGet()
						.getCount();
					query = QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("userType", "STUDENT"))
						.mustNot(QueryBuilders.matchQuery("institution.id", "ICPNA"))
						.mustNot(QueryBuilders.matchQuery("userModel", "capes_model"))
						.must(QueryBuilders.matchQuery("access.accessType", Constants.ACCESSTYPE_INSTITUTION))
						.must(QueryBuilders.rangeQuery("dateCreated").from(startDate).to(endDate));
					count = count +  elasticSearchClient.prepareCount(Constants.USERS_INDEX)
							.setQuery(query)
							.execute()
							.actionGet()
							.getCount();

				if(i < 12 ){
					last12Month = last12Month + count;
				}
				String monthName=new DateFormatSymbols().getMonths()[date.get(Calendar.MONTH)];
				dataMap.put(monthName.substring(0, Math.min(monthName.length(), 3)) +" '"+ String.valueOf(date.get(Calendar.YEAR)).substring(2), count);
				date.add(Calendar.MONTH, -1);
			}
			dataMap.put("Last 12 Months", last12Month);
			return dataMap;
		}
	 public Map<String,Long> getactiveUsersCapesReportData(int year, int month){
			Map<String,Long>  dataMap = new LinkedHashMap<String,Long>();
			Calendar date = new GregorianCalendar();
			date.set(Calendar.DATE, 1);
			date.set(Calendar.MONTH, month-1);
			date.set(Calendar.YEAR,year);
			String startDate = date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-01T00:00:00";
			String endDate   = date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-"+ date.getActualMaximum(Calendar.DAY_OF_MONTH) + "T23:59:59";
			QueryBuilder query = QueryBuilders.boolQuery()
					.must(QueryBuilders.matchQuery("userType", "STUDENT"))
					.must(QueryBuilders.matchQuery("userModel", "capes_model"))
					.must(QueryBuilders.rangeQuery("dateLastLogin").from(startDate).to(endDate));
			long count = elasticSearchClient.prepareCount(Constants.USERS_ALL_ALIAS)
					.setQuery(query)
					.execute()
					.actionGet()
					.getCount();
			String monthName=new DateFormatSymbols().getMonths()[date.get(Calendar.MONTH)];
			dataMap.put(monthName.substring(0, Math.min(monthName.length(), 3)) +" '"+ String.valueOf(date.get(Calendar.YEAR)).substring(2), count);
			return dataMap;
	 }
	 
	 public Map<String,Long> getactiveUsersAllOtherReportData(int year, int month){
		Map<String,Long>  dataMap = new LinkedHashMap<String,Long>();
		Calendar date = new GregorianCalendar();
		date.set(Calendar.DATE, 1);
		date.set(Calendar.MONTH, month-1);
		date.set(Calendar.YEAR,year);
		String startDate = date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-01T00:00:00";
		String endDate   = date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-"+ date.getActualMaximum(Calendar.DAY_OF_MONTH) + "T23:59:59";
		QueryBuilder query = QueryBuilders.boolQuery()
				.must(QueryBuilders.matchQuery("userType", "STUDENT"))
				.mustNot(QueryBuilders.matchQuery("institution.id", "ICPNA"))
				.mustNot(QueryBuilders.matchQuery("userModel", "capes_model"))
				.must(QueryBuilders.rangeQuery("dateLastLogin").from(startDate).to(endDate));
		long count = elasticSearchClient.prepareCount(Constants.USERS_ALL_ALIAS)
				.setQuery(query)
				.execute()
				.actionGet()
				.getCount();
		String monthName=new DateFormatSymbols().getMonths()[date.get(Calendar.MONTH)];
		dataMap.put(monthName.substring(0, Math.min(monthName.length(), 3)) +" '"+ String.valueOf(date.get(Calendar.YEAR)).substring(2), count);
		return dataMap;
	 }
	 	
}