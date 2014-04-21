package myeltanalytics.service;

import java.io.File;

import myeltanalytics.model.Country;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;

public class Helper
{
    
    public static final String COUNTRY_XML_FILE_NAME = "country.xml";
    
    public static final String INSTITUTION_XML_FILE_NAME = "institution.xml";
    
    public static String USERS_INDEX = "users";
    
    public static String USERS_TYPE = "user_info";
    
    public static String SUBMISSIONS_INDEX = "submissions";
    
    public static String SUBMISSIONS_TYPE = "submissions_info";
    
    public static String MYELT_ANALYTICS_INDEX = "myeltanalytics";
    
    public static String MYELT_ANALYTICS_TYPE = "status";
    
    public static final String BLANK = "";
    
    public static Document countryDocument = null;
    public static Document institutionDocument = null;
    
    static {
        SAXReader reader = new SAXReader();
        try
        {
            countryDocument = reader.read(new File(COUNTRY_XML_FILE_NAME));
            institutionDocument = reader.read(new File(INSTITUTION_XML_FILE_NAME));
        }
        catch (DocumentException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    public static boolean isIndexExist(String index, Client elasticSearchClient) {
        ActionFuture<IndicesExistsResponse> exists = elasticSearchClient.admin().indices().exists(new IndicesExistsRequest(index));
        IndicesExistsResponse actionGet = exists.actionGet();
        return actionGet.isExists();
    }

    public static String lookupCountryCode(String countryName)
    {
        String xPath  = "//country/name[text()=\"" +  countryName + "\"]";
        Node node = countryDocument.selectSingleNode( xPath );
        if(node != null){
            return node.getParent().valueOf("/code");
        }
        return "";
    }

    public static String getRegion(String countryCode)
    {
        String xPath  = "//country/code[text()=\"" +  countryCode + "\"]";
        Node node = countryDocument.selectSingleNode( xPath );
        if(node != null){
            return node.getParent().valueOf("region");
        }
        return "";
    }

    public static Country getInstitutionCountry(String institutionId)
    {
        String xPath  = "//institution/id[text()=\"" +  institutionId + "\"]";
        Node node = institutionDocument.selectSingleNode( xPath );
        if(node != null) {
            Node institution = node.getParent();
            String countryCode = institution.valueOf("countryCode");
            return new Country(countryCode,lookupCountryName(countryCode));
        }
        else 
            return new Country("U.S.A","US");
    }

    private static String lookupCountryName(String countryCode)
    {
        String xPath  = "//country/code[text()=\"" +  countryCode + "\"]";
        Node node = countryDocument.selectSingleNode( xPath );
        if(node != null){
            return node.getParent().valueOf("name");
        }
        return "";
    }
}
