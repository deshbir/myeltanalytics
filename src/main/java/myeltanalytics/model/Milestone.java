package myeltanalytics.model;

public class Milestone
{
    private String id;
    private String status;
    private String level;
    private String score;
    private String maxScore;
    private String isActive;
    private String testName;
    private String passPercent;
    private String passAction;
    private String expiry;
    private String startedDate;
    private String accessedDate;
    private String completedDate;
    
    
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public String getAccessedDate()
    {
        return accessedDate;
    }
    public void setAccessedDate(String accessedDate)
    {
        this.accessedDate = accessedDate;
    }
    public String getStatus()
    {
        return status;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }
    public String getLevel()
    {
        return level;
    }
    public void setLevel(String level)
    {
        this.level = level;
    }
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getMaxScore() {
		return maxScore;
	}
	public void setMaxScore(String maxScore) {
		this.maxScore = maxScore;
	}
	public String getStartedDate() {
		return startedDate;
	}
	public void setStartedDate(String startedDate) {
		this.startedDate = startedDate;
	}
	public String getCompletedDate() {
		return completedDate;
	}
	public void setCompletedDate(String completedDate) {
		this.completedDate = completedDate;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public String getPassPercent() {
		return passPercent;
	}
	public void setPassPercent(String passPercent) {
		this.passPercent = passPercent;
	}
	public String getPassAction() {
		return passAction;
	}
	public void setPassAction(String passAction) {
		this.passAction = passAction;
	}
	public String getExpiry() {
		return expiry;
	}
	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}
	
}
