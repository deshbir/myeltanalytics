package myeltanalytics.model;

public class Milestone
{
    private String id;
    private String status;
    private String level;
    private String accessedDate;
    
    
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
   
}
