package myeltanalytics.service;

import java.util.Iterator;
import java.util.List;

import myeltanalytics.model.Country;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

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
    public static final String LAST_IDENTIFIER = "lastIdentifier";
    public static final String JOB_STATUS = "jobStatus";
    public static final String START_DATETIME = "startDateTime";    
    public static final String SYNC_JOB_ID = "syncJobId";    
    public static final String STATUS_INPROGRESS = "InProgress";
    public static final String STATUS_PAUSED = "Paused";
    public static final String STATUS_COMPLETED = "Completed";
    
    //Users constants
    public static final String USER_WITH_ACCESSCODE = "user_with_accesscode";
    public static final String USER_WITHOUT_ACCESSCODE = "user_without_accesscode";
    public static final String ADDITIONAL_ACCESSCODE = "additional_accesscode";
    public static final String CAPES_MODEL = "capes_model";
    public static final String ICPNA_INSTITUTION = "ICPNA";
    public static final String SEVEN_INSTITUTION = "7";
    public static final String USERS_INDEX = "users";
    public static final String USERS_TYPE = "users_info";
    public static final String USERS_ALL_ALIAS = "users_all";
    public static final String ACCESS_CODES_ALL_ALIAS = "accesscodes_all";
    public static final String USERS_CAPES_ALIAS = "users_capes";
    public static final String USERS_ICPNA_ALIAS = "users_icpna";
    public static final String USERS_SEVEN_ALIAS = "users_seven";
   
    //Submissions Constants
    public static final String SUBMISSIONS_ALL_ALIAS = "submissions_all";
    public static final String SUBMISSIONS_ASSIGNMENTS_ALIAS = "submissions_assignments";
    public static final String SUBMISSIONS_EXAMVIEW_ALIAS = "submissions_examview";    
    public static final String SUBMISSIONS_INDEX = "submissions";    
    public static final String SUBMISSIONS_TYPE = "submissions_info";
    
    public static final String ID = "id";    
    public static final String BLANK = "";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String IGNORE_INSTITUTIONS = "('COMPROTEST','MYELT','TLTELT' ,'TLIBERO' ,'TLUS' ,'TEST' ,'TLEMEA' ,'TLASI')";
    public static final long SQL_RECORDS_LIMIT = 50000;
    
    public static final String DEFAULT_ERROR_MESSAGE = "It looks like something went wrong and an error has occurred. Please try agin later.";
    public static final String MYSQL_ERROR_MESSAGE = "Unable to communicate with MySQL Server. Please check MySQL Server settings in Settings Tab.";
    
    public static final String DEFAULT_REGION = "North America";
    
    public static Document countryDocument = null;    
    public static Document institutionDocument = null;
    public static JSONObject regionCountryMap = new JSONObject(); 
    
    private static final Logger LOGGER = Logger.getLogger(Helper.class);
    
    static {
        setupCountryDoc();
        setupInstitutionDoc();
        setupRegionCountryMap();
    }
    
    private static void setupCountryDoc() {
        SAXReader reader = new SAXReader();
        try
        {
            Resource countryResource = new ClassPathResource(COUNTRY_XML_FILE_NAME);
            countryDocument = reader.read(countryResource.getInputStream());
        }
        catch (Exception e)
        {
            LOGGER.error("Error reading Country XML file", e);
        }        
    }
    
    private static void setupInstitutionDoc() {
        SAXReader reader = new SAXReader();
        try
        {
            Resource institutionResource = new ClassPathResource(INSTITUTION_XML_FILE_NAME);
            institutionDocument = reader.read(institutionResource.getInputStream());
        }
        catch (Exception e)
        {
            LOGGER.error("Error reading Institution XML file", e);
        }       
    }
    
    private static void setupRegionCountryMap() {
        try {
            JSONArray regionArray = new JSONArray();
            if (countryDocument == null) {
                setupCountryDoc();
            }
            List<?> regionList = countryDocument.selectNodes( "//region" );            
            for (Iterator<?> regionIter = regionList.iterator(); regionIter.hasNext(); ) {
                Node regionNode = (Node) regionIter.next();
                JSONObject regionObj = new JSONObject();
                regionObj.put("name", regionNode.valueOf("name"));
                JSONArray countriesArray = new JSONArray();
                List<?> countriesList = regionNode.selectNodes("./countries/country");
                for (Iterator<?> countryIter = countriesList.iterator(); countryIter.hasNext(); ) {
                    Node countryNode = (Node) countryIter.next();
                    JSONObject countryObj = new JSONObject();
                    countryObj.put("name", countryNode.valueOf("name"));
                    countriesArray.put(countryObj);
                }
                regionObj.put("countries", countriesArray);  
                regionArray.put(regionObj);
            }  
            regionCountryMap.put("regions", regionArray);
        }
        catch (Exception e)
        {
            LOGGER.error("Error creating region country map", e);
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
   
    public static JSONObject getCountryDocumentAsJson() throws JSONException {
       if (regionCountryMap == null) {
           setupRegionCountryMap();
       }
       return regionCountryMap;
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
        String xPath  = "//country/code[text()=\"" +  countryCode.toUpperCase() + "\"]";
        Node node = countryDocument.selectSingleNode( xPath );
        if(node != null){
            return node.getParent().getParent().valueOf("name");
        }
        return DEFAULT_REGION;
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
        String xPath  = "//country/code[text()=\"" +  countryCode.toUpperCase() + "\"]";
        Node node = countryDocument.selectSingleNode( xPath );
        if(node != null){
            return node.getParent().valueOf("name");
        }
        return null;
    }
    
    /**
     * Deletes the user records with different sync id on job completion 
     * @param index: index whose records are to be deleted
     * @param type: type for which records are to be deleted
     * @param jobId: which has to be checked for calculating out of sync.
     */
    public static void deleteUnsyncedRecords(Client elasticSearchClient, String index, String type, String jobId)
    {
        elasticSearchClient.prepareDeleteByQuery(index)
            .setQuery(
                QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("_type", type))
                .mustNot(QueryBuilders.matchQuery("syncJobId",jobId))
                )
            .execute()
            .actionGet();        
    }
    
    public static String constructErrorResponse(String errorMessage) {
        String response = "{\"status\":\"error\",\"errorMessage\":\"" + errorMessage + "\"}";
        return response;
    }
    
    public static String constructSuccessResponse() {
        return "{\"status\":\"success\"}";
    }
}
