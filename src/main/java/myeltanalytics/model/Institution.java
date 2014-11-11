package myeltanalytics.model;

import myeltanalytics.service.HelperService;

import org.dom4j.Node;


public class Institution extends AbstractInstitution
{
    private Country country;
    private String other;
      
    /**
     * @param id
     * @param name
     */
    public Institution(String id, String name, String country, String other)
    {
        super(id,name);
        this.setCountry(country);
        this.other = other;
    }
    
    public Institution(String id){
    	super(id);
    }
    /**
     * @return the other
     */
    protected String getOther()
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
    
    public Country getCountry()
    {
       return country;
    }   
    
    public void setCountry(Country country)
    {
        this.country = country;
    }
    
    public void setCountry(String countryName)
    {
        if(countryName != null && !countryName.equals("")){
            String xPath  = "//country/name[text()=\"" +  country + "\"]";
            Node node = HelperService.countryDocument.selectSingleNode( xPath );
            if(node != null){
                this.country = new Country(countryName, node.getParent().valueOf("code"));
            } 
        } 
    }    
}