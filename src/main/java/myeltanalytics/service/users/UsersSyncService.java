package myeltanalytics.service.users;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import myeltanalytics.model.Constants;
import myeltanalytics.model.JobInfo;
import myeltanalytics.service.HelperService;

import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service(value="usersSyncService")
public class UsersSyncService
{
    @Autowired
    private Client elasticSearchClient;
    
    @Autowired
    private HelperService helperService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Value("${users.threadpoolsize}")
    private int userSyncThreadPoolSize;
    
    @Value("${spring.datasource.username}")
    private String dbUserName;
    
    @Value("${spring.datasource.password}")
    private String dbPassword;
    
    @Value("${spring.datasource.driverClassName}")
    private String dbDriverClassName;
    
    @Value("${spring.datasource.initialSize}")
    private int dbInitialSize;
    
    @Value("${spring.datasource.maxActive}")
    private int dbMaxActive;
    
    @Value("${spring.datasource.maxIdle}")
    private int dbMaxIdle;
    
    @Value("${spring.datasource.url}")
    private String mainDatabaseURLFull;   
    
    public static String mainDatabaseURL;
    
    private final Logger LOGGER = Logger.getLogger(UsersSyncService.class);
    
    private ExecutorService userSyncExecutor = null;
    
    public static JobInfo jobInfo = new JobInfo();
    
    private static long recordsProcessed = 0l;
    
    private static long failedRecordProcessed = 01;
    
    SearchHit[] failedUsersSearchHits = null;
    
    private static Map<String, JdbcTemplate> jdbcTemplateMap = new HashMap<String,JdbcTemplate>();
    
    public JdbcTemplate getJdbcTemplate(String databaseURL) {
        JdbcTemplate myJdbcTemplate = null;
        if (databaseURL.equals(".") || databaseURL.equals(mainDatabaseURL)) {
            myJdbcTemplate = jdbcTemplate;
        } else {
            myJdbcTemplate = jdbcTemplateMap.get(databaseURL);
            if (myJdbcTemplate == null) {
                myJdbcTemplate = buildJdbcTemplate(databaseURL);
                jdbcTemplateMap.put(databaseURL, myJdbcTemplate);
            }
        }
        return myJdbcTemplate;
    }
    
    public void startFreshSync() throws JsonProcessingException {
        String newJobId = UUID.randomUUID().toString();  
        LOGGER.info("Starting a fresh UsersSyncJob with syncJobId=" + newJobId);
        LOGGER.info("Updating lastJobInfo for UsersSyncJob with syncJobId=" + newJobId);
        updateLastJobInfoInES(newJobId);
        jobInfo.setJobId(newJobId);
        jobInfo.setLastIdentifier("");
        jobInfo.setSuccessRecords(0);
        jobInfo.setErrorRecords(0);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        jobInfo.setStartDateTime(dateFormat.format(date));
        jobInfo.setJobStatus(Constants.STATUS_INPROGRESS);    
        LOGGER.info("Updating userStatus for UsersSyncJob with syncJobId=" + newJobId);
        updateUserStatus();
        jobInfo.setTotalRecords(getTotalUsersCount());
        jobInfo.setFailedsUserStatus(Constants.STATUS_NOT_STARTED);
        startSyncJob();
    }
    
    public void stopSync() throws InterruptedException, JsonProcessingException {
        LOGGER.info("Starting to abort UsersSyncJob with syncJobId=" + jobInfo.getJobId());
        jobInfo.setJobStatus(Constants.STATUS_PAUSED);
        LOGGER.info("Updating userStatus for UsersSyncJob with syncJobId=" + jobInfo.getJobId());
        updateUserStatus();        
        userSyncExecutor.shutdown();
        userSyncExecutor.awaitTermination(2, TimeUnit.MINUTES);   
        LOGGER.info("Aborted UsersSyncJob with syncJobId=" + jobInfo.getJobId());
    }
    
    public void resumeSync() throws JsonProcessingException {
    	LOGGER.info("Resuming UsersSyncJob with syncJobId=" + jobInfo.getJobId());
    	jobInfo.setJobStatus(Constants.STATUS_INPROGRESS);
    	jobInfo.setFailedsUserStatus(Constants.STATUS_NOT_STARTED);
    	LOGGER.info("Updating userStatus for UsersSyncJob with syncJobId=" + jobInfo.getJobId());
    	updateUserStatus();
    	startSyncJob();
    }
    
