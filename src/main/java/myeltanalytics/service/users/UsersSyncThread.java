package myeltanalytics.service.users;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import myeltanalytics.model.AccessCode;
import myeltanalytics.model.Constants;
import myeltanalytics.model.ElasticSearchInstitution;
import myeltanalytics.model.ElasticSearchUser;
import myeltanalytics.model.Institution;
import myeltanalytics.model.Milestone;
import myeltanalytics.model.MilestoneInfo;
import myeltanalytics.model.SyncInfo;
import myeltanalytics.model.User;
import myeltanalytics.service.ApplicationContextProvider;
import myeltanalytics.service.HelperService;

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
    
    private String loginName;
    
    private String institutionId;
    
    private boolean isErrorRecord;
    private JdbcTemplate auxJdbcTemplate;
    
    public UsersSyncThread(String loginName, String institutionId , boolean isErrorRecord) {
        this.loginName = loginName;
        this.institutionId = institutionId;
        this.isErrorRecord = isErrorRecord; 
    }
    
    @Override
    public void run() {
        if (UsersSyncService.jobInfo != null && !(UsersSyncService.jobInfo.getJobStatus().equals(Constants.STATUS_PAUSED))) {
            
        	User user = null;        	
        	ElasticSearchUser esUser = null;        	
        	SyncInfo syncInfo = new SyncInfo();
        	
            //LOGGER.debug("Starting sync for user with LoginName= " + loginName + ", InstitutionId= " + institutionId);
            try {
                // Getting user's data from MySQL database
            	user = populateUser(loginName, institutionId);
            } catch(Exception e) {
                /*********************************************************************************************************
                 * Add an Error User Record in ElasticSearch if any error occurs while reading user's data from MySQL.
                 *********************************************************************************************************/
                LOGGER.error("Failure while reading data from MySQL for LoginName= " + loginName, e);                
            	syncInfo.setJobId(UsersSyncService.jobInfo.getJobId());
            	syncInfo.setMessage(e.getMessage());
            	StringWriter sw = new StringWriter();
            	PrintWriter pw = new PrintWriter(sw);
            	e.printStackTrace(pw);
            	syncInfo.setStacktrace(sw.toString());
            	syncInfo.setExceptionClasss(e.getClass().getSimpleName());            	
            	syncInfo.setStatus("Error");
        		esUser = getErrorEsUser(syncInfo);      		
            	
        		try {
        		    pushuser(esUser);
        		    if(!isErrorRecord){
        		    	UsersSyncService.jobInfo.incrementErrorRecords();
        		    }else{
        		    	UsersSyncService.jobInfo.incrementFailedUserProcessed();
        		    }
            		UsersSyncService.jobInfo.setLastIdentifier(loginName);
            		usersSyncService.updateLastSyncedUserStatus();
            	} catch (Exception ex)  {
                    LOGGER.error("Failure while pushing to elasticsearch for LoginName= " + loginName, ex);
                }       		
            	return;
            }
            try {
                syncInfo.setStatus("Success");
                syncInfo.setJobId(UsersSyncService.jobInfo.getJobId());
                user.setSyncInfo(syncInfo);
            
                List<AccessCode> accessCodes = user.getAccesscodes();           
            	
        		if(accessCodes.size() ==0){
        			esUser  = ElasticSearchUser.transformUser(user,null,"USER_WITHOUT_ACCESSCODE");
        			pushuser(esUser);
        		}
        		else {
        			for(int i = 0 ;i < accessCodes.size(); i++){
        				AccessCode accessCode  =  accessCodes.get(i);
        				String userType = null; 
        				if(i == 0){
        					userType = "USER_WITH_ACCESSCODE";
        				} else {
        					userType = "ADDITIONAL_ACCESSCODE";
        				}
        				esUser = ElasticSearchUser.transformUser(user, accessCode, userType);
        				pushuser(esUser);
        			}
        		}
        		if(isErrorRecord){
        			UsersSyncService.jobInfo.decrementErrorRecords();
        			UsersSyncService.jobInfo.incrementFailedUserProcessed();
        		}
        		UsersSyncService.jobInfo.incrementSuccessRecords();
        		
            }
            catch (Exception e) {
                LOGGER.error("Failure while pushing to elasticsearch for LoginName= " + loginName, e);
                if(!isErrorRecord){
                	UsersSyncService.jobInfo.incrementErrorRecords();
                }else{
                	UsersSyncService.jobInfo.incrementFailedUserProcessed();
                }
            }
            
            UsersSyncService.jobInfo.setLastIdentifier(loginName);
            
        	try
            {
                usersSyncService.updateLastSyncedUserStatus();
            }
            catch (JsonProcessingException e1)
            {                	// TODO Auto-generated catch block
                LOGGER.error("Failure for json processing= " + loginName, e1);
            }
        }
    }
    
    private void pushuser(ElasticSearchUser esUser) throws JsonProcessingException{
        String loginName = String.valueOf(esUser.getUserName());
        if (esUser.getAccessCode() != null ) {
            loginName = loginName + esUser.getAccessCode().getCode();
        }
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse
        String json = mapper.writeValueAsString(esUser);
        elasticSearchClient.prepareIndex(Constants.USERS_INDEX, Constants.USERS_TYPE, loginName).setSource(json).execute().actionGet();
    }
    
    private User populateUser(String loginName, String institutionId)
    {
        /**
         * UserInstitutionMap handling
         * 1. Instead of default jdbcTemplate (which maps to the Master DB), use institution DB specific jdbcTemplate
         * 2. select DatabaseURL from institutions where Id=instId
         * 3. Use DatabaseURl to lookup URL for dataSource.
         * 4. Make a jdbcTemplate and query it
         * 
         * ---
         * 
         * 1. Add databaseURL as a field in USER ES record.
         */
       
        String dbURL = jdbcTemplate.queryForObject("Select DatabaseURL from Institutions where ID = ?", new Object[]{institutionId}, String.class);
        auxJdbcTemplate = usersSyncService.getJdbcTemplate(dbURL);
        
        User user = auxJdbcTemplate.queryForObject(
            "Select id,name,email,parent,createdAt,lastLoginAt,firstName,lastName,countryCode,InstitutionID from Users where name = ? limit 1", new Object[] { loginName },
            new RowMapper<User>() {
                
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUserName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setUserType(rs.getInt("parent"));
                    
                    DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
                    
                    if (rs.getTimestamp("createdAt") != null) {
                        user.setDateCreated(dateFormat.format(rs.getTimestamp("createdAt").getTime())); 
                    } 
                    
                    if (rs.getLong("lastLoginAt") != 0) {
                        user.setDateLastLogin(dateFormat.format(rs.getLong("lastLoginAt")));
                    } 
                    
                    user.setFirstName(rs.getString("firstName"));
                    user.setLastName(rs.getString("lastName"));
                    
                    user.setInstitution(populateInstitution(rs.getString("InstitutionID"))); 
                    
                    user.setUserCountry(rs.getString("countryCode"));                                     
                    user.setCourses(populateCourses(user.getId()));
                    user.setAccesscodes(populateAccessCodes(user.getId()));
                    
                    /*****************************************
                     *  Populate Milestones for CAPES users
                     ******************************************/
                    if (user.getInstitution().getDistrict() != null && user.getInstitution().getDistrict().equals("CAPES")) {
                        user.setMilestones(populateMilestones(user.getId()));
                        if (user.getMilestones() != null && user.getMilestones().size() > 0) {
                        	//Get last accessed milestone
                            Milestone lastMilestone = user.getMilestones().get(user.getMilestones().keySet().toArray()[0]);
                            user.setLastMilestoneLevel(lastMilestone.getLevel());
                            user.setLastMilestoneId(lastMilestone.getId());
                            user.setLastMilestoneStatus(lastMilestone.getStatus());
                            user.setLastMilestoneAccessedDate(lastMilestone.getAccessedDate());
                            user.setLastMilestoneStartedDate(lastMilestone.getStartedDate());
                            user.setLastMilestoneIsActive(lastMilestone.getIsActive());
                            user.setLastMilestonecompletedDate(lastMilestone.getCompletedDate());
                            user.setLastMilestoneMaxScore(lastMilestone.getMaxScore());
                            user.setLastMilestoneScore(lastMilestone.getScore());
                            //Read some additional milestone info from capes.xml
                            if(lastMilestone.getTestName() != null){
                            	user.setLastMilestoneTestName(lastMilestone.getTestName());
                            	user.setLastMilestoneExpiry(lastMilestone.getExpiry());
                            	user.setLastMilestonePassAction(lastMilestone.getPassAction());
                            	user.setLastMilestonePassPercent(lastMilestone.getPassPercent());
                            }
                        }
                    }
                    return user;
                }
            });
        if (dbURL.equals(".")) {
            dbURL = UsersSyncService.mainDatabaseURL;
        } 
        //cut "jdbc:mysql://" from starting
        dbURL = dbURL.substring(13, dbURL.length());
        if (dbURL.indexOf("?") != -1) {
            dbURL = dbURL.substring(0, dbURL.indexOf("?"));
        }
        user.setDatabaseURL(dbURL);
        return user;
    }
    
    /**
     * Function to get milestone info for CAPES users from MySQL database.
     * @param userId unique id of user(CAPES)
     * @return Milestones Map
     */
    private HashMap<String,Milestone> populateMilestones(long userId)
    {
         
    	List<Milestone> results = auxJdbcTemplate.query(
            "Select MilestoneID,Status,LevelNo,Score,MaxScore,StartedDate,AccessedDate,CompletedDate,IsActive from MyeltWorkflowMilestones where UserID=? order by AccessedDate DESC;", new Object[] { userId },
            new RowMapper<Milestone>() {
                @Override
                public Milestone mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Milestone milestone = new Milestone();
                    DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
                    milestone.setId(rs.getString("MilestoneID"));
                    if (rs.getLong("StartedDate") != 0) {
                        milestone.setStartedDate(dateFormat.format(rs.getLong("StartedDate")));
                    }                    
                    milestone.setLevel(rs.getString("LevelNo"));
                    milestone.setStatus(rs.getString("Status"));
                    if (rs.getLong("AccessedDate") != 0) {
                        milestone.setAccessedDate(dateFormat.format(rs.getLong("AccessedDate")));
                    }
                    if (rs.getLong("CompletedDate") != 0) {
                        milestone.setCompletedDate(dateFormat.format(rs.getLong("CompletedDate")));
                    }
                    milestone.setScore(rs.getString("Score"));
                    milestone.setMaxScore(rs.getString("MaxScore"));
                    milestone.setIsActive(rs.getString("IsActive"));
                    
                    //Get additional milestone info from capes.xml 
                    MilestoneInfo milestoneInfo  = HelperService.milestoneInfo.get(milestone.getId());
                    if(milestoneInfo != null){
                    	milestone.setExpiry(milestoneInfo.getExpiry());
                    	milestone.setTestName(milestoneInfo.getName());
                    	milestone.setPassAction(milestoneInfo.getPassAction());
                    	milestone.setPassPercent(milestoneInfo.getPassPercent());
                    }
                    return milestone;
                }
            });
	        HashMap<String, Milestone> milestones = new LinkedHashMap<String , Milestone>();
	        for(Milestone milestone : results){
	        	milestones.put(milestone.getId(),milestone);
	        }
        return milestones;
    }
    
    private List<AccessCode> populateAccessCodes(long userId)
    {
        List<AccessCode> accessCode = jdbcTemplate.query(
            "Select d.name as Discipline,bl.name as ProductName,SUBSTRING(ar.feature,11) as ProductCode,ba.AccessCode,ba.LastModified from AccessRights as ar, BookAccessCodes as ba,BookList as bl, Discipline as d where d.Abbr = bl.discipline and bl.Abbr=SUBSTRING(ar.feature,11) and ar.userId=ba.userId and ba.userId=? and ba.BookAbbr like CONCAT(SUBSTRING(ar.feature,11),'%') order by ba.LastModified", new Object[] {userId},
            new RowMapper<AccessCode>() {
                @Override
                public AccessCode mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AccessCode accessCode = new AccessCode();
                    accessCode.setCode(rs.getString("ProductCode") + "-" + rs.getString("AccessCode"));
                    
                    DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
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
            "Select name from Sections, SectionMembers where Sections.id=SectionMembers.sectionId and SectionMembers.userId = ?", new Object[] { userId },
            new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("name");
                }
            });
        return courses;
    }

    /** Populate Institutions from Main DB (Institutions and Districts table are in Main DB) */
    private Institution populateInstitution(String institutionId){        
        Institution  institution = jdbcTemplate.queryForObject(
            "Select Institutions.id,Institutions.name,Institutions.country,Institutions.other,Districts.name as district from Institutions left join Districts on Districts.id=Institutions.DistrictID where Institutions.id=?", new Object[] { institutionId },
            new RowMapper<Institution>() {
                @Override
                public Institution mapRow(ResultSet rs, int rowNum) throws SQLException {                                      
                    return new Institution(rs.getString("id"),rs.getString("name"), rs.getString("country"), rs.getString("other"), rs.getString("district"));
                }
            });
        return institution;
    }
    
    //function to create EsUser which is not in sync with MySQL user  
    private ElasticSearchUser getErrorEsUser(SyncInfo syncInfo){
    	ElasticSearchUser esUser = new ElasticSearchUser();
    	ElasticSearchInstitution elasticSearchInstitution = new ElasticSearchInstitution(institutionId);
		esUser.setUserName(loginName);
		esUser.setInstitution(elasticSearchInstitution);
		esUser.setSyncInfo(syncInfo);
		esUser.setRecordType("USER_ERROR");
		return esUser;
    }
}