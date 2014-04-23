package myeltanalytics.controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.annotation.PostConstruct;

import myeltanalytics.model.SyncUserEvent;
import myeltanalytics.service.EventBusService;
import myeltanalytics.service.Helper;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.TermsFilterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
@RequestMapping("/users")
public class UsersSyncController {
  
    public static long LAST_USER_ID = -1;    
    
    private final Logger LOGGER = Logger.getLogger(UsersSyncController.class);
    
    @Autowired
    private Client elasticSearchClient;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private EventBusService eventBusService;
    
    @PostConstruct
    void initializeLastStatusParameters() throws IOException{
        setLastSyncedUserId();
        createUsersIndex();
    }    
    
    @RequestMapping(value= "/startSync")
    @ResponseBody String startSyncUsers() throws JsonProcessingException{
        String query = "select id from users where type=0 and id > ? order by id";
        if (Helper.USER_QUERY_LIMIT > 0) {
            query = query + " limit " + Helper.USER_QUERY_LIMIT;
        }
        try {
            jdbcTemplate.query(
                query, new Object[] { LAST_USER_ID },
                new RowCallbackHandler()
                {
                    @Override
                    public void processRow(ResultSet rs) throws SQLException
                    {
                        try
                        {
                            SyncUserEvent event = new SyncUserEvent(Helper.USERS_INDEX, Helper.USERS_TYPE, rs.getLong("id"));
                            eventBusService.postEvent(event);
                        } catch(Exception e){
                            LOGGER.error("Error while processing User row" ,e);
                        }
                        
                    }
                });
            LOGGER.debug("Started User sync process");
            return "Started User sync process";
        } catch (Exception e) {
            LOGGER.error("Error starting User sync process", e);
            return "Error starting User sync process";
        }
        
    }      
    
    private void setLastSyncedUserId() {
        try {
            GetResponse userIdStatusResponse = elasticSearchClient.prepareGet(Helper.MYELT_ANALYTICS_INDEX, Helper.USERS_SYNC_JOB_TYPE, "lastUserId").execute().actionGet();
            Map<String,Object> map = userIdStatusResponse.getSourceAsMap();
            Integer userIdStatus = (Integer) map.get("id");
            if(userIdStatus != null){
                LAST_USER_ID = userIdStatus; 
            }
        }
        catch (Exception e){
            //will come when application is started first time
            //ignore if comes once
            LOGGER.error("An error occured while reading last synced UserId from ElasticSearch" ,e);
        }
    }   
  
    private void createUsersIndex() throws IOException {
        if (!Helper.isIndexExist(Helper.USERS_INDEX, elasticSearchClient)) {
            
            elasticSearchClient.admin().indices().create(new CreateIndexRequest(Helper.USERS_INDEX)
                    .mapping(Helper.USERS_TYPE, buildUserTypeMappings())).actionGet();      
            
            TermsFilterBuilder usersOnlyFilter = FilterBuilders.termsFilter("recordType", Helper.USER_WITH_ACCESSCODE.toLowerCase(), Helper.USER_WITHOUT_ACCESSCODE.toLowerCase());
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Helper.USERS_INDEX, Helper.USERS_ONLY_ALIAS, usersOnlyFilter).execute().actionGet();
            
            TermsFilterBuilder accessCodesOnlyFilter = FilterBuilders.termsFilter("recordType", Helper.USER_WITH_ACCESSCODE.toLowerCase(), Helper.ADDITIONAL_ACCESSCODE.toLowerCase());
            elasticSearchClient.admin().indices().prepareAliases().addAlias(Helper.USERS_INDEX, Helper.ACCESS_CODES_ONLY_ALIAS, accessCodesOnlyFilter).execute().actionGet();
        }      
    } 
    
    private XContentBuilder buildUserTypeMappings(){
        XContentBuilder builder = null; 
             try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject()
            .startObject("properties")
                .startObject("accessCode")
                    .startObject("properties")
                        .startObject("discipline")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()
                         .startObject("productName")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()
                     .endObject()
                .endObject()
                .startObject("country")
                    .startObject("properties")
                        .startObject("name")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()
                     .endObject()
                .endObject()
                .startObject("userCountry")
                    .startObject("properties")
                        .startObject("name")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()
                     .endObject()
                .endObject()
                .startObject("region")
                    .field("type", "string")                      
                    .field("index", "not_analyzed")
                 .endObject()
                .startObject("courses")
                    .field("type", "string")                      
                    .field("index", "not_analyzed")
                 .endObject()
                 .startObject("productNames")
                    .field("type", "string")                      
                    .field("index", "not_analyzed")
                 .endObject()
                .startObject("disciplines")
                    .field("type", "string")                      
                    .field("index", "not_analyzed")
                 .endObject()
                 .startObject("institution")
                     .startObject("properties")
                         .startObject("name")
                             .field("type", "string")                      
                             .field("index", "not_analyzed")
                         .endObject()
                         .startObject("country")
                            .startObject("properties")
                                .startObject("name")
                                     .field("type", "string")                      
                                     .field("index", "not_analyzed")
                                 .endObject()
                             .endObject()
                        .endObject()
                     .endObject()      
                 .endObject()
           .endObject()
           .endObject();           
        } catch (Exception e) {
            LOGGER.error("An error occured while building mapping for user_info" , e);
        }
        return builder;
    }
    
    
}
