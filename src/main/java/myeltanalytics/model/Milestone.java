package myeltanalytics.model;

public class Milestone
{
    private String id;
    private String status;
    private String level;
    private String startedDate;
    
    
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
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
    public String getStartedDate()
    {
        return startedDate;
    }
    public void setStartedDate(String startedDate)
    {
        this.startedDate = startedDate;
    }
}
