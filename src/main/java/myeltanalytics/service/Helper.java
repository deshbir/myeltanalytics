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
    
    public static final String USERS_INDEX = "users";
    
    public static final String MYELT_ANALYTICS_INDEX = "myelt_analytics";
    
    
    public static final String USERS_TYPE = "users_info";
    
    public static final String USERS_ONLY_ALIAS = "users_only";
    
	public static final String ACCESS_CODES_ONLY_ALIAS = "accesscodes_only";
    
    public static final String SUBMISSIONS_INDEX = "submissions";
    
    public static final String USERS_SYNC_JOB_TYPE = "users_sync_job";
    
    
    public static final String TOTAL_RECORDS = "totalRecords";
    
    public static final String SUCCESSFULL_RECORDS = "successRecords";
    
    public static final String ERROR_RECORDS = "errorRecords";
    
    public static final String USERS_LAST_SYNCED_ID = "last_synced_user_id";
    
    public static final String USERS_JOB_STATUS = "userstatus";
    
    public static final String SUBMISSIONS_SYNC_JOB_TYPE = "submissions_sync_job";
    
    public static final String LAST_JOB_ID = "lastJobId";
    
    public static final String JOB_STATUS_INPROGRESS = "in_progress";
    
    public static final String JOB_STATUS_PAUSED = "paused";
    
    public static final String JOB_STATUS_COMPLETED = "completed";

    public static final String USER_WITH_ACCESSCODE = "user_with_accesscode";
    
    public static final String USER_WITHOUT_ACCESSCODE = "user_without_accesscode";
    
    public static final String ADDITIONAL_ACCESSCODE = "additional_accesscode";
    
    public static final String ID = "id";
    
    public static final String BLANK = "";

    public static final String SUBMISSIONS_TYPE = "submissions_info";
    
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    
    public static String usersSyncJobId;
    
    public static String usersSyncJobStatus;
    
    public static long lastSyncedUserId;
    
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
