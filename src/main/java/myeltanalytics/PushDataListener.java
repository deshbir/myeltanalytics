package myeltanalytics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;


@Service(value="pushDataListener")
public class PushDataListener
{
    public static Map<Long,Status> USER_POSTED_STATUS_MAP = new HashMap<Long,Status>();
    private final Logger LOGGER = Logger.getLogger(PushDataListener.class);
    
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
    public void onEvent(PushObjectEvent event) {
        try
        {
            User user = populateUser(event.getId());
            ObjectMapper mapper = new ObjectMapper(); // create once, reuse
            String json;
            json = mapper.writeValueAsString(user);
            elasticSearchClient.prepareIndex(event.getIndex(),event.getDocument(),String.valueOf(event.getId())).setSource(json).execute().actionGet();
            USER_POSTED_STATUS_MAP.put(user.getId(), Status.SUCCESS);
            System.out.println("User with UserId= " + event.getId() + " pushed successfully");
            LOGGER.debug("User with UserId= " + event.getId() + " pushed successfully");
        }
        catch(Exception e){
            e.printStackTrace();
            LOGGER.error("Failure for UserId= " + event.getId(), e);
            //TO-DO retry logic if neccessary
            USER_POSTED_STATUS_MAP.put(event.getId(), Status.FAILURE);
        }
        
    }


    private User populateUser(long userId)
    {
       User user = jdbcTemplate.queryForObject(
            "select id,name,email,parent,createdAt,lastLoginAt,firstName,lastName,country,InstitutionID from users where id = ?", new Object[] { userId },
            new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = new User(rs.getLong("id"));
                    user.setUserName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setUserType(rs.getInt("parent"));
                    user.setDateCreated(rs.getDate("createdAt"));
                    user.setDateLastLogin(rs.getLong("lastLoginAt"));
                    user.setFirstName(rs.getString("firstName"));
                    user.setLastName(rs.getString("lastName"));
                    user.setCountry(rs.getString("country"));
                    user.setInstituion(populateInstitution(rs.getString("InstitutionID")));
                    user.setProducts(populateProducts(user.getId()));
                    user.setCourses(populateCourses(user.getId()));
                    user.setAccesscodes(populateAccessCodes(user.getId()));
                    return user;
                }
            });
        return user;
    }
    
    protected List<String> populateAccessCodes(long userId)
    {
        List<String> accessCodes = jdbcTemplate.query(
            "select BookAbbr,AccessCode from bookaccesscodes where UserID = ?", new Object[] { userId },
            new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return (rs.getString("BookAbbr") + "365-" + rs.getString("AccessCode"));
                }
            });
        return accessCodes;
    }


    protected List<String> populateProducts(long userId)
    {
        List<String> products = jdbcTemplate.query(
            "select feature from accessrights where userId = ?", new Object[] { userId },
            new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    String feature  = rs.getString("feature");
                    if (feature != null && !feature.equals("") && feature.startsWith("book-view-")) {
                        return feature.substring(10);
                    }
                    return feature;
                }
            });
        return products;
    }
    
    protected List<String> populateCourses(long userId)
    {
        List<String> courses = jdbcTemplate.query(
            "select name from sections where id IN (select SectionID from sectionmembers where UserID = ?)", new Object[] { userId },
            new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("name");
                }
            });
        return courses;
    }


    protected User.Institution populateInstitution(String institutionId){
        User.Institution  institution = jdbcTemplate.queryForObject(
            "select id,name from institutions where id = ?", new Object[] { institutionId },
            new RowMapper<User.Institution>() {
                @Override
                public User.Institution mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new User.Institution(rs.getString("id"),rs.getString("name"));
                }
            });
        return institution;

    }
    
}
