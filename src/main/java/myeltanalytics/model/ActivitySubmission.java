package myeltanalytics.model;


public class ActivitySubmission
{
    private long id;
    private String syncJobId;
    private String dateSubmitted;
    private double studentScore;
    private double maxScore;
    private Activity activity;
    private User user;
    private int progressSaved;
    private int timeSpent;
    
    
    public static class Activity{
        private long id;
        private String name;
        private int maxTakesAllowed;
        private int sectionId;
        private int activityType;
        private Book book;
        private String assignmentData;
        public Activity(long id)
        {
            this.id = id;
        }
        /**
         * @return the id
         */
        public long getId()
        {
            return id;
        }
        /**
         * @param id the id to set
         */
        public void setId(long id)
        {
            this.id = id;
        }
        /**
         * @return the name
         */
        public String getName()
        {
            return name;
        }
        /**
         * @param name the name to set
         */
        public void setName(String name)
        {
            this.name = name;
        }
        /**
         * @return the maxTakesAllowed
         */
        public int getMaxTakesAllowed()
        {
            return maxTakesAllowed;
        }
        /**
         * @param maxTakesAllowed the maxTakesAllowed to set
         */
        public void setMaxTakesAllowed(int maxTakesAllowed)
        {
            this.maxTakesAllowed = maxTakesAllowed;
        }
        public int getSectionId()
        {
            return sectionId;
        }
        public void setSectionId(int sectionId)
        {
            this.sectionId = sectionId;
        }
        
        public void setActivityType(int activityType)
        {
            this.activityType = activityType;
        }
        
        protected int getActivityType()
        {
            return this.activityType;
        }
        
        /**
         * @return the book
         */
        protected Book getBook()
        {
            return book;
        }
        /**
         * @param book the book to set
         */
        public void setBook(Book book)
        {
            this.book = book;
        }
        
        public void setAssignmentData(String assignmentData)
        {
            this.assignmentData = assignmentData; 
            
        }
        
        protected String getAssignmentData()
        {
            return this.assignmentData;
        }
    }
    
    
    public static class Book{
        private String abbr;
        private String name;
        private String discipline;
        /**
         * @return the abbr
         */
        public String getAbbr()
        {
            return abbr;
        }
        /**
         * @param abbr the abbr to set
         */
        public void setAbbr(String abbr)
        {
            this.abbr = abbr;
        }
        /**
         * @return the name
         */
        public String getName()
        {
            return name;
        }
        /**
         * @param name the name to set
         */
        public void setName(String name)
        {
            this.name = name;
        }
        /**
         * @return the discipline
         */
        public String getDiscipline()
        {
            return discipline;
        }
        /**
         * @param discipline the discipline to set
         */
        public void setDiscipline(String discipline)
        {
            this.discipline = discipline;
        }
        
    }  
    
    
    public static class User{
        private long id;
        private String firstName;
        private String lastName;
        private String country;
        private Institution institution;
        public User(long id)
        {
            this.id = id;
        }
        /**
         * @return the id
         */
        public long getId()
        {
            return id;
        }
        /**
         * @param id the id to set
         */
        public void setId(long id)
        {
            this.id = id;
        }
        /**
         * @return the firstName
         */
        public String getFirstName()
        {
            return firstName;
        }
        /**
         * @param firstName the firstName to set
         */
        public void setFirstName(String firstName)
        {
            this.firstName = firstName;
        }
        /**
         * @return the lastName
         */
        public String getLastName()
        {
            return lastName;
        }
        /**
         * @param lastName the lastName to set
         */
        public void setLastName(String lastName)
        {
            this.lastName = lastName;
        }
        public String getCountry()
        {
            return country;
        }
        public void setCountry(String country)
        {
            this.country = country;
        }
        public void setInstitution(Institution institution)
        {
            this.institution = institution;
        }
        protected Institution getInstitution(){
            return institution;
        }
        
    } 
    
    public ActivitySubmission(long id)
    {
        this.id = id;
    }
    public long getId()
    {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * @return the dateSubmitted
     */
    public String getDateSubmitted()
    {
        return dateSubmitted;
    }
    /**
     * @param dateSubmitted the dateSubmitted to set
     */
    public void setDateSubmitted(String dateSubmitted)
    {
        this.dateSubmitted = dateSubmitted;
    }
    /**
     * @return the studentScore
     */
    public double getStudentScore()
    {
        return studentScore;
    }
    /**
     * @param studentScore the studentScore to set
     */
    public void setStudentScore(double studentScore)
    {
        this.studentScore = studentScore;
    }
    /**
     * @return the maxScore
     */
    public double getMaxScore()
    {
        return maxScore;
    }
    /**
     * @param maxScore the maxScore to set
     */
    public void setMaxScore(double maxScore)
    {
        this.maxScore = maxScore;
    }
    /**
     * @return the assignment
     */
    public Activity getActivity()
    {
        return activity;
    }
    /**
     * @param assignment the assignment to set
     */
    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }
    
    /**
     * @return the user
     */
    public User getUser()
    {
        return user;
    }
    /**
     * @param user the user to set
     */
    public void setUser(User user)
    {
        this.user = user;
    }
    /**
     * @return the institution
     */
    public Institution getInstitution()
    {
        return user.getInstitution();
    }
    
    public Book getBook(){
        return activity.getBook();
    }
    public String getSyncJobId()
    {
        return syncJobId;
    }
    public void setSyncJobId(String syncJobId)
    {
        this.syncJobId = syncJobId;
    }
    
    public void setProgressSaved(int progressSaved)
    {
        this.progressSaved =  progressSaved; 
    }
    
    public String getStatus(){
        if(progressSaved == 0){
            return "Submitted";
        } else {
            return "In-Progress";  
        }
    }
    
    public String getActivityType()
    {
        int activityType = activity.getActivityType();
        if(activityType == 5){
            return "ExamView";
        } else if(("").equals(activity.getAssignmentData())){
            return "ExamView";
        } else {
            return "Assignment";
        }
    }
    
    public void setTimeSpent(int timeSpent)
    {
        this.timeSpent = timeSpent;
    }
    
    public int getTimeSpent()
    {
        return this.timeSpent;
    }
}
