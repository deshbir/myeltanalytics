package myeltanalytics.model;

public class JobStatus
{
    private long jobId;
    private long lastId;
    private long totalRecords;
    private long successRecords;
    private long errorRecords;
    
    
    public JobStatus() {
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
    
    
    public long getLastId()
    {
        return lastId;
    }

    public void setLastId(long lastId)
    {
        this.lastId = lastId;
    }



    public long getJobId()
    {
        return jobId;
    }



    public void setJobId(long jobId)
    {
        this.jobId = jobId;
    }
    
}