package myeltanalytics.service;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import myeltanalytics.model.Constants;

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
import org.springframework.stereotype.Service;

@Service(value="helperService")
public class HelperService
{
    public static final String[] IGNORE_INSTITUTIONS = new String[]{"COMPROTEST","MYELT","TLTELT" ,"TLIBERO" ,"TLUS" ,"TEST" ,"TLEMEA" ,"TLASI","API Test Bar Aux", "MyELT AUX 4", "QA AUX 4 Test","APRIL 5", "capesmyelt206_qainfotech", "capesprod", "July 16", "July 17", "QA AUX 1 Test", "QA AUX 2 Test", "CAPES AUX DB 3 Test", "QA AUX 3 Test"};
    public static Document countryDocument = null;    
    public static Document institutionDocument = null;
    public static JSONObject regionCountryMap = new JSONObject(); 
    public static JSONArray ignoreInstitutionsJson = new JSONArray();
    public static String ignoreInstitutionsQuery = null;
    
    private static final Logger LOGGER = Logger.getLogger(HelperService.class);
    
    
    @PostConstruct
    private void setup() throws Exception {
        setupCountryDoc();
        setupInstitutionDoc();
        setupRegionCountryMap();
        setupIgnoreInstitutions();
    }
    private void setupIgnoreInstitutions() throws JSONException {
        StringBuffer ignoreInstitutionsQueryBuffer = new StringBuffer("(");
        for (int i=0; i<IGNORE_INSTITUTIONS.length; i++) {
            ignoreInstitutionsJson.put(IGNORE_INSTITUTIONS[i]);
            ignoreInstitutionsQueryBuffer.append("'").append(IGNORE_INSTITUTIONS[i]).append("'");
            if (i < IGNORE_INSTITUTIONS.length - 1) {
                ignoreInstitutionsQueryBuffer.append(",");
            }
        }
        ignoreInstitutionsQueryBuffer.append(")");
        ignoreInstitutionsQuery = ignoreInstitutionsQueryBuffer.toString();
    }
    private void setupCountryDoc() {
        SAXReader reader = new SAXReader();
        try
        {
            Resource countryResource = new ClassPathResource(Constants.COUNTRY_XML_FILE_NAME);
            countryDocument = reader.read(countryResource.getInputStream());
        }
        catch (Exception e)
        {
            LOGGER.error("Error reading Country XML file", e);
        }        
    }
    
    private void setupInstitutionDoc() {
        SAXReader reader = new SAXReader();
        try
        {
            Resource institutionResource = new ClassPathResource(Constants.INSTITUTION_XML_FILE_NAME);
            institutionDocument = reader.read(institutionResource.getInputStream());
        }
        catch (Exception e)
        {
            LOGGER.error("Error reading Institution XML file", e);
        }       
    }
    
    private void setupRegionCountryMap() {
        try {
            JSONArray regionArray = new JSONArray();
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
    
    public boolean isIndexExist(String index, Client elasticSearchClient) {
        ActionFuture<IndicesExistsResponse> exists = elasticSearchClient.admin().indices().exists(new IndicesExistsRequest(index));
        IndicesExistsResponse actionGet = exists.actionGet();
        return actionGet.isExists();
    }
    
    public boolean isTypeExist(String index, String type, Client elasticSearchClient) {
        ActionFuture<TypesExistsResponse> exists = elasticSearchClient.admin().indices().typesExists(new TypesExistsRequest(new String[] { index} , type));
        TypesExistsResponse actionGet = exists.actionGet();
        return actionGet.isExists();
    }
   
    public JSONObject getRegionCountryMap() throws JSONException {
       return regionCountryMap;
    }     
   
    /**
     * Deletes the user records with different sync id on job completion 
     * @param index: index whose records are to be deleted
     * @param type: type for which records are to be deleted
     * @param jobId: which has to be checked for calculating out of sync.
     */
    public void deleteUnsyncedRecords(Client elasticSearchClient, String index, String type, String jobId)
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
    
    public String constructErrorResponse(String errorMessage) {
        String response = "{\"status\":\"error\",\"errorMessage\":\"" + errorMessage + "\"}";
        return response;
    }
    
    public String constructSuccessResponse() {
        return "{\"status\":\"success\"}";
    }
}
