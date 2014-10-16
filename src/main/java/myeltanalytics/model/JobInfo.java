package myeltanalytics.model;

public class JobInfo
{
    private String jobId;
    private String jobStatus;
    /**
     * Unique Identifier
     * 1. "loginName" in case of users
     * 2. "id" in case of submissions
     */
    private String lastIdentifier;
    private long totalRecords;
    private long successRecords;
    private long errorRecords;
    private String startDateTime;
    private String retryJobStatus;
	private long retryRecordsProcessed;
	private long totalRetryRecords;
    
	public String getRetryJobStatus() {
		return retryJobStatus;
	}

	public long getRetryRecordsProcessed() {
		return retryRecordsProcessed;
	}

	public long getTotalRetryRecords() {
		return totalRetryRecords;
	}

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
    
    public String getLastIdentifier()
    {
        return lastIdentifier;
    }

    public String getJobStatus()
    {
        return jobStatus;
    }
    
    public String getStartDateTime()
    {
        return startDateTime;
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
    
    public synchronized void setLastIdentifier(String lastIdentifier) {
        this.lastIdentifier = lastIdentifier;
    }
    
    public synchronized void incrementSuccessRecords() {
        this.successRecords++;
    }
    
    public synchronized void incrementErrorRecords() {
        this.errorRecords++;
    }
    
    public synchronized void decrementErrorRecords() {
        this.errorRecords--;
    }
    
    public synchronized void incrementRetryRecordsProcessed(){
		this.retryRecordsProcessed++;
	}  

    public synchronized void setStartDateTime(String startDateTime)
    {
        this.startDateTime = startDateTime;
    }
    
    public synchronized void setRetryJobStatus(String retryJobStatus) {
		this.retryJobStatus = retryJobStatus;
	}
    
    public synchronized void setRetryRecordsProcessed(long retryRecordsProcessed) {
		this.retryRecordsProcessed = retryRecordsProcessed;
	}

	public synchronized void setTotalRetryRecords(long totalRetryRecords) {
		this.totalRetryRecords = totalRetryRecords;
	}

}