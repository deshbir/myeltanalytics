package myeltanalytics.model;


public class Constants
{
    
    //Configuration XML files
    public static final String COUNTRY_XML_FILE_NAME = "country.xml";    
    public static final String INSTITUTION_XML_FILE_NAME = "institution.xml";
    public static final String CAPES_FILE_NAME = "capes.xml";
    
    //myelt_analytics index constants    
    public static final String MYELT_ANALYTICS_INDEX = "myelt_analytics";    
    public static final String LAST_JOB_ID = "lastJobId";
    public static final String USERS_JOB_STATUS = "usersJobStatus";
    public static final String SUBMISSIONS_JOB_STATUS = "submissionsJobStatus";
    
    //Job Status constants
    public static final String TOTAL_RECORDS = "totalRecords";    
    public static final String SUCCESSFULL_RECORDS = "successRecords";    
    public static final String ERROR_RECORDS = "errorRecords";    
    public static final String LAST_IDENTIFIER = "lastIdentifier";
    public static final String JOB_STATUS = "jobStatus";
    public static final String START_DATETIME = "startDateTime";    
    public static final String SYNC_JOB_ID = "syncJobId";    
    public static final String STATUS_INPROGRESS = "InProgress";
    public static final String STATUS_PAUSED = "Paused";
    public static final String STATUS_COMPLETED = "Completed";
    
    //Users constants
    public static final String USER_WITH_ACCESSCODE = "user_with_accesscode";
    public static final String USER_WITHOUT_ACCESSCODE = "user_without_accesscode";
    public static final String ADDITIONAL_ACCESSCODE = "additional_accesscode";
    public static final String USER_ERROR = "user_error";
    public static final String CAPES_MODEL = "capes_model";
    public static final String ICPNA_INSTITUTION = "ICPNA";
    public static final String SEVEN_INSTITUTION = "7";
    public static final String USERS_INDEX = "users";
    public static final String USERS_TYPE = "users_info";
    public static final String USERS_ALL_ALIAS = "users_all";
    public static final String USERS_ERROR_ALIAS = "users_error";
    public static final String ACCESS_CODES_ALL_ALIAS = "accesscodes_all";
    public static final String ACCESS_CODES_FY14_ALIAS = "accesscodes_fy14";
    public static final String ACCESS_CODES_FY13_ALIAS = "accesscodes_fy13";
    public static final String ACCESS_CODES_FY12_ALIAS = "accesscodes_fy12";
    public static final String ACCESS_CODES_FY11_ALIAS = "accesscodes_fy11";
    public static final String USERS_CAPES_ALIAS = "users_capes";
    public static final String USERS_ICPNA_ALIAS = "users_icpna";
    public static final String USERS_SEVEN_ALIAS = "users_seven";
    public static final String USERS_FY14_ALIAS = "users_fy14";
    public static final String USERS_FY13_ALIAS = "users_fy13";
    public static final String USERS_FY12_ALIAS = "users_fy12";
    public static final String USERS_FY11_ALIAS = "users_fy11";
   
    //Submissions Constants
    public static final String SUBMISSIONS_ALL_ALIAS = "submissions_all";
    public static final String SUBMISSIONS_ASSIGNMENTS_ALIAS = "submissions_assignments";
    public static final String SUBMISSIONS_EXAMVIEW_ALIAS = "submissions_examview";    
    public static final String SUBMISSIONS_INDEX = "submissions";    
    public static final String SUBMISSIONS_TYPE = "submissions_info";
    
    //Other Constants
    public static final String ID = "id";    
    public static final String BLANK = "";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    
    public static final long SQL_RECORDS_LIMIT = 50000;    
    public static final String DEFAULT_ERROR_MESSAGE = "It looks like something went wrong and an error has occurred. Please try agin later.";
    public static final String MYSQL_ERROR_MESSAGE = "Unable to communicate with MySQL Server. Please check MySQL Server settings in Settings Tab.";    
    public static final String DEFAULT_REGION = "NO REGION";
}
