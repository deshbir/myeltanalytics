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
    private String failedsUserStatus;
	private long failedUserProcessed;
	private long totalFailedUsersToProcess;
	
	
    
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
    
    public String getStartDateTime()
    {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime)
    {
        this.startDateTime = startDateTime;
    }
	public String getFailedsUserStatus() {
		return failedsUserStatus;
	}

	public synchronized void setFailedsUserStatus(String failedsUserStatus) {
		this.failedsUserStatus = failedsUserStatus;
	}

	public long getFailedUserProcessed() {
		return failedUserProcessed;
	}

	public synchronized void setFailedUserProcessed(long failedUserProcessed) {
		this.failedUserProcessed = failedUserProcessed;
	}

	public long getTotalFailedUsersToProcess() {
		return totalFailedUsersToProcess;
	}

	public synchronized void setTotalFailedUser(long totalFailedUsersToProcess) {
		this.totalFailedUsersToProcess = totalFailedUsersToProcess;
	}
	
	public synchronized void incrementFailedUserProcessed(){
		this.failedUserProcessed++;
	}

}