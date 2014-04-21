package myeltanalytics.model;

public class Country
{
    private String name;
    private String code;
    
    
    /**
     * @param name
     * @param code
     */
    public Country(String name, String code)
    {
        this.name = name;
        this.code = code;
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
    /**
     * @return the code
     */
    public String getCode()
    {
        return code;
    }
    /**
     * @param code the code to set
     */
    public void setCode(String code)
    {
        this.code = code;
    }
    
}
