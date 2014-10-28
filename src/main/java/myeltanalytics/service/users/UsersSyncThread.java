package myeltanalytics.service.users;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import myeltanalytics.model.Access;
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
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
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
    
    private JdbcTemplate auxJdbcTemplate;
    
    public UsersSyncThread(String loginName, String institutionId) {
        this.loginName = loginName;
        this.institutionId = institutionId;
    }
    
    @Override
    public void run() {
        if (UsersSyncService.jobInfo != null && (!(UsersSyncService.jobInfo.getJobStatus().equals(Constants.STATUS_PAUSED)))) {
            
        	User user = null;        	
        	ElasticSearchUser esUser = null;        	
        	SyncInfo syncInfo = new SyncInfo();        	
        	syncInfo.setJobId(UsersSyncService.jobInfo.getJobId());
        	
        	UsersSyncService.jobInfo.setLastIdentifier(loginName);
        	
            //LOGGER.debug("Starting sync for user with LoginName= " + loginName + ", InstitutionId= " + institutionId);
            try {
                // Getting user's data from MySQL database
            	user = populateUser(loginName, institutionId);
            } catch(Exception e) {
                /*********************************************************************************************************
                 * Add an Error User Record in ElasticSearch if any error occurs while reading user's data from MySQL.
                 *********************************************************************************************************/
                LOGGER.error("Failure while reading data from MySQL for LoginName= " + loginName, e);                
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
        		    if(UsersSyncService.jobInfo.getJobStatus().equals(Constants.STATUS_INPROGRESS_RETRY)) {
        		    	UsersSyncService.jobInfo.incrementRetryRecordsProcessed();
        		    } else {
        		    	UsersSyncService.jobInfo.incrementErrorRecords();
        		    }
            		
            		usersSyncService.updateLastSyncedUserStatus();
            	} catch (Exception ex)  {
                    LOGGER.error("Failure while pushing to elasticsearch for LoginName= " + loginName, ex);
                }       		
            	return;
            }
            try {
                syncInfo.setStatus("Success");
                user.setSyncInfo(syncInfo);
            
                List<Access> accessList = user.getAccessList();           
            	
        		if(accessList.size() == 0){
        			esUser  = ElasticSearchUser.transformUser(user,null,"USER_WITHOUT_ACCESS");
        			pushuser(esUser);
        		}
        		else {
        			IndexRequestBuilder indexedEsUser;
        			BulkRequestBuilder bulkRequest = elasticSearchClient.prepareBulk();
        			for(int i = 0 ;i < accessList.size(); i++){
        				Access access  =  accessList.get(i);
        				String recordType = null; 
        				if(i == 0){
        					recordType = "USER_WITH_ACCESS";
        				} else {
        					recordType = "ADDITIONAL_ACCESS";
        				}
        				esUser = ElasticSearchUser.transformUser(user, access, recordType);
        				indexedEsUser =  perpareIndexedEsUser(esUser);
        				bulkRequest.add(indexedEsUser);
        				if(i == (accessList.size() -1)) {
        					bulkRequest.execute().actionGet();
        				}
        			}
        			
        		}
        		if (UsersSyncService.jobInfo.getJobStatus().equals(Constants.STATUS_INPROGRESS_RETRY)) {
        			UsersSyncService.jobInfo.decrementErrorRecords();
        			UsersSyncService.jobInfo.incrementRetryRecordsProcessed();
        		}
        		UsersSyncService.jobInfo.incrementSuccessRecords();
        		
            }
            catch (Exception e) {
                LOGGER.error("Failure while pushing to elasticsearch for LoginName= " + loginName, e);
                if (UsersSyncService.jobInfo.getJobStatus().equals(Constants.STATUS_INPROGRESS_RETRY)) {
                	UsersSyncService.jobInfo.incrementRetryRecordsProcessed();
                } else {
                	UsersSyncService.jobInfo.incrementErrorRecords();
                }
            }
            
        	try
            {
                usersSyncService.updateLastSyncedUserStatus();
            }
            catch (JsonProcessingException e1)
            {                	
                LOGGER.error("Failure for json processing= " + loginName, e1);
            }
        }
    }
    
    private void pushuser(ElasticSearchUser esUser) throws JsonProcessingException{
//        String elasticSearchID = String.valueOf(esUser.getUserName());
//        if (esUser.getAccess() != null ) {
//        	if (esUser.getAccess().getCode() != null ) {
//        		elasticSearchID = elasticSearchID + esUser.getAccess().getProductCode()+ esUser.getAccess().getCode();
//        	} else {
//        		elasticSearchID = elasticSearchID + esUser.getAccess().getProductCode();
//        	}
//        }
        ObjectMapper mapper = new ObjectMapper(); 
        String json = mapper.writeValueAsString(esUser);
        elasticSearchClient.prepareIndex(Constants.USERS_INDEX, Constants.USERS_TYPE).setSource(json).execute().actionGet();
    }
    
    private IndexRequestBuilder perpareIndexedEsUser (ElasticSearchUser esUser)throws JsonProcessingException{
    	String elasticSearchID = String.valueOf(esUser.getUserName());
        if (esUser.getAccess() != null ) {
        	if (esUser.getAccess().getCode() != null ) {
        		elasticSearchID = elasticSearchID + esUser.getAccess().getProductCode()+ esUser.getAccess().getCode();
        	} else {
        		elasticSearchID = elasticSearchID + esUser.getAccess().getProductCode();
        	}
        }
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(esUser);
        IndexRequestBuilder indexedEsUser =  elasticSearchClient.prepareIndex(Constants.USERS_INDEX, Constants.USERS_TYPE, elasticSearchID).setSource(json);
        return indexedEsUser;
    }
    
    private User populateUser(String loginName, String institutionId)
    {
        /**
         * UserInstitutionMap handling
         * 1. Instead of default jdbcTemplate (which maps to the Master DB), use institution DB specific jdbcTemplate
         * 2. Select DatabaseURL from institutions where Id=instId
         * 3. Use DatabaseURl to lookup URL for dataSource.
         * 4. Make a jdbcTemplate and query it
         */       
        String dbURL = jdbcTemplate.queryForObject("Select DatabaseURL from Institutions where ID = ?", new Object[]{institutionId}, String.class);
        auxJdbcTemplate = usersSyncService.getJdbcTemplate(dbURL);
        
        /********************************************************
         * 
         * Populate users from Aux DB (Users table is in Aux DB)
         * 
         *********************************************************
         * We found 2 users on reporting server with following names
         * 	1. student
         * 	2. instructor
         * When we query for "student", the query sometimes returns default "STUDENT" user with parent=0 (since query is case insensitive).
         * Adding a check parent<>0 to avoid such situation
         ********************************************************/
        User user = auxJdbcTemplate.queryForObject(
            "Select id,name,email,parent,createdAt,lastLoginAt,firstName,lastName,countryCode,InstitutionID from Users where name = ? AND parent<>0 limit 1", new Object[] { loginName },
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
                    user.setAccessList(populateAccessList(user));
                    
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
        //Add databaseURL as a field in USER ES record.
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
         
        /** Populate milestones from Aux DB (MyeltWorkflowMilestoness table is in Aux DB) */
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
    
    private List<Access> populateAccessList(User user)
    {
    	List<Access> accessList = new ArrayList<Access>();

    	/** AccessRights table is in Aux DB */
    	List<Map<String,Object>> accessRights = auxJdbcTemplate.queryForList("Select SUBSTRING(Feature,11) as ProductCode, LastModified from AccessRights where UserId="+user.getId()+" And Feature like 'book-view-%' And Feature <> 'book-view-ALL' AND AccessLevel>0 order by LastModified");
    	Iterator<Map<String,Object>> accessRightsIter = accessRights.iterator();
    	while(accessRightsIter.hasNext()) {
    		Access access = new Access();
    		Map<String,Object> accessRight = accessRightsIter.next();
    		String productCode = String.valueOf(accessRight.get("ProductCode"));
    		String lastModified = String.valueOf(accessRight.get("LastModified"));
    		access.setProductCode(productCode);
    		access.setDateCreated(lastModified);

    		List<Map<String,Object>> accessCodeList = jdbcTemplate.queryForList("Select AccessCode from BookAccessCodes where UserId='" + user.getId() + "' And BookAbbr like '" + productCode + "%'");

    		Map<String,String> bookInfo = usersSyncService.getBookInfo(productCode);
    		
    		if (accessCodeList != null && accessCodeList.size() > 0) {
    			Map<String,Object> accessCode = accessCodeList.get(0);
    			access.setCode(String.valueOf(accessCode.get("AccessCode")));
    			access.setAccessType(Constants.ACCESSTYPE_ACCESSCODE);
    		} else {
    			access.setAccessType(Constants.ACCESSTYPE_ACCESSRIGHT);
    		}
    		
    		if (bookInfo != null) {
    			access.setProductName(String.valueOf(bookInfo.get(Constants.PRODUCTNAME)));
        		access.setDiscipline(String.valueOf(bookInfo.get(Constants.DISCIPLINE)));
    		}
    		
    		accessList.add(access);
    	}
    	List<Map<String,Object>> accessRightsByInstitutionId = auxJdbcTemplate.queryForList("Select SUBSTRING(Feature,11) as ProductCode, LastModified from AccessRights where UserId ="+getUserTypeIdFromUserType(user.getUserType())+" AND InstitutionID = '"+user.getInstitution().getId()+"' And Feature like 'book-view-%' And Feature <> 'book-view-ALL' AND AccessLevel > 0");
    	Iterator<Map<String,Object>> accessRightsByInstitutionIdIter = accessRightsByInstitutionId.iterator();
    	while(accessRightsByInstitutionIdIter.hasNext()) {
    		Access access = new Access();
    		Map<String,Object> accessRight = accessRightsByInstitutionIdIter.next();
    		String productCode = String.valueOf(accessRight.get("ProductCode"));
    		String lastModified = String.valueOf(accessRight.get("LastModified"));
    		access.setProductCode(productCode);
    		access.setDateCreated(lastModified);
    		access.setAccessType(Constants.ACCESSTYPE_INSTITUTION);
    		Map<String,String> bookInfo = usersSyncService.getBookInfo(productCode);
    		if (bookInfo != null) {
    			access.setProductName(String.valueOf(bookInfo.get(Constants.PRODUCTNAME)));
        		access.setDiscipline(String.valueOf(bookInfo.get(Constants.DISCIPLINE)));
    		}
    		accessList.add(access);
    	}

    	return accessList;
    }
    
   
    private List<String> populateCourses(long userId)
    {
        /** Populate courses from Aux DB (Sections and SectionMembers tables are in Aux DB) */
        List<String> courses = auxJdbcTemplate.query(
            "Select name from Sections, SectionMembers where Sections.id=SectionMembers.sectionId and SectionMembers.userId = ?", new Object[] { userId },
            new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString("name");
                }
            });
        return courses;
    }
   
    private Institution populateInstitution(String institutionId) {   
        /** Populate Institutions from Main DB (Institutions and Districts table are in Main DB) */
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
    
    /**
     * Function to create EsUser which is not in sync with MySQL user  
     * @param syncInfo SyncInfo
     * @return ElasticSearchUser
     */
    private ElasticSearchUser getErrorEsUser(SyncInfo syncInfo){
    	ElasticSearchUser esUser = new ElasticSearchUser();
    	ElasticSearchInstitution elasticSearchInstitution = new ElasticSearchInstitution(institutionId);
		esUser.setUserName(loginName);
		esUser.setInstitution(elasticSearchInstitution);
		esUser.setSyncInfo(syncInfo);
		esUser.setRecordType(Constants.USER_ERROR);
		return esUser;
    }
    
    private int getUserTypeIdFromUserType(String userType){
    	int userParent = 1;
    	switch (userType) {
        	case "STUDENT": {
        		userParent = 1;
        		break;
        	}
        	case "INSTRUCTOR": {
        		userParent = 2;
        		break;
        	}
        	case "AUTHOR":  {
        		userParent = 3;
        		break;
        	}
        	case "ADMINISTRATOR":  {
        		userParent = 4;
        		break;
        	}
        	case "DISTRICT":  {
        		userParent = 5;
        		break;
        	}
        	case "SUPERUSER":  {
        		userParent = 6;
        		break;
        	}
       }
   	   return userParent;
    }
}