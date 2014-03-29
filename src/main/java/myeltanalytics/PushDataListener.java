package myeltanalytics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import myeltanalytics.ActivitySubmission.Assignment;
import myeltanalytics.ActivitySubmission.Book;

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
    public void onPushUserEvent(PushUserEvent event) {
        try
        {
            User user = populateUser(event.getId());
            ObjectMapper mapper = new ObjectMapper(); // create once, reuse
            String json;
            json = mapper.writeValueAsString(user);
            elasticSearchClient.prepareIndex(event.getIndex(),event.getDocument(),String.valueOf(event.getId())).setSource(json).execute().actionGet();
            setLastUserStatus(event.getId());
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
    
    
    @Subscribe
    @AllowConcurrentEvents
    public void onPushSubmissionEvent(PushSubmissionEvent event) {
        try
        {
            ActivitySubmission activitySubmission = populateSubmission(event.getId());
            ObjectMapper mapper = new ObjectMapper(); // create once, reuse
            String json;
            json = mapper.writeValueAsString(activitySubmission);
            elasticSearchClient.prepareIndex(event.getIndex(),event.getDocument(),String.valueOf(event.getId())).setSource(json).execute().actionGet();
            setLastActivitySubmissionStatus(event.getId());
            LOGGER.debug("Submission with SubmissionId= " + event.getId() + " pushed successfully");
        }
        catch(Exception e){
            e.printStackTrace();
            LOGGER.error("Failure for assignmentResultID= " + event.getId(), e);
        }
        
    }


    private ActivitySubmission populateSubmission(long activitySubmissionId)
    {
        ActivitySubmission activitySubmission = jdbcTemplate.queryForObject(
            "select id,CompletedAt,Score,PossibleScore,assignmentId,userId from assignmentResults where id = ?", new Object[] { activitySubmissionId },
            new RowMapper<ActivitySubmission>() {
                @Override
                public ActivitySubmission mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ActivitySubmission activitySubmission = new ActivitySubmission(rs.getLong("id"));
                    activitySubmission.setDateSubmitted(rs.getDate("CompletedAt"));
                    activitySubmission.setStudentScore(rs.getDouble("Score"));
                    activitySubmission.setMaxScore(rs.getDouble("PossibleScore"));
                    activitySubmission.setAssignment(populateAssignment(rs.getLong("assignmentId")));
                    activitySubmission.setUser(populateUserForSubmission(rs.getLong("userId")));
                    return activitySubmission;
                }
            });
        return activitySubmission;
    }

    

    protected ActivitySubmission.User populateUserForSubmission(long userId)
    {
        ActivitySubmission.User user = jdbcTemplate.queryForObject(
            "select id,firstName,lastName,country,InstitutionID from users where id = ?", new Object[] { userId },
            new RowMapper<ActivitySubmission.User>() {
                @Override
                public ActivitySubmission.User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ActivitySubmission.User user = new ActivitySubmission.User(rs.getLong("id"));
                    user.setFirstName(rs.getString("firstName"));
                    user.setLastName(rs.getString("lastName"));
                    user.setCountry(rs.getString("country"));
                    user.setInstitution(populateInstitution(rs.getString("InstitutionID")));
                    return user;
                }
            });
        return user;
    }

   
    protected Assignment populateAssignment(long assignmentId)
    {
        Assignment assignment = jdbcTemplate.queryForObject(
            "select id,name,NumRetakes,AssignmentType,AssignmentData from assignments where id = ?", new Object[] { assignmentId },
            new RowMapper<Assignment>() {

                @Override
                public Assignment mapRow(ResultSet rs, int rowNum) throws SQLException
                {
                    Assignment assignment = new Assignment(rs.getLong("id"));
                    assignment.setName(rs.getString("name"));
                    assignment.setMaxTakesAllowed(rs.getInt("NumRetakes"));
                    assignment.setActivityType(rs.getInt("AssignmentType"));
                    assignment.setBook(populateBookDetails(rs.getString("AssignmentData")));
                    return assignment;
                }
            
            });
        return assignment;
    }
    
    
    protected Book populateBookDetails(String assignmentData)
    {
        if(assignmentData.equals(MainController.BLANK)){
            int startIndex = assignmentData.indexOf("book=") + 5;
            int endIndex = assignmentData.indexOf("&",startIndex);
            String bookAbbr = assignmentData.substring(startIndex, endIndex);
            Book book = jdbcTemplate.queryForObject(
                "select abbr,name,discipline from booklist where abbr = ?", new Object[] { bookAbbr },
                new RowMapper<Book>() {
    
                    @Override
                    public Book mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        Book book = new Book();
                        book.setName(rs.getString("name"));
                        book.setAbbr(rs.getString("abbr"));
                        book.setDiscipline(rs.getString("discipline"));
                        return book;
                    }
                
                });
            return book;
        } else {
            return null;
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
                    user.setInstitution(populateInstitution(rs.getString("InstitutionID")));
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
                    return null;
                }
            });
        return products;
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
            "select id,name from institutions where id = ?", new Object[] { institutionId },
            new RowMapper<Institution>() {
                @Override
                public Institution mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Institution(rs.getString("id"),rs.getString("name"));
                }
            });
        return institution;

    }
    
    synchronized void setLastUserStatus(long userStatus) throws JsonProcessingException{
        MainController.LAST_USER_ID = userStatus;
        String json = "{\"id\": " + userStatus + "}";
        elasticSearchClient.prepareIndex("myeltanalytics", "status", "lastUserId").setSource(json).execute().actionGet();
    }
    
    synchronized void setLastActivitySubmissionStatus(long activitySubmissionStatus){
        MainController.LAST_ACTIVITY_SUBMISSION_ID = activitySubmissionStatus;
        String json = "{\"id\": " + activitySubmissionStatus + "}";
        elasticSearchClient.prepareIndex("myeltanalytics", "status", "lastActivitySubmissionId").setSource(json).execute().actionGet();
    }
    
}
