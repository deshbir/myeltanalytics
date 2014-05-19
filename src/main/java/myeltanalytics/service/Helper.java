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
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.client.Client;

public class Helper
{
    
    //Country and Institution XML file names
    public static final String COUNTRY_XML_FILE_NAME = "country.xml";    
    public static final String INSTITUTION_XML_FILE_NAME = "institution.xml";
    
    //myelt_analytics index constants    
    public static final String MYELT_ANALYTICS_INDEX = "myelt_analytics";    
    public static final String LAST_JOB_ID = "lastJobId";
    public static final String USERS_JOB_STATUS = "usersJobStatus";
    public static final String SUBMISSIONS_JOB_STATUS = "submissionsJobStatus";
    
    //Job Status constants
    public static final String TOTAL_RECORDS = "totalRecords";    
    public static final String SUCCESSFULL_RECORDS = "successRecords";    
    public static final String ERROR_RECORDS = "errorRecords";    
    public static final String LAST_ID = "lastId";    
    public static final String JOB_STATUS = "jobStatus";
    public static final String SYNC_JOB_ID = "syncJobId";
    
    public static final String STATUS_INPROGRESS = "InProgress";
    public static final String STATUS_PAUSED = "Paused";
    public static final String STATUS_COMPLETED = "Completed";
        
    public static final String ID = "id";    
    public static final String BLANK = "";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String IGNORE_INSTITUTIONS = "('COMPROTEST','MYELT','TLTELT' ,'TLIBERO' ,'TLUS' ,'TEST' ,'TLEMEA' ,'TLASI')";
    public static final long SQL_RECORDS_LIMIT = 100000;
    
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
    
    public static boolean isTypeExist(String index, String type, Client elasticSearchClient) {
        ActionFuture<TypesExistsResponse> exists = elasticSearchClient.admin().indices().typesExists(new TypesExistsRequest(new String[] { index} , type));
        TypesExistsResponse actionGet = exists.actionGet();
        return actionGet.isExists();
    }
   

    public static String lookupCountryCode(String countryName)
    {
        String xPath  = "//country/name[text()=\"" +  countryName + "\"]";
        Node node = countryDocument.selectSingleNode( xPath );
        if(node != null){
            return node.getParent().valueOf("code");
        }
        return null;
    }

    public static String getRegion(String countryCode)
    {
        String xPath  = "//country/code[text()=\"" +  countryCode + "\"]";
        Node node = countryDocument.selectSingleNode( xPath );
        if(node != null){
            return node.getParent().valueOf("region");
        }
        return "North America";
    }

    public static Country getInstitutionCountry(String institutionId)
    {
        String xPath  = "//institution/id[text()=\"" +  institutionId + "\"]";
        Node node = institutionDocument.selectSingleNode( xPath );
        if(node != null) {
            Node institution = node.getParent();
            String countryCode = institution.valueOf("countryCode");
            return new Country(lookupCountryName(countryCode),countryCode);
        }
        else 
            return null;
    }
    
    public static Country getDefaultCountry()
    {
        return new Country("Madagascar","MG");
    }

    private static String lookupCountryName(String countryCode)
    {
        String xPath  = "//country/code[text()=\"" +  countryCode + "\"]";
        Node node = countryDocument.selectSingleNode( xPath );
        if(node != null){
            return node.getParent().valueOf("name");
        }
        return null;
    }
}
