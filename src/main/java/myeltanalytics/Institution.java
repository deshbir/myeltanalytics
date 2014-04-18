package myeltanalytics;

public class Institution
{
    private String id;
    private String name;
    private String country;
    private String other;
    private String district;
      
    /**
     * @param id
     * @param name
     */
    public Institution(String id, String name, String country, String other, String district)
    {
        this.id = id;
        this.name = name;
        this.country = country;
        this.other = other;
        this.district = district;
    }
    /**
     * @return the other
     */
    public String getOther()
    {
        return other;
    }
    /**
     * @param other the other to set
     */
    public void setOther(String other)
    {
        this.other = other;
    }
    /**
     * @return the district
     */
    public String getDistrict()
    {
        return district;
    }
    /**
     * @param district the district to set
     */
    public void setDistrict(String district)
    {
        this.district = district;
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