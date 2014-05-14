package myeltanalytics.service.submissions;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import myeltanalytics.model.JobInfo;
import myeltanalytics.service.Helper;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
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
    private JdbcTemplate jdbcTemplate;
    
    @Value("${submissions.threadpoolsize}")
    private int submissionsSyncThreadPoolSize;
    
    public static final String SUBMISSIONS_INDEX = "submissions";
    
    public static final String SUBMISSIONS_TYPE = "submissions_info";
    
    private final Logger LOGGER = Logger.getLogger(SubmissionsSyncService.class);
    
    private ExecutorService submissionsSyncExecutor = null;
    
    public JobInfo jobInfo = new JobInfo();
    
    private long recordsProcessed;
    
    public void startFreshSync() throws JsonProcessingException {
        String newJobId = UUID.randomUUID().toString();        
        updateLastJobInfoInES(newJobId);
        
        jobInfo.setJobId(newJobId);
        jobInfo.setLastId("");
        jobInfo.setSuccessRecords(0);
        jobInfo.setErrorRecords(0);
        jobInfo.setTotalRecords(getTotalSubmissionsCount());
        jobInfo.setJobStatus(Helper.STATUS_INPROGRESS);        
        updateLastSyncedSubmissionStatus();
        
        startSyncJob();
    }
    
    public void stopSync() throws InterruptedException, JsonProcessingException {
        jobInfo.setJobStatus(Helper.STATUS_PAUSED);
        updateLastSyncedSubmissionStatus();
        
        submissionsSyncExecutor.shutdown();
        submissionsSyncExecutor.awaitTermination(1, TimeUnit.MINUTES);    
    }
    
    public void resumeSync() throws JsonProcessingException {
        jobInfo.setJobStatus(Helper.STATUS_INPROGRESS);
        updateLastSyncedSubmissionStatus();
       
        startSyncJob();
    }
    
    private void startSyncJob() {
        
        submissionsSyncExecutor = Executors.newFixedThreadPool(submissionsSyncThreadPoolSize);
        
        jdbcTemplate.query(
            "select id from assignmentresults where id > ? order by id limit " + Helper.SQL_RECORDS_LIMIT, new Object[] { jobInfo.getLastId() },
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
        return submissionsCount;
    }
    
    public synchronized void updateLastSyncedSubmissionStatus() throws JsonProcessingException {
        if (jobInfo.getErrorRecords() + jobInfo.getSuccessRecords() == jobInfo.getTotalRecords()) {
            jobInfo.setJobStatus(Helper.STATUS_COMPLETED);
        }
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jobInfo);
        elasticSearchClient.prepareIndex(Helper.MYELT_ANALYTICS_INDEX, Helper.SUBMISSIONS_JOB_STATUS, String.valueOf(jobInfo.getJobId())).setSource(json).execute().actionGet();
        
        recordsProcessed++;
        if (recordsProcessed == Helper.SQL_RECORDS_LIMIT) {
            recordsProcessed = 0;
            startSyncJob();
        }
    }
   
    public void updateLastJobInfoInES(String jobId) throws JsonProcessingException {       
        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put(Helper.ID, jobId);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonMap);
        elasticSearchClient.prepareIndex(Helper.MYELT_ANALYTICS_INDEX, Helper.SUBMISSIONS_JOB_STATUS, Helper.LAST_JOB_ID).setSource(json).execute().actionGet();
    }
    
    public void refreshJobStatusFromES() throws JsonProcessingException {
        if (Helper.isIndexExist(Helper.MYELT_ANALYTICS_INDEX, elasticSearchClient) && Helper.isTypeExist(Helper.MYELT_ANALYTICS_INDEX, Helper.SUBMISSIONS_JOB_STATUS, elasticSearchClient)) {
            GetResponse lastJobInfoResponse = elasticSearchClient.prepareGet(Helper.MYELT_ANALYTICS_INDEX, Helper.SUBMISSIONS_JOB_STATUS, Helper.LAST_JOB_ID).execute().actionGet();
            Map<String,Object> lastJobInfoMap = lastJobInfoResponse.getSourceAsMap();
            String lastJobId = (String) lastJobInfoMap.get(Helper.ID);
            if(lastJobId != null){
                jobInfo.setJobId(lastJobId);
                GetResponse lastJobResponse = elasticSearchClient.prepareGet(Helper.MYELT_ANALYTICS_INDEX, Helper.SUBMISSIONS_JOB_STATUS, String.valueOf(lastJobId)).execute().actionGet();
                Map<String,Object> map  = lastJobResponse.getSourceAsMap();
                jobInfo.setLastId((String) map.get(Helper.LAST_ID));
                jobInfo.setSuccessRecords((Integer) map.get(Helper.SUCCESSFULL_RECORDS));
                jobInfo.setErrorRecords((Integer) map.get(Helper.ERROR_RECORDS));
                jobInfo.setTotalRecords((Integer) map.get(Helper.TOTAL_RECORDS));
                String jobStatus = (String) map.get(Helper.JOB_STATUS);
                //If server shut-down while job is running, status is still "InProgress" in Database, but the job is actually terminated/paused
                if (jobStatus.equals(Helper.STATUS_INPROGRESS)) {
                    jobInfo.setJobStatus(Helper.STATUS_PAUSED);
                    updateLastSyncedSubmissionStatus();
                } else {
                    jobInfo.setJobStatus((String) map.get(Helper.JOB_STATUS));
                }
                
            }
        }
    }
    
    public void createSubmissionsIndex() throws IOException {
        if (!Helper.isIndexExist(SUBMISSIONS_INDEX, elasticSearchClient)) {
            
            elasticSearchClient.admin().indices().create(new CreateIndexRequest(SUBMISSIONS_INDEX)
                    .mapping(SUBMISSIONS_TYPE, buildSumissionTypeMappings())).actionGet();      
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
    
}
