package myeltanalytics.model;

public class SyncInfo {
	private String status;
	private String jobId;
	private String exceptionClasss;
	private String message;
	private String stacktrace;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStacktrace() {
		return stacktrace;
	}
	public void setStacktrace(String stacktrace) {
		this.stacktrace = stacktrace;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getExceptionClasss() {
		return exceptionClasss;
	}
	public void setExceptionClasss(String exceptionClasss) {
		this.exceptionClasss = exceptionClasss;
	}
	
}

