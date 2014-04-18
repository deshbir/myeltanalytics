package myeltanalytics;

public class PushUserEvent
{
    private String type;
    private String index;
    private long id;
    
    public PushUserEvent(String index, String type , long id)
    {
        this.type = type;
        this.index = index;
        this.id = id;
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

    
    
}
