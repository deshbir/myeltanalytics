package myeltanalytics.model;

public class ElasticSearchInstitution extends AbstractInstitution
{
	
	public ElasticSearchInstitution(String id)
    {
        super(id);
    }
	
    protected ElasticSearchInstitution(String id, String name)
    {
        super(id, name);
    }


    private Country country;
    

    public Country getCountry()
    {
        return country;
    }

    public void setCountry(Country country)
    {
        this.country = country;
    }

   
    public static ElasticSearchInstitution transformInstitution(Institution institution)
    {
        ElasticSearchInstitution esi = new ElasticSearchInstitution(institution.getId(),institution.getName());
        esi.setCountry(institution.getCountry());
        return esi;
    }
    
    
}