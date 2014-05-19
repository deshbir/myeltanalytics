package myeltanalytics.model;

public class JobInfo
{
    private String jobId;
    private String jobStatus;
    private String lastId;
    private long totalRecords;
    private long successRecords;
    private long errorRecords;
    
    public long getTotalRecords()
    {
        return totalRecords;
    }
    
    public long getSuccessRecords()
    {
        return successRecords;
    }
   
    public long getErrorRecords()
    {
        return errorRecords;
    }
    
    public String getJobId()
    {
        return jobId;
    }
    
    public String getLastId()
    {
        return lastId;
    }

    public String getJobStatus()
    {
        return jobStatus;
    }
    
    public synchronized void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }
    
    public synchronized void setSuccessRecords(long successRecords) {
        this.successRecords = successRecords;
    }
    
    public synchronized void setErrorRecords(long errorRecords) {
        this.errorRecords = errorRecords;
    }
    
    public synchronized void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }
    
    public synchronized void setJobId(String jobId) {
        this.jobId = jobId;
    }
    
    public synchronized void setLastId(String lastId) {
        this.lastId = lastId;
    }
    
    public synchronized void incrementSuccessRecords() {
        this.successRecords++;
    }
    
    public synchronized void incrementErrorRecords() {
        this.errorRecords++;
    }
}