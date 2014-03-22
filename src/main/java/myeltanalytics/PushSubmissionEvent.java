package myeltanalytics;

public class PushSubmissionEvent
{
    private String document;
    private String index;
    private long id;
    
    public PushSubmissionEvent(String document, String index, long id)
    {
        this.document = document;
        this.index = index;
        this.id = id;
    }
    
    
    public String getDocument()
    {
        return document;
    }
    public void setDocument(String document)
    {
        this.document = document;
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
