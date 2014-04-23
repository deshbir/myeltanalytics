package myeltanalytics.service.listener;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.PostConstruct;

import myeltanalytics.controller.SubmissionsSyncController;
import myeltanalytics.model.ActivitySubmission;
import myeltanalytics.model.ActivitySubmission.Assignment;
import myeltanalytics.model.ActivitySubmission.Book;
import myeltanalytics.model.Institution;
import myeltanalytics.model.SyncSubmissionEvent;
import myeltanalytics.service.Helper;

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


@Service(value="submissionsSyncListener")
public class SubmissionsSyncListener
{
    private final Logger LOGGER = Logger.getLogger(SubmissionsSyncListener.class);
    
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
    public void onSyncSubmissionEvent(SyncSubmissionEvent event) {
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
        if(!assignmentData.equals(Helper.BLANK)){
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
    
    synchronized void setLastActivitySubmissionStatus(long activitySubmissionStatus){
        SubmissionsSyncController.LAST_ACTIVITY_SUBMISSION_ID = activitySubmissionStatus;
        String json = "{\"id\": " + activitySubmissionStatus + "}";
        elasticSearchClient.prepareIndex(Helper.MYELT_ANALYTICS_INDEX, Helper.SUBMISSIONS_SYNC_JOB_TYPE, "lastActivitySubmissionId").setSource(json).execute().actionGet();
    }
    
}
