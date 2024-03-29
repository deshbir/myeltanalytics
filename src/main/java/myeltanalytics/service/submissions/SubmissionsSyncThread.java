package myeltanalytics.service.submissions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import myeltanalytics.model.ActivitySubmission;
import myeltanalytics.model.ActivitySubmission.Activity;
import myeltanalytics.model.ActivitySubmission.Book;
import myeltanalytics.model.Constants;
import myeltanalytics.model.Institution;
import myeltanalytics.service.ApplicationContextProvider;

import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class SubmissionsSyncThread implements Runnable
{
    private JdbcTemplate jdbcTemplate = (JdbcTemplate)ApplicationContextProvider.getApplicationContext().getBean("jdbcTemplate");
    
    private Client elasticSearchClient = (Client)ApplicationContextProvider.getApplicationContext().getBean("elasticSearchClient");
    
    private SubmissionsSyncService submissionsSyncService = (SubmissionsSyncService)ApplicationContextProvider.getApplicationContext().getBean("submissionsSyncService");
    
    private final Logger LOGGER = Logger.getLogger(SubmissionsSyncThread.class);
    
    private String submissionId;
    
    public SubmissionsSyncThread(String submissionId) {
        this.submissionId = submissionId;
    }
    
    @Override
    public void run() {
        if(SubmissionsSyncService.jobInfo != null && !(SubmissionsSyncService.jobInfo.getJobStatus().equals(Constants.STATUS_PAUSED))){
            try
            {
                //LOGGER.debug("Starting sync for submission with SubmissionId= " + submissionId);
                ActivitySubmission activitySubmission = populateSubmission(submissionId);
                ObjectMapper mapper = new ObjectMapper(); // create once, reuse
                String json = mapper.writeValueAsString(activitySubmission);
                elasticSearchClient.prepareIndex(Constants.SUBMISSIONS_INDEX, Constants.SUBMISSIONS_TYPE, submissionId).setSource(json).execute().actionGet();
                SubmissionsSyncService.jobInfo.incrementSuccessRecords();
                SubmissionsSyncService.jobInfo.setLastIdentifier(submissionId);
                submissionsSyncService.updateLastSyncedSubmissionStatus();
                //LOGGER.debug("Submssion with SubmissionId= " + submissionId + " synced successfully");
            }
            catch(Exception e){
                //e.printStackTrace();
                SubmissionsSyncService.jobInfo.incrementErrorRecords();
                try
                {
                    SubmissionsSyncService.jobInfo.setLastIdentifier(submissionId);
                    submissionsSyncService.updateLastSyncedSubmissionStatus();
                }
                catch (JsonProcessingException e1)
                {
                    // TODO Auto-generated catch block
                    LOGGER.error("Failure for json processing= " + submissionId, e1);
                }
                LOGGER.error("Failure for SubmissionId= " + submissionId, e);
                //TO-DO retry logic if neccessary
            }
        }
    }
    
    private ActivitySubmission populateSubmission(String activitySubmissionId)
    {
        ActivitySubmission activitySubmission = jdbcTemplate.queryForObject(
            "Select id,CompletedAt,Score,PossibleScore,assignmentId,userId,ProgressSaved,TimeSpent from AssignmentResults where id = ?", new Object[] { activitySubmissionId },
            new RowMapper<ActivitySubmission>() {
                @Override
                public ActivitySubmission mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ActivitySubmission activitySubmission = new ActivitySubmission(rs.getLong("id"));
                    
                    DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
                    
                    if (rs.getTimestamp("CompletedAt") != null) {
                        activitySubmission.setDateSubmitted(dateFormat.format(rs.getTimestamp("CompletedAt").getTime())); 
                    } 
                    activitySubmission.setSyncJobId(SubmissionsSyncService.jobInfo.getJobId());
                    activitySubmission.setStudentScore(rs.getDouble("Score"));
                    activitySubmission.setMaxScore(rs.getDouble("PossibleScore"));
                    activitySubmission.setProgressSaved(rs.getInt("ProgressSaved"));
                    activitySubmission.setTimeSpent(rs.getInt("TimeSpent"));
                    activitySubmission.setActivity(populateActivity(rs.getLong("assignmentId")));
                    activitySubmission.setUser(populateUserForSubmission(rs.getLong("userId")));
                    return activitySubmission;
                }
            });
        return activitySubmission;
    }

    protected ActivitySubmission.User populateUserForSubmission(long userId)
    {
        ActivitySubmission.User user = jdbcTemplate.queryForObject(
            "Select id,firstName,lastName,country,InstitutionID,DistrictID from Users where id = ?", new Object[] { userId },
            new RowMapper<ActivitySubmission.User>() {
                @Override
                public ActivitySubmission.User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ActivitySubmission.User user = new ActivitySubmission.User(rs.getLong("id"));
                    user.setFirstName(rs.getString("firstName"));
                    user.setLastName(rs.getString("lastName"));
                    user.setCountry(rs.getString("country"));
                    user.setInstitution(populateInstitution(rs.getString("InstitutionID"), rs.getString("DistrictID")));
                    return user;
                }
            });
        return user;
    }
   
    protected Activity populateActivity(long assignmentId)
    {
        Activity activity = jdbcTemplate.queryForObject(
            "Select id,name,NumRetakes,AssignmentType,AssignmentData from Assignments where id = ?", new Object[] { assignmentId },
            new RowMapper<Activity>() {

                @Override
                public Activity mapRow(ResultSet rs, int rowNum) throws SQLException
                {
                    Activity assignment = new Activity(rs.getLong("id"));
                    assignment.setName(rs.getString("name"));
                    assignment.setMaxTakesAllowed(rs.getInt("NumRetakes"));
                    assignment.setActivityType(rs.getInt("AssignmentType"));
                    assignment.setAssignmentData(rs.getString("AssignmentData"));
                    assignment.setBook(populateBookDetails(rs.getString("AssignmentData")));
                    return assignment;
                }
            
            });
        return activity;
    }
    
    protected Book populateBookDetails(String assignmentData)
    {
        if(!assignmentData.equals(Constants.BLANK)){
            int startIndex = assignmentData.indexOf("book=") + 5;
            int endIndex = assignmentData.indexOf("&",startIndex);
            String bookAbbr = assignmentData.substring(startIndex, endIndex);
            Book book = jdbcTemplate.queryForObject(
                "Select b.abbr,b.name,d.name from BookList as b inner join Discipline as d on b.discipline= d.abbr where b.abbr = ?", new Object[] { bookAbbr },
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

    protected Institution populateInstitution(final String institutionId, String districtId){
    	/** Populate Institutions from Main DB (Institutions and Districts table are in Main DB) */
        Institution  institution = jdbcTemplate.queryForObject(
            "Select id,name,country,other from Institutions where id=?", new Object[] { institutionId },
            new RowMapper<Institution>() {
                @Override
                public Institution mapRow(ResultSet rs, int rowNum) throws SQLException {                                      
                    return new Institution(institutionId,rs.getString("name"), rs.getString("country"), rs.getString("other"));
                }
            });
        return institution;
    }
    
}
