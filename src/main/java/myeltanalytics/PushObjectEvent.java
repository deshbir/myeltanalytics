package myeltanalytics;

public class PushObjectEvent
{
    private String document;
    private String index;
    private String id;
    private User user;
    
    
    /**
     * @param document
     * @param index
     * @param id
     */
    public PushObjectEvent(String document, String index, String id,User user)
    {
        this.document = document;
        this.index = index;
        this.id = String.valueOf(id);
        this.user = user;
    }
    
    
    public PushObjectEvent(String document, String index, long id,User user)
    {
        this.document = document;
        this.index = index;
        this.id = String.valueOf(id);
        this.user = user;
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


    public User getUser()
    {
        return user;
    }


    public void setUser(User user)
    {
        this.user = user;
    }
    
    
    
}
