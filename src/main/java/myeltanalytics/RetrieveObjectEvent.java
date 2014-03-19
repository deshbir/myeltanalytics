package myeltanalytics;

public class RetrieveObjectEvent
{
    private String document;
    private String index;
    private String id;
    
    
    
    /**
     * @param document
     * @param index
     * @param id
     */
    public RetrieveObjectEvent(String document, String index, String id)
    {
        this.document = document;
        this.index = index;
        this.id = id;
    }
    
    
    public RetrieveObjectEvent(String document, String index, int id)
    {
        this.document = document;
        this.index = index;
        this.id = String.valueOf(id);
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
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    
    
    
}
