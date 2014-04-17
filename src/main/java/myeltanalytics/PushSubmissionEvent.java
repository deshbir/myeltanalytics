package myeltanalytics;

public class PushSubmissionEvent
{
    private String type;
    private String index;
    private long id;
    private long jobId;
    
    public PushSubmissionEvent(String index, String type, long id,long jobId)
    {
        this.type = type;
        this.index = index;
        this.id = id;
        this.setJobId(jobId);
    }
    
    
    public String getType()
    {
        return type;
    }
    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getIndex()
    {
        return index;
    }
    public void setIndex(String index)
    {
        this.index = index;
    }
    public long getId()
    {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
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
