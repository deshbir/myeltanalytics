package myeltanalytics.service.users;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import myeltanalytics.model.AccessCode;
import myeltanalytics.model.Country;
import myeltanalytics.model.ElasticSearchUser;
import myeltanalytics.model.Institution;
import myeltanalytics.model.User;
import myeltanalytics.service.ApplicationContextProvider;
import myeltanalytics.service.Helper;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class UsersSyncThread implements Runnable
{
    private JdbcTemplate jdbcTemplate = (JdbcTemplate)ApplicationContextProvider.getApplicationContext().getBean("jdbcTemplate");
    
    private Client elasticSearchClient = (Client)ApplicationContextProvider.getApplicationContext().getBean("elasticSearchClient");
    
    private UsersSyncService usersSyncService = (UsersSyncService)ApplicationContextProvider.getApplicationContext().getBean("usersSyncService");
    
    private final Logger LOGGER = Logger.getLogger(UsersSyncThread.class);
    
    private String userId;
    
    public UsersSyncThread(String userId) {
        this.userId = userId;
    }
    
    @Override
    public void run() {
        if(usersSyncService.jobInfo != null && !(usersSyncService.jobInfo.getJobStatus().equals(Helper.STATUS_PAUSED))){
            try
            {
                User user = populateUser(userId);
                List<AccessCode> accessCodes = user.getAccesscodes();
                if(accessCodes.size() ==0){
                    ElasticSearchUser esUser  = ElasticSearchUser.transformUser(user,null,"USER_WITHOUT_ACCESSCODE",usersSyncService.jobInfo.getJobId());
                    pushuser(esUser);
                }
                else {
                    for(int i = 0 ;i < accessCodes.size(); i++){
                        ElasticSearchUser esUser = null;
                        AccessCode accessCode  =  accessCodes.get(i);
                        if(i == 0){
                            esUser  = ElasticSearchUser.transformUser(user,accessCode,"USER_WITH_ACCESSCODE",usersSyncService.jobInfo.getJobId());
                            
                        } else {
                            esUser = ElasticSearchUser.transformUser(user,accessCode,"ADDITIONAL_ACCESSCODE",usersSyncService.jobInfo.getJobId());
                        }
                        pushuser(esUser);
                    }
                }
                usersSyncService.jobInfo.setSuccessRecords(usersSyncService.jobInfo.getSuccessRecords() + 1);
                usersSyncService.jobInfo.setLastId(userId);
                usersSyncService.updateLastSyncedUserStatus();
                LOGGER.debug("User with UserId= " + userId + " synced successfully");
            }
            catch(Exception e){
                //e.printStackTrace();
                usersSyncService.jobInfo.setErrorRecords(usersSyncService.jobInfo.getErrorRecords() + 1);
                try
                {
                    usersSyncService.jobInfo.setLastId(userId);
                    usersSyncService.updateLastSyncedUserStatus();
                }
                catch (JsonProcessingException e1)
                {
                    // TODO Auto-generated catch block
                    LOGGER.error("Failure for json processing= " + userId, e1);
                }
                LOGGER.error("Failure for UserId= " + userId, e);
                //TO-DO retry logic if neccessary
            }
        }
    }
    
    private void pushuser(ElasticSearchUser esUser) throws JsonProcessingException{
        String id = String.valueOf(esUser.getId()) + esUser.getAccessCode();
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse
        String json = mapper.writeValueAsString(esUser);
        elasticSearchClient.prepareIndex(UsersSyncService.USERS_INDEX, UsersSyncService.USERS_TYPE, id).setSource(json).execute().actionGet();
    }
    
    private User populateUser(String userId)
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
                    
                    DateFormat dateFormat = new SimpleDateFormat(Helper.DATE_FORMAT);
                    
                    if (rs.getTimestamp("createdAt") != null) {
                        user.setDateCreated(dateFormat.format(rs.getTimestamp("createdAt").getTime())); 
                    } 
                    
                    if (rs.getLong("lastLoginAt") != 0) {
                        user.setDateLastLogin(dateFormat.format(rs.getLong("lastLoginAt")));
                    } 
                    
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
    
    private List<AccessCode> populateAccessCodes(long userId)
    {
        List<AccessCode> accessCode = jdbcTemplate.query(
            "select d.name as Discipline,bl.name as ProductName,SUBSTRING(ar.feature,11) as ProductCode,ba.AccessCode,ba.LastModified from accessrights as ar, bookaccesscodes as ba,booklist as bl, discipline as d where d.Abbr = bl.discipline and bl.Abbr=SUBSTRING(ar.feature,11) and ar.userId=ba.userId and ba.userId=? and ba.BookAbbr like CONCAT(SUBSTRING(ar.feature,11),'%') order by ba.LastModified", new Object[] {userId},
            new RowMapper<AccessCode>() {
                @Override
                public AccessCode mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AccessCode accessCode = new AccessCode();
                    accessCode.setCode(rs.getString("ProductCode") + "-" + rs.getString("AccessCode"));
                    
                    DateFormat dateFormat = new SimpleDateFormat(Helper.DATE_FORMAT);
                    if (rs.getTimestamp("LastModified") != null) {
                        accessCode.setDateCreated(dateFormat.format(rs.getTimestamp("LastModified")));
                    } 
                    
                    accessCode.setProductCode(rs.getString("ProductCode"));
                    accessCode.setProductName(rs.getString("ProductName"));
                    accessCode.setDiscipline(rs.getString("Discipline"));
                    return accessCode;
                }
            });
        return accessCode;
    }
    
    private List<String> populateCourses(long userId)
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


    private Institution populateInstitution(String institutionId){
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
}
