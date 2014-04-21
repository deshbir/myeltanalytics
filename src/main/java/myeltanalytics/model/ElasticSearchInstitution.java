package myeltanalytics.model;

public class ElasticSearchInstitution extends AbstractInstitution
{
    
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
        ElasticSearchInstitution esi = new ElasticSearchInstitution(institution.getName(),institution.getId());
        esi.setCountry(institution.getCountry());
        return esi;
    }
    
    
}