package myeltanalytics.service.listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import myeltanalytics.controller.UsersSyncController;
import myeltanalytics.model.AccessCode;
import myeltanalytics.model.Country;
import myeltanalytics.model.ElasticSearchUser;
import myeltanalytics.model.Institution;
import myeltanalytics.model.SyncUserEvent;
import myeltanalytics.model.User;
import myeltanalytics.service.Helper;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;


@Service(value="usersSyncListener")
public class UsersSyncListener
{
    private final Logger LOGGER = Logger.getLogger(UsersSyncListener.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private EventBus eventBus;
    
    @Autowired
    private Client elasticSearchClient;
    
    @PostConstruct
    void subscribeToBus(){
        eventBus.register(this);
    }
    
    @Subscribe
    @AllowConcurrentEvents
    public void onSyncUserEvent(SyncUserEvent event) {
        try
        {
            User user = populateUser(event.getId());
            List<AccessCode> accessCodes = user.getAccesscodes();
            if(accessCodes.size() ==0){
                ElasticSearchUser esUser  = ElasticSearchUser.transformUser(user, null, Helper.USER_WITHOUT_ACCESSCODE);
                pushuser(esUser,event);
            }
            else {
                for(int i = 0 ;i < accessCodes.size(); i++){
                    ElasticSearchUser esUser = null;
                    AccessCode accessCode  =  accessCodes.get(i);
                    if(i == 0){
                        esUser  = ElasticSearchUser.transformUser(user,accessCode, Helper.USER_WITH_ACCESSCODE);
                        
                    } else {
                        esUser = ElasticSearchUser.transformUser(user,accessCode, Helper.ADDITIONAL_ACCESSCODE);
                    }
                    pushuser(esUser,event);
                }
            }
            //System.out.println("User with UserId= " + event.getId() + " pushed successfully");
            LOGGER.debug("User with UserId= " + event.getId() + " synced successfully");
        }
        catch(Exception e){
            //e.printStackTrace();
            LOGGER.error("Failure for UserId= " + event.getId(), e);
            //TO-DO retry logic if neccessary
        }
    }
    private void pushuser(ElasticSearchUser esUser,SyncUserEvent event) throws JsonProcessingException{
        
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse
        String json = mapper.writeValueAsString(esUser);
        elasticSearchClient.prepareIndex(event.getIndex(),event.getType()).setSource(json).execute().actionGet();
        setLastUserStatus(event.getId());
    }
    private User populateUser(long userId)
    {
       User user = jdbcTemplate.queryForObject(
            "select id,name,email,parent,createdAt,lastLoginAt,firstName,lastName,country,countryCode,InstitutionID from users where id = ?", new Object[] { userId },
            new RowMapper<User>() {
                
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUserName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setUserType(rs.getInt("parent"));
                    user.setDateCreated(rs.getTimestamp("createdAt"));
                    user.setDateLastLogin(rs.getLong("lastLoginAt"));
                    user.setFirstName(rs.getString("firstName"));
                    user.setLastName(rs.getString("lastName"));
                    Country country = new Country(rs.getString("country"),rs.getString("countryCode"));
                    user.setUserCountry(country);
                    user.setInstitution(populateInstitution(rs.getString("InstitutionID")));                   
                    user.setCourses(populateCourses(user.getId()));
                    user.setAccesscodes(populateAccessCodes(user.getId()));
                    return user;
                }
            });
        return user;
    }
    
    protected List<AccessCode> populateAccessCodes(long userId)
    {
        List<AccessCode> accessCode = jdbcTemplate.query(
            "select d.name as Discipline,bl.name as ProductName,SUBSTRING(ar.feature,11) as ProductCode,ba.AccessCode,ba.LastModified from accessrights as ar, bookaccesscodes as ba,booklist as bl, discipline as d where d.Abbr = bl.discipline and bl.Abbr=SUBSTRING(ar.feature,11) and ar.userId=ba.userId and ba.userId=? and ba.BookAbbr like CONCAT(SUBSTRING(ar.feature,11),'%') order by ba.LastModified", new Object[] {userId},
            new RowMapper<AccessCode>() {
                @Override
                public AccessCode mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AccessCode accessCode = new AccessCode();
                    accessCode.setCode(rs.getString("ProductCode") + "-" + rs.getString("AccessCode"));
                    accessCode.setDateCreated(rs.getTimestamp("LastModified"));
                    accessCode.setProductCode(rs.getString("ProductCode"));
                    accessCode.setProductName(rs.getString("ProductName"));
                    accessCode.setDiscipline(rs.getString("Discipline"));
                    return accessCode;
                }
            });
        return accessCode;
    }
    
    protected List<String> populateCourses(long userId)
    {
        List<String> courses = jdbcTemplate.query(
            "select name from sections, sectionmembers where sections.id=sectionmembers.sectionId and sectionmembers.userId = ?", new Object[] { userId },
            new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("name");
                }
            });
        return courses;
    }


    protected Institution populateInstitution(String institutionId){
        Institution  institution = jdbcTemplate.queryForObject(
            "select institutions.id,institutions.name,institutions.country,institutions.other,districts.name as district from institutions left join districts on districts.id=institutions.DistrictID where institutions.id=?", new Object[] { institutionId },
            new RowMapper<Institution>() {
                @Override
                public Institution mapRow(ResultSet rs, int rowNum) throws SQLException {                                      
                    return new Institution(rs.getString("id"),rs.getString("name"), rs.getString("country"), rs.getString("other"), rs.getString("district"));
                }
            });
        return institution;

    }
    
    synchronized void setLastUserStatus(long userStatus) throws JsonProcessingException{
        UsersSyncController.LAST_USER_ID = userStatus;
        String json = "{\"id\": " + userStatus + "}";
        elasticSearchClient.prepareIndex(Helper.MYELT_ANALYTICS_INDEX, Helper.USERS_SYNC_JOB_TYPE, "lastUserId").setSource(json).execute().actionGet();
    }
}
