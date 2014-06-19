package myeltanalytics.model;

import myeltanalytics.service.HelperService;

import org.dom4j.Node;


public class Institution extends AbstractInstitution
{
    private String country;
    private String other;
    private String district;
      
    /**
     * @param id
     * @param name
     */
    public Institution(String id, String name, String country, String other, String district)
    {
        super(id,name);
        this.setCountry(country);
        this.other = other;
        this.district = district;
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
    /**
     * @return the district
     */
    protected String getDistrict()
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
    
    public Country getCountry()
    {
        if(!country.equals("")){
            String xPath  = "//country/name[text()=\"" +  country + "\"]";
            Node node = HelperService.countryDocument.selectSingleNode( xPath );
            if(node == null){
                return getDefaultCountry();
            }
            return new Country(country, node.getParent().valueOf("code"));
        } 
        return getDefaultCountry();
    }
    
    public Country getDefaultCountry()
    {
        return new Country("Madagascar","MG");
    }
    
    public void setCountry(String country)
    {
        this.country = country;
    }
    
}