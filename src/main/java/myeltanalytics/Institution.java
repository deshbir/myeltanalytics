package myeltanalytics;

public class Institution
{
    private String id;
    private String name;
    private String country;
      
    /**
     * @param id
     * @param name
     */
    public Institution(String id, String name, String country)
    {
        this.id = id;
        this.name = name;
        this.country = country;
    }
    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
    public String getCountry()
    {
        return country;
    }
    public void setCountry(String country)
    {
        this.country = country;
    }
    
}