    public void startFailedUsersSync() throws JsonProcessingException {
    	LOGGER.info("Starting Failed UsersSyncJob with syncJobId=" + jobInfo.getJobId());
    	jobInfo.setFailedsUserStatus(Constants.STATUS_INPROGRESS);
    	jobInfo.setFailedUserProcessed(0);
    	jobInfo.setTotalFailedUser(elasticSearchClient.prepareCount(Constants.USERS_ERROR_ALIAS).execute().actionGet().getCount());
    	getFailedUsers();
    	LOGGER.info("Updating userStatus for UsersSyncJob with syncJobId=" + jobInfo.getJobId());
    	updateUserStatus();
    	startFailedRecordsSync();
    }
    
    private void startSyncJob() {
        
        recordsProcessed = 0;
        userSyncExecutor = Executors.newFixedThreadPool(userSyncThreadPoolSize);
        
        String query = null;
        if (jobInfo.getLastIdentifier().equals("")) {
            query = "(SELECT Name as LoginName,InstitutionID FROM Users where type=0 and InstitutionID NOT IN " + HelperService.ignoreInstitutionsQuery 
                + ") UNION (SELECT LoginName,InstitutionID FROM UserInstitutionMap where InstitutionID NOT IN " + HelperService.ignoreInstitutionsQuery  + ")"
                + " order by LoginName limit " + Constants.SQL_RECORDS_LIMIT;
        } else {
            query = "SELECT LoginName,InstitutionID from ((SELECT Name as LoginName,InstitutionID FROM Users where type=0 and InstitutionID NOT IN " + HelperService.ignoreInstitutionsQuery 
                + ") UNION (SELECT LoginName,InstitutionID FROM UserInstitutionMap where InstitutionID NOT IN " + HelperService.ignoreInstitutionsQuery  + "))"
                + " as allusers where LoginName > \"" + jobInfo.getLastIdentifier() + "\" order by LoginName limit " + Constants.SQL_RECORDS_LIMIT;
        }
        
        
        System.out.println(query);
        
        LOGGER.info("Starting sync process for UsersSyncJob with syncJobId=" + jobInfo.getJobId() + ", query=" + query);
        
        /**
         * UserInstitutionMap handling
         * 1. Change query to use loginName instead of id  --- and change table to UserInstitutionMap
         * 2. Additinally select instId and pass it as a argument to Thread.
         */
        jdbcTemplate.query(query,
            new RowCallbackHandler()
            {
        	@Override
        	public void processRow(ResultSet rs) throws SQLException
        	{

            	try {
                    String loginName = rs.getString("LoginName");
                    String institutionID = rs.getString("InstitutionID");
                    Runnable worker = new UsersSyncThread(loginName, institutionID,false);
                    userSyncExecutor.execute(worker);    
                } catch (Exception e) {
                    LOGGER.error("Error while processing User row" ,e);
                }
        	}
            });
    }
    
    public synchronized void updateLastSyncedUserStatus() throws JsonProcessingException{
        
//        boolean isCompleted = false;
        if(!(jobInfo.getFailedsUserStatus().equals(Constants.STATUS_INPROGRESS))){
	        if (jobInfo.getErrorRecords() + jobInfo.getSuccessRecords() == jobInfo.getTotalRecords()) {
//	            isCompleted = true;
	            jobInfo.setJobStatus(Constants.STATUS_COMPLETED);
	        }
	        updateUserStatus();
	        recordsProcessed++;
        
//	        if (isCompleted) {
//	           //delete the records that have not been update/synced; they are records that have been deleted in database
//	            //helperService.deleteUnsyncedRecords(elasticSearchClient, Constants.USERS_INDEX, Constants.USERS_TYPE, jobInfo.getJobId());
//	        } else {
            if (recordsProcessed == Constants.SQL_RECORDS_LIMIT) {
                startSyncJob();
            }
//	        }
        } else {
        	if(jobInfo.getFailedUserProcessed() == jobInfo.getTotalFailedUsersToProcess()){
        		//helperService.deleteUnsyncedRecords(elasticSearchClient, Constants.USERS_INDEX, Constants.USERS_TYPE, jobInfo.getJobId());
        		jobInfo.setFailedsUserStatus(Constants.STATUS_COMPLETED);
        		if(!(jobInfo.getJobStatus().equals(Constants.STATUS_COMPLETED))){
        			jobInfo.setJobStatus(Constants.STATUS_PAUSED);
        		}
        	}
        	updateUserStatus();
        	failedRecordProcessed++;
        	if(failedRecordProcessed == 10000){
        		getFailedUsers();
        		startFailedRecordsSync();
        	}
        }
    }
    
