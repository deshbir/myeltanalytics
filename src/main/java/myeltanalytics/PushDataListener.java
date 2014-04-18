package myeltanalytics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
            String json = mapper.writeValueAsString(user);
            elasticSearchClient.prepareIndex(event.getIndex(),event.getType(),String.valueOf(event.getId())).setSource(json).execute().actionGet();
            setLastUserStatus(event.getId());
            //System.out.println("User with UserId= " + event.getId() + " pushed successfully");
            LOGGER.debug("User with UserId= " + event.getId() + " pushed successfully");
        }
        catch(Exception e){
            //e.printStackTrace();
            LOGGER.error("Failure for UserId= " + event.getId(), e);
            //TO-DO retry logic if neccessary
        }
    }
    
    @Subscribe
    @AllowConcurrentEvents
    public void onPushSubmissionEvent(PushSubmissionEvent event) {
        try
        {
            ActivitySubmission activitySubmission = populateSubmission(event.getId());
            ObjectMapper mapper = new ObjectMapper(); // create once, reuse
            String json = mapper.writeValueAsString(activitySubmission);
            elasticSearchClient.prepareIndex(event.getIndex(),event.getType(),String.valueOf(event.getId())).setSource(json).execute().actionGet();
            setLastActivitySubmissionStatus(event.getId());
            //System.out.println("Submission with SubmissionId= " + event.getId() + " pushed successfully");
            LOGGER.debug("Submission with SubmissionId= " + event.getId() + " pushed successfully");
        }
        catch(Exception e){
            //e.printStackTrace();
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
        if(!assignmentData.equals(MainController.BLANK)){
            int startIndex = assignmentData.indexOf("book=") + 5;
            int endIndex = assignmentData.indexOf("&",startIndex);
            String bookAbbr = assignmentData.substring(startIndex, endIndex);
            Book book = jdbcTemplate.queryForObject(
                "select b.abbr,b.name,d.name from booklist as b inner join discipline as d on b.discipline= d.abbr where b.abbr = ?", new Object[] { bookAbbr },
                new RowMapper<Book>() {
    
                    @Override
                    public Book mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        Book book = new Book();
                        book.setName(rs.getString("b.name"));
                        book.setAbbr(rs.getString("b.abbr"));
                        book.setDiscipline(rs.getString("d.name"));
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
            "select d.name as Discipline,bl.name as ProductName,SUBSTRING(ar.feature,11) as ProductCode,ba.AccessCode,ba.LastModified from accessrights as ar, bookaccesscodes as ba,booklist as bl, discipline as d where d.Abbr = bl.discipline and bl.Abbr=SUBSTRING(ar.feature,11) and ar.userId=? and ba.userId=? and ba.BookAbbr like CONCAT(SUBSTRING(ar.feature,11),'%') order by ba.LastModified", new Object[] { userId },
            new RowMapper<AccessCode>() {
                @Override
                public AccessCode mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AccessCode accessCode = new AccessCode();
                    accessCode.setCode(rs.getString("ProductName") + "-" + rs.getString("AccessCode"));
                    accessCode.setDateCreated(rs.getDate("LastModified"));
                    accessCode.setProductCode(rs.getString("ProductCode"));
                    accessCode.setProductName(rs.getString("ProductName"));
                    accessCode.setDiscipline(rs.getString("Discipline"));
                    return accessCode;
                }
            });
        return accessCode;
    }


//    protected List<String> populateProducts(long userId)
//    {
//        List<String> products = jdbcTemplate.query(
//            "select SUBSTRING(feature,11) as feature from accessrights where feature like 'book-view-%' and feature != 'book-view-ALL' and userId = ?", new Object[] { userId },
//            new RowMapper<String>() {
//                @Override
//                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
//                    return rs.getString("feature");
//                }
//            });
//        return products;
//    }
//    
//    protected List<String> populateDisciplines(long userId)
//    {
//        List<String> disciplines = jdbcTemplate.query(
//            "select distinct discipline.name as discipline from accessrights,booklist,discipline where discipline.abbr = booklist.discipline and booklist.abbr = SUBSTRING(accessrights.feature,11) and accessrights.feature like 'book-view-%' and accessrights.feature != 'book-view-ALL' and accessrights.userId = ?", new Object[] { userId },
//            new RowMapper<String>() {
//                @Override
//                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
//                    return rs.getString("discipline");
//                }
//            });
//        return disciplines;
//    }
    
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
                    return new Institution(rs.getString(1),rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
                }
            });
        return institution;

    }
    
    synchronized void setLastUserStatus(long userStatus) throws JsonProcessingException{
        MainController.LAST_USER_ID = userStatus;
        String json = "{\"id\": " + userStatus + "}";
        elasticSearchClient.prepareIndex(MainController.MYELT_ANALYTICS_INDEX, MainController.MYELT_ANALYTICS_TYPE, "lastUserId").setSource(json).execute().actionGet();
    }
    
    synchronized void setLastActivitySubmissionStatus(long activitySubmissionStatus){
        MainController.LAST_ACTIVITY_SUBMISSION_ID = activitySubmissionStatus;
        String json = "{\"id\": " + activitySubmissionStatus + "}";
        elasticSearchClient.prepareIndex(MainController.MYELT_ANALYTICS_INDEX, MainController.MYELT_ANALYTICS_TYPE, "lastActivitySubmissionId").setSource(json).execute().actionGet();
    }
    
}
