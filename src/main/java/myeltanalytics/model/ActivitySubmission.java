package myeltanalytics.model;

import java.util.Date;

public class ActivitySubmission
{
    private long id;
    private Date dateSubmitted;
    private double studentScore;
    private double maxScore;
    private Assignment assignment;
    private User user;
    
    public enum ActivtiyType{
        TYPE_BOOK(2,"book"),TYPE_LINKEDTEST(5,"linked test");
        private String type;
        private int code;
        
        ActivtiyType(int code, String type){
            this.setType(type);
            this.setCode(code);
        }

        public String getTypeString()
        {
            return type;
        }

        public void setType(String type)
        {
            this.type = type;
        }

        public int getCode()
        {
            return code;
        }

        public void setCode(int code)
        {
            this.code = code;
        }
        
        
    }
    public static class Assignment{
        private long id;
        private String name;
        private int maxTakesAllowed;
        private int sectionId;
        private String activityType;
        private Book book;
        public Assignment(long id)
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
        public String getActivityType()
        {
            return activityType;
        }
        public void setActivityType(int activityType)
        {
            switch(activityType){
                case 2:
                    this.activityType = ActivtiyType.TYPE_BOOK.getTypeString();
                    break;
                case 5:
                    this.activityType = ActivtiyType.TYPE_LINKEDTEST.getTypeString();
                    break;
                
            }
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
    public Date getDateSubmitted()
    {
        return dateSubmitted;
    }
    /**
     * @param dateSubmitted the dateSubmitted to set
     */
    public void setDateSubmitted(Date dateSubmitted)
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
    public Assignment getAssignment()
    {
        return assignment;
    }
    /**
     * @param assignment the assignment to set
     */
    public void setAssignment(Assignment assignment)
    {
        this.assignment = assignment;
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
        return assignment.getBook();
    }
    
}