    public synchronized void updateUserStatus() throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jobInfo);
        elasticSearchClient.prepareIndex(Constants.MYELT_ANALYTICS_INDEX, Constants.USERS_JOB_STATUS, String.valueOf(jobInfo.getJobId())).setSource(json).execute().actionGet();
    }
    
    
    public void setup() throws IOException {
        createUsersIndex();
        refreshJobStatusFromES();
        mainDatabaseURL = mainDatabaseURLFull.substring(0, mainDatabaseURLFull.indexOf("?"));
    }
    public void createUsersIndex() throws IOException {
        
        //Create Users Index
        if (!helperService.isIndexExist(Constants.USERS_INDEX, elasticSearchClient)) {            
            elasticSearchClient.admin().indices().create(new CreateIndexRequest(Constants.USERS_INDEX)
                    .mapping(Constants.USERS_TYPE, buildUserTypeMappings())).actionGet();   
        }
        
        //Create required aliases for Users reports
        if (!helperService.isIndexExist(Constants.USERS_ERROR_ALIAS, elasticSearchClient)) {
            TermsFilterBuilder usersOnlyFilter = FilterBuilders.termsFilter("recordType", Constants.USER_ERROR);
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.USERS_INDEX, Constants.USERS_ERROR_ALIAS, usersOnlyFilter).execute().actionGet();
        }     
        
        if (!helperService.isIndexExist(Constants.USERS_ALL_ALIAS, elasticSearchClient)) {
            TermsFilterBuilder usersOnlyFilter = FilterBuilders.termsFilter("recordType", Constants.USER_WITH_ACCESSCODE, Constants.USER_WITHOUT_ACCESSCODE);
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.USERS_INDEX, Constants.USERS_ALL_ALIAS, usersOnlyFilter).execute().actionGet();
        }        

        if (!helperService.isIndexExist(Constants.USERS_CAPES_ALIAS, elasticSearchClient)) {
            AndFilterBuilder capesModelUsersFilter = FilterBuilders.andFilter(FilterBuilders.termsFilter("recordType", Constants.USER_WITH_ACCESSCODE, Constants.USER_WITHOUT_ACCESSCODE), FilterBuilders.termsFilter("studentType", Constants.CAPES_MODEL));
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.USERS_INDEX, Constants.USERS_CAPES_ALIAS, capesModelUsersFilter).execute().actionGet();
        }
        
        if (!helperService.isIndexExist(Constants.USERS_ICPNA_ALIAS, elasticSearchClient)) {
            AndFilterBuilder ICPNAInstUsersFilter = FilterBuilders.andFilter(FilterBuilders.termsFilter("recordType", Constants.USER_WITH_ACCESSCODE, Constants.USER_WITHOUT_ACCESSCODE), FilterBuilders.termsFilter("institution.id", Constants.ICPNA_INSTITUTION));
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.USERS_INDEX, Constants.USERS_ICPNA_ALIAS, ICPNAInstUsersFilter).execute().actionGet();
        }
        
        if (!helperService.isIndexExist(Constants.USERS_SEVEN_ALIAS, elasticSearchClient)) {
            AndFilterBuilder SevenInstUsersFilter = FilterBuilders.andFilter(FilterBuilders.termsFilter("recordType", Constants.USER_WITH_ACCESSCODE, Constants.USER_WITHOUT_ACCESSCODE), FilterBuilders.termsFilter("institution.id", Constants.SEVEN_INSTITUTION));
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.USERS_INDEX, Constants.USERS_SEVEN_ALIAS, SevenInstUsersFilter).execute().actionGet();
        }
        
        if (!helperService.isIndexExist(Constants.USERS_FY14_ALIAS, elasticSearchClient)) {
            AndFilterBuilder registrationsFY14Filter = FilterBuilders.andFilter(FilterBuilders.termsFilter("recordType", Constants.USER_WITH_ACCESSCODE, Constants.USER_WITHOUT_ACCESSCODE), FilterBuilders.rangeFilter("dateCreated").from("2013-07-01T00:00:00").to("2014-06-30T23:59:59"));
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.USERS_INDEX, Constants.USERS_FY14_ALIAS, registrationsFY14Filter).execute().actionGet();
        }
        
        if (!helperService.isIndexExist(Constants.USERS_FY13_ALIAS, elasticSearchClient)) {
            AndFilterBuilder registrationsFY13Filter = FilterBuilders.andFilter(FilterBuilders.termsFilter("recordType", Constants.USER_WITH_ACCESSCODE, Constants.USER_WITHOUT_ACCESSCODE), FilterBuilders.rangeFilter("dateCreated").from("2012-07-01T00:00:00").to("2013-06-30T23:59:59"));
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.USERS_INDEX, Constants.USERS_FY13_ALIAS, registrationsFY13Filter).execute().actionGet();
        }
        
        if (!helperService.isIndexExist(Constants.USERS_FY12_ALIAS, elasticSearchClient)) {
            AndFilterBuilder registrationsFY12Filter = FilterBuilders.andFilter(FilterBuilders.termsFilter("recordType", Constants.USER_WITH_ACCESSCODE, Constants.USER_WITHOUT_ACCESSCODE), FilterBuilders.rangeFilter("dateCreated").from("2011-07-01T00:00:00").to("2012-06-30T23:59:59"));
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.USERS_INDEX, Constants.USERS_FY12_ALIAS, registrationsFY12Filter).execute().actionGet();
        }
        
        if (!helperService.isIndexExist(Constants.USERS_FY11_ALIAS, elasticSearchClient)) {
            AndFilterBuilder registrationsFY11Filter = FilterBuilders.andFilter(FilterBuilders.termsFilter("recordType", Constants.USER_WITH_ACCESSCODE, Constants.USER_WITHOUT_ACCESSCODE), FilterBuilders.rangeFilter("dateCreated").from("2010-07-01T00:00:00").to("2011-06-30T23:59:59"));
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.USERS_INDEX, Constants.USERS_FY11_ALIAS, registrationsFY11Filter).execute().actionGet();
        }
        
        //Create required aliases for Access Codes reports
        if (!helperService.isIndexExist(Constants.ACCESS_CODES_ALL_ALIAS, elasticSearchClient)) {
            TermsFilterBuilder accessCodesOnlyFilter = FilterBuilders.termsFilter("recordType", Constants.USER_WITH_ACCESSCODE, Constants.ADDITIONAL_ACCESSCODE);
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.USERS_INDEX, Constants.ACCESS_CODES_ALL_ALIAS, accessCodesOnlyFilter).execute().actionGet();
        }
        
        if (!helperService.isIndexExist(Constants.ACCESS_CODES_FY14_ALIAS, elasticSearchClient)) {
            AndFilterBuilder accessCodesFY14Filter = FilterBuilders.andFilter(FilterBuilders.termsFilter("recordType", Constants.USER_WITH_ACCESSCODE, Constants.ADDITIONAL_ACCESSCODE), FilterBuilders.rangeFilter("dateCreated").from("2013-07-01T00:00:00").to("2014-06-30T23:59:59"));
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.USERS_INDEX, Constants.ACCESS_CODES_FY14_ALIAS, accessCodesFY14Filter).execute().actionGet();
        }
        
        if (!helperService.isIndexExist(Constants.ACCESS_CODES_FY13_ALIAS, elasticSearchClient)) {
            AndFilterBuilder accessCodesFY13Filter = FilterBuilders.andFilter(FilterBuilders.termsFilter("recordType", Constants.USER_WITH_ACCESSCODE, Constants.ADDITIONAL_ACCESSCODE), FilterBuilders.rangeFilter("dateCreated").from("2012-07-01T00:00:00").to("2013-06-30T23:59:59"));
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.USERS_INDEX, Constants.ACCESS_CODES_FY13_ALIAS, accessCodesFY13Filter).execute().actionGet();
        }
        
        if (!helperService.isIndexExist(Constants.ACCESS_CODES_FY12_ALIAS, elasticSearchClient)) {
            AndFilterBuilder accessCodesFY12Filter = FilterBuilders.andFilter(FilterBuilders.termsFilter("recordType", Constants.USER_WITH_ACCESSCODE, Constants.ADDITIONAL_ACCESSCODE), FilterBuilders.rangeFilter("dateCreated").from("2011-07-01T00:00:00").to("2012-06-30T23:59:59"));
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.USERS_INDEX, Constants.ACCESS_CODES_FY12_ALIAS, accessCodesFY12Filter).execute().actionGet();
        }
        
        if (!helperService.isIndexExist(Constants.ACCESS_CODES_FY11_ALIAS, elasticSearchClient)) {
            AndFilterBuilder accessCodesFY11Filter = FilterBuilders.andFilter(FilterBuilders.termsFilter("recordType", Constants.USER_WITH_ACCESSCODE, Constants.ADDITIONAL_ACCESSCODE), FilterBuilders.rangeFilter("dateCreated").from("2010-07-01T00:00:00").to("2011-06-30T23:59:59"));
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.USERS_INDEX, Constants.ACCESS_CODES_FY11_ALIAS, accessCodesFY11Filter).execute().actionGet();
        }
    }
    
    public long getTotalUsersCount() throws JsonProcessingException {
        
        String sql = "SELECT Count(*) from ((SELECT Name as LoginName,InstitutionID FROM Users where type=0 and InstitutionID NOT IN " + HelperService.ignoreInstitutionsQuery
            + ") UNION (SELECT LoginName,InstitutionID FROM UserInstitutionMap where InstitutionID NOT IN " + HelperService.ignoreInstitutionsQuery + ")) as allusers";
        
        System.out.println(sql);
        
        long usersCount = jdbcTemplate.queryForObject(sql, Long.class);
        LOGGER.info("Total users to sync= " + usersCount + " for syncJobId= " + UsersSyncService.jobInfo.getJobId());
        return usersCount;
    }
    
    
    public void updateLastJobInfoInES(String jobId) throws JsonProcessingException {       
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put(Constants.ID, jobId);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonMap);
        elasticSearchClient.prepareIndex(Constants.MYELT_ANALYTICS_INDEX, Constants.USERS_JOB_STATUS, Constants.LAST_JOB_ID).setSource(json).execute().actionGet();
    }
    
    public void refreshJobStatusFromES() throws JsonProcessingException {
        if (helperService.isIndexExist(Constants.MYELT_ANALYTICS_INDEX, elasticSearchClient) && helperService.isTypeExist(Constants.MYELT_ANALYTICS_INDEX, Constants.USERS_JOB_STATUS, elasticSearchClient)) {
            GetResponse lastJobInfoResponse = elasticSearchClient.prepareGet(Constants.MYELT_ANALYTICS_INDEX, Constants.USERS_JOB_STATUS, Constants.LAST_JOB_ID).execute().actionGet();
            Map<String,Object> lastJobInfoMap = lastJobInfoResponse.getSourceAsMap();
            String lastJobId = (String) lastJobInfoMap.get(Constants.ID);
            if(lastJobId != null){
                jobInfo.setJobId(lastJobId);
                GetResponse lastJobResponse = elasticSearchClient.prepareGet(Constants.MYELT_ANALYTICS_INDEX, Constants.USERS_JOB_STATUS, String.valueOf(lastJobId)).execute().actionGet();
                Map<String,Object> map  = lastJobResponse.getSourceAsMap();
                if (map != null) {
                    jobInfo.setLastIdentifier((String) map.get(Constants.LAST_IDENTIFIER));
                    jobInfo.setSuccessRecords((Integer) map.get(Constants.SUCCESSFULL_RECORDS));
                    jobInfo.setErrorRecords((Integer) map.get(Constants.ERROR_RECORDS));
                    jobInfo.setTotalRecords((Integer) map.get(Constants.TOTAL_RECORDS));
                    jobInfo.setStartDateTime((String) map.get(Constants.START_DATETIME));
                    String jobStatus = (String) map.get(Constants.JOB_STATUS);
                    //If server shut-down while job is running, status is still "InProgress" in Database, but the job is actually terminated/paused
                    if (jobStatus.equals(Constants.STATUS_INPROGRESS)) {
                        jobInfo.setJobStatus(Constants.STATUS_PAUSED);
                        updateUserStatus();
                    } else {
                        jobInfo.setJobStatus((String) map.get(Constants.JOB_STATUS));
                    }
                }
            }
        }
    }
    
    public XContentBuilder buildUserTypeMappings(){
        XContentBuilder builder = null; 
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject()
            .startObject("properties")
                .startObject("dateCreated")
                    .field("type", "date")                      
                .endObject()
                .startObject("dateLastLogin")
                    .field("type", "date")                      
                .endObject()
                .startObject("accessCode")
                    .startObject("properties")
                        .startObject("discipline")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()
                         .startObject("productName")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()
                         .startObject("dateCreated")
                            .field("type", "date")                      
                        .endObject()
                     .endObject()
                .endObject()
                .startObject("country")
                    .startObject("properties")
                        .startObject("name")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()
                     .endObject()
                .endObject()
                .startObject("userCountry")
                    .startObject("properties")
                        .startObject("name")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()
                     .endObject()
                .endObject()
                .startObject("region")
                    .field("type", "string")                      
                    .field("index", "not_analyzed")
                 .endObject()
                .startObject("courses")
                    .field("type", "string")                      
                    .field("index", "not_analyzed")
                 .endObject()
                 .startObject("productNames")
                    .field("type", "string")                      
                    .field("index", "not_analyzed")
                 .endObject()
                .startObject("disciplines")
                    .field("type", "string")                      
                    .field("index", "not_analyzed")
                 .endObject()
                 .startObject("institution")
                     .startObject("properties")
                         .startObject("name")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()
                         .startObject("id")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()
                         .startObject("country")
                            .startObject("properties")
                                .startObject("name")
                                     .field("type", "string")                      
                                     .field("index", "not_analyzed")
                                 .endObject()
                             .endObject()
                        .endObject()
                     .endObject()      
                 .endObject()
           .endObject()
           .endObject();           
        } catch (Exception e) {
            LOGGER.error("An error occured while building mapping for user_info" , e);
        }
        return builder;
    }
    
    private JdbcTemplate buildJdbcTemplate(String databaseURL) {
        
        if (databaseURL.indexOf("?") == -1) {
            databaseURL = databaseURL + "?zeroDateTimeBehavior=convertToNull";
        } else {
            databaseURL = databaseURL + "&zeroDateTimeBehavior=convertToNull";
        }
        
        DataSource dataSource = new DataSource();
        
        dataSource.setDriverClassName(dbDriverClassName);
        dataSource.setUrl(databaseURL);
        dataSource.setUsername(dbUserName);
        dataSource.setPassword(dbPassword);
        dataSource.setInitialSize(dbInitialSize);
        dataSource.setMaxIdle(dbMaxIdle);
        dataSource.setMaxActive(dbMaxActive);
        
        JdbcTemplate template = new JdbcTemplate();
        template.setDataSource(dataSource);
        
        return template;

  }
    
