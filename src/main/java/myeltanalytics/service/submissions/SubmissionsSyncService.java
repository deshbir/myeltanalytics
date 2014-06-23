package myeltanalytics.service.submissions;

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
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service(value="submissionsSyncService")
public class SubmissionsSyncService
{
    @Autowired
    private Client elasticSearchClient;
    
    @Autowired
    private HelperService helperService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Value("${submissions.threadpoolsize}")
    private int submissionsSyncThreadPoolSize;
    
    private final Logger LOGGER = Logger.getLogger(SubmissionsSyncService.class);
    
    private ExecutorService submissionsSyncExecutor = null;
    
    public static JobInfo jobInfo = new JobInfo();
    
    private static long recordsProcessed = 0l;
    
    public void startFreshSync() throws JsonProcessingException {
        String newJobId = UUID.randomUUID().toString();  
        
        LOGGER.info("Starting a fresh SubmissionsSyncJob with syncJobId=" + newJobId);
        
        LOGGER.info("Updating lastJobInfo for SubmissionsSyncJob with syncJobId=" + newJobId);
        updateLastJobInfoInES(newJobId);
        
        jobInfo.setJobId(newJobId);
        jobInfo.setLastIdentifier("");
        jobInfo.setSuccessRecords(0);
        jobInfo.setErrorRecords(0);
        jobInfo.setTotalRecords(getTotalSubmissionsCount());
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        jobInfo.setStartDateTime(dateFormat.format(date));
        
        jobInfo.setJobStatus(Constants.STATUS_INPROGRESS);      
        
        LOGGER.info("Updating submissionStatus for SubmissionsSyncJob with syncJobId=" + newJobId);
        updateSubmissionStatus();
        
        startSyncJob();
    }
    
    public void stopSync() throws InterruptedException, JsonProcessingException {
        LOGGER.info("Starting to abort SubmissionsSyncJob with syncJobId=" + jobInfo.getJobId());
        
        jobInfo.setJobStatus(Constants.STATUS_PAUSED);
        LOGGER.info("Updating submissionStatus for SubmissionsSyncJob with syncJobId=" + jobInfo.getJobId());
        updateSubmissionStatus();
        
        submissionsSyncExecutor.shutdown();
        submissionsSyncExecutor.awaitTermination(2, TimeUnit.MINUTES); 
        LOGGER.info("Aborted SubmissionsSyncJob with syncJobId=" + jobInfo.getJobId());
    }
    
    public void resumeSync() throws JsonProcessingException {
        LOGGER.info("Resuming SubmissionsSyncJob with syncJobId=" + jobInfo.getJobId());
        jobInfo.setJobStatus(Constants.STATUS_INPROGRESS);
        LOGGER.info("Updating submissionStatus for SubmissionsSyncJob with syncJobId=" + jobInfo.getJobId());
        updateSubmissionStatus();
       
        startSyncJob();
    }
    
    private void startSyncJob() {
        
        recordsProcessed = 0;
        submissionsSyncExecutor = Executors.newFixedThreadPool(submissionsSyncThreadPoolSize);
        
        String query = "select id from assignmentresults";
        if (jobInfo.getLastIdentifier().equals("")) {
            query = query + " order by id limit " + Constants.SQL_RECORDS_LIMIT;
        } else {
            query = query + " where id > " + jobInfo.getLastIdentifier() + " order by id limit " + Constants.SQL_RECORDS_LIMIT;
        }
        
        LOGGER.info("Starting sync process for SubmissionsSyncJob with syncJobId=" + jobInfo.getJobId() + ", query=" + query);
        
        jdbcTemplate.query(query,
            new RowCallbackHandler()
            {
                @Override
                public void processRow(ResultSet rs) throws SQLException
                {
                    try {
                        String currentId = rs.getString("id");
                        Runnable worker = new SubmissionsSyncThread(currentId);
                        submissionsSyncExecutor.execute(worker);    
                    } catch (Exception e) {
                        LOGGER.error("Error while processing Activity Submission row" ,e);
                    }
                    
                    
                }
            });
        
    }
    
    private long getTotalSubmissionsCount() throws JsonProcessingException {
        String sql = "select count(*) from assignmentresults";
        long submissionsCount = jdbcTemplate.queryForObject(sql, Long.class);
        LOGGER.info("Total submissions to sync= " + submissionsCount + " for syncJobId= " + SubmissionsSyncService.jobInfo.getJobId());
        return submissionsCount;
    }
    
    public synchronized void updateLastSyncedSubmissionStatus() throws JsonProcessingException {
        boolean isCompleted = false;
        if (jobInfo.getErrorRecords() + jobInfo.getSuccessRecords() == jobInfo.getTotalRecords()) {
            isCompleted = true;
            jobInfo.setJobStatus(Constants.STATUS_COMPLETED);
        }
       
        updateSubmissionStatus();        
        recordsProcessed++;
        
        if (isCompleted) {
            //delete the records that have not been update/synced; they are records that have been deleted in database
            helperService.deleteUnsyncedRecords(elasticSearchClient, Constants.SUBMISSIONS_INDEX, Constants.SUBMISSIONS_TYPE, jobInfo.getJobId());
         } else {
             if (recordsProcessed == Constants.SQL_RECORDS_LIMIT) {
                 startSyncJob();
             }
         }
    }
    
