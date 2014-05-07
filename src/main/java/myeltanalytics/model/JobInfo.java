package myeltanalytics.model;

public class JobInfo
{
    private String jobId;
    private String jobStatus;
    private String lastId;
    private long totalRecords;
    private long successRecords;
    private long errorRecords;
    
    
    public JobInfo() {
        //default
    }
    
    
    
    public long getTotalRecords()
    {
        return totalRecords;
    }
    public void setTotalRecords(long totalRecords)
    {
        this.totalRecords = totalRecords;
    }
    public long getSuccessRecords()
    {
        return successRecords;
    }
    public void setSuccessRecords(long successRecords)
    {
        this.successRecords = successRecords;
    }
    public long getErrorRecords()
    {
        return errorRecords;
    }
    public void setErrorRecords(long errorRecords)
    {
        this.errorRecords = errorRecords;
    }
    
    
    public String getLastId()
    {
        return lastId;
    }

    public void setLastId(String lastId)
    {
        this.lastId = lastId;
    }



    public String getJobId()
    {
        return jobId;
    }



    public void setJobId(String jobId)
    {
        this.jobId = jobId;
    }



    public String getJobStatus()
    {
        return jobStatus;
    }



    public void setJobStatus(String jobStatus)
    {
        this.jobStatus = jobStatus;
    }
    
}