//  private void deletePreviousJobData()
//  {
//      //delete the user_type
//      elasticSearchClient.prepareDeleteByQuery(Constants.USERS_INDEX)
//          .setQuery(QueryBuilders.termQuery("_type", Constants.USERS_TYPE))
//          .execute()
//          .actionGet();
//      
//  }
    
    
    /*
     * Get all Records from 'users_error' index.
     */
    void getFailedUsers() {
//				failedUsersSearchHits = new SearchHit[totalHits];
//				elasticSearchClient.prepareSearch("users_error").addField("Source").execute().actionGet();
				int failedUserArraySize = 0;
				if( (jobInfo.getTotalFailedUsersToProcess() - jobInfo.getFailedUserProcessed()) > 10000){
    				failedUserArraySize = 10000;
				}else{
					failedUserArraySize = (int)(jobInfo.getTotalFailedUsersToProcess() - jobInfo.getFailedUserProcessed());
				}
    			failedUsersSearchHits = elasticSearchClient.prepareSearch(Constants.USERS_ERROR_ALIAS).setSize(failedUserArraySize).execute().actionGet().getHits().getHits();
//				int i = 0;
//				SearchResponse scrollResp = elasticSearchClient.prepareSearch(Constants.USERS_ERROR_ALIAS)
//				.setSearchType(SearchType.SCAN)
//				.setScroll(new TimeValue(60000))
//				.setSize(100).execute().actionGet();
//				    	
//				while (true) {
//				scrollResp = elasticSearchClient.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(600000)).execute().actionGet();
//				for (SearchHit hit : scrollResp.getHits().hits()) {
//			    	failedUsersSearchHits[i] = hit;
//				i++;
//				}
//				if (scrollResp.getHits().getHits().length == 0) {
//				break;
//				}
//}

    }
    
    /*
     *  Sync User from 'users_error' index.
     */
	private void startFailedRecordsSync()throws JsonProcessingException{
		failedRecordProcessed = 0; 
		userSyncExecutor = Executors.newFixedThreadPool(userSyncThreadPoolSize);
		for (SearchHit searchHit : failedUsersSearchHits){
			String loginName = (String)searchHit.getSource().get("userName");
			 Map<String, String> institutionMap = (HashMap<String,String>)(searchHit.getSource().get("institution"));
			 String institutionID = institutionMap.get("id");
			 Runnable worker = new UsersSyncThread(loginName, institutionID,true);
			 userSyncExecutor.execute(worker);
		}
	}
}