    public synchronized void updateSubmissionStatus() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jobInfo);
        elasticSearchClient.prepareIndex(Constants.MYELT_ANALYTICS_INDEX, Constants.SUBMISSIONS_JOB_STATUS, String.valueOf(jobInfo.getJobId())).setSource(json).execute().actionGet();
    }
   
    public void updateLastJobInfoInES(String jobId) throws JsonProcessingException {       
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put(Constants.ID, jobId);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonMap);
        elasticSearchClient.prepareIndex(Constants.MYELT_ANALYTICS_INDEX, Constants.SUBMISSIONS_JOB_STATUS, Constants.LAST_JOB_ID).setSource(json).execute().actionGet();
    }
    
    public void refreshJobStatusFromES() throws JsonProcessingException {
        if (helperService.isIndexExist(Constants.MYELT_ANALYTICS_INDEX, elasticSearchClient) && helperService.isTypeExist(Constants.MYELT_ANALYTICS_INDEX, Constants.SUBMISSIONS_JOB_STATUS, elasticSearchClient)) {
            GetResponse lastJobInfoResponse = elasticSearchClient.prepareGet(Constants.MYELT_ANALYTICS_INDEX, Constants.SUBMISSIONS_JOB_STATUS, Constants.LAST_JOB_ID).execute().actionGet();
            Map<String,Object> lastJobInfoMap = lastJobInfoResponse.getSourceAsMap();
            String lastJobId = (String) lastJobInfoMap.get(Constants.ID);
            if(lastJobId != null){
                jobInfo.setJobId(lastJobId);
                GetResponse lastJobResponse = elasticSearchClient.prepareGet(Constants.MYELT_ANALYTICS_INDEX, Constants.SUBMISSIONS_JOB_STATUS, String.valueOf(lastJobId)).execute().actionGet();
                Map<String,Object> map  = lastJobResponse.getSourceAsMap();
                if (map != null) {
                    jobInfo.setLastIdentifier((String) map.get(Constants.LAST_IDENTIFIER));
                    jobInfo.setSuccessRecords((Integer) map.get(Constants.SUCCESSFULL_RECORDS));
                    jobInfo.setErrorRecords((Integer) map.get(Constants.ERROR_RECORDS));
                    jobInfo.setTotalRecords((Integer) map.get(Constants.TOTAL_RECORDS));
                    String jobStatus = (String) map.get(Constants.JOB_STATUS);
                    jobInfo.setStartDateTime((String) map.get(Constants.START_DATETIME));
                    //If server shut-down while job is running, status is still "InProgress" in Database, but the job is actually terminated/paused
                    if (jobStatus.equals(Constants.STATUS_INPROGRESS)) {
                        jobInfo.setJobStatus(Constants.STATUS_PAUSED);
                        updateSubmissionStatus();
                    } else {
                        jobInfo.setJobStatus((String) map.get(Constants.JOB_STATUS));
                    }
                }
            }
        }
    }
    
    public void createSubmissionsIndex() throws IOException {
        //Create Submissions Index
        if (!helperService.isIndexExist(Constants.SUBMISSIONS_INDEX, elasticSearchClient)) {
            elasticSearchClient.admin().indices().create(new CreateIndexRequest(Constants.SUBMISSIONS_INDEX)
                    .mapping(Constants.SUBMISSIONS_TYPE, buildSumissionTypeMappings())).actionGet();    
        } 
        
        //Create required aliases for Submissions reports
        if (!helperService.isIndexExist(Constants.SUBMISSIONS_ALL_ALIAS, elasticSearchClient)) {
            TermsFilterBuilder submissionsAllFilter = FilterBuilders.termsFilter("status", "submitted");
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.SUBMISSIONS_INDEX, Constants.SUBMISSIONS_ALL_ALIAS, submissionsAllFilter).execute().actionGet();
        }
        
        if (!helperService.isIndexExist(Constants.SUBMISSIONS_ASSIGNMENTS_ALIAS, elasticSearchClient)) {
            AndFilterBuilder submissionsAssignmentsFilter = FilterBuilders.andFilter(FilterBuilders.termsFilter("status", "submitted"), FilterBuilders.termsFilter("activityType", "assignment"));
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.SUBMISSIONS_INDEX, Constants.SUBMISSIONS_ASSIGNMENTS_ALIAS, submissionsAssignmentsFilter).execute().actionGet();
        }
  
        if (!helperService.isIndexExist(Constants.SUBMISSIONS_EXAMVIEW_ALIAS, elasticSearchClient)) {
            AndFilterBuilder submissionsExamviewFilter = FilterBuilders.andFilter(FilterBuilders.termsFilter("status", "submitted"), FilterBuilders.termsFilter("activityType", "examview"));
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Constants.SUBMISSIONS_INDEX, Constants.SUBMISSIONS_EXAMVIEW_ALIAS, submissionsExamviewFilter).execute().actionGet();
        }
    }
    
    private XContentBuilder buildSumissionTypeMappings()
    {
        XContentBuilder builder = null; 
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject()
            .startObject("properties")
                .startObject("book")
                    .startObject("properties")
                        .startObject("discipline")
                            .field("type", "string")                      
                            .field("index", "not_analyzed")                        
                        .endObject()
                        .startObject("name")
                            .field("type", "string")                      
                            .field("index", "not_analyzed")                        
                        .endObject()
                    .endObject()
                .endObject()
                .startObject("institution")
                 .startObject("properties")
                     .startObject("name")
                         .field("type", "string")                      
                         .field("index", "not_analyzed")
                     .endObject()
                     .endObject()
                 .endObject()      
              .endObject()
           .endObject();           
        } catch (Exception e) {
            LOGGER.error("An error occured while building mapping for submission_info" , e);
        }
        return builder;
    }
    
    public void setup() throws IOException {
       createSubmissionsIndex();
       refreshJobStatusFromES();
    }   
    
}
