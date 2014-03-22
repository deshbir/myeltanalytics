package myeltanalytics;

import java.util.Date;
import java.util.List;

public class User {
    private long id;
    private String userName;
    private String email;
    private String userType;
    private Date dateCreated;
    private Date dateLastLogin;
    private String firstName,lastName;
    private String country;
    private Institution institution;
    private List<String> products;
    private List<String> courses;
    private List<String> accesscodes;
    
    public enum StudentType{
        STUDENT(1,"STUDENT"),INSTRUCTOR(2,"INSTRUCTOR"),AUTHOR(3,"AUTHOR"),ADMINISTRATOR(4,"ADMINISTRATOR"),DISTRICT(5,"DISTRICT"),SUPERUSER(6,"SUPERUSER");
        private int code;
        private String typeString;
        StudentType(int code, String typeString){
            this.code = code;
            this.typeString = typeString;
        }
        /**
         * @return the code
         */
        public int getCode()
        {
            return code;
        }
        /**
         * @param code the code to set
         */
        public void setCode(int code)
        {
            this.code = code;
        }
        /**
         * @return the typeString
         */
        public String getTypeString()
        {
            return typeString;
        }
        /**
         * @param typeString the typeString to set
         */
        public void setTypeString(String typeString)
        {
            this.typeString = typeString;
        }
        
        
    }
    
    
    public User(long id){
        this.id = id;
    }
    
    /*@Override
    public String toString() {
        return String.format(
                "User[id=%d, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }*/

    

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
     * @return the userName
     */
    public String getUserName()
    {
        return userName;
    }



    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }



    /**
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }



    /**
     * @param email the email to set
     */
    public void setEmail(String email)
    {
        this.email = email;
    }



    /**
     * @return the userType
     */
    public String getUserType()
    {
        return userType;
    }



    /**
     * @param userType the userType to set
     */
    public void setUserType(int userType)
    {
        switch(userType){
            case 1:
                this.userType = StudentType.STUDENT.getTypeString();
                break;
            case 2:
                this.userType = StudentType.INSTRUCTOR.getTypeString();
                break;
            case 3:
                this.userType = StudentType.AUTHOR.getTypeString();
                break;
            case 4:
                this.userType = StudentType.ADMINISTRATOR.getTypeString();
                break;
            case 5:
                this.userType = StudentType.DISTRICT.getTypeString();
                break;
            case 6:
                this.userType = StudentType.SUPERUSER.getTypeString();
                break;
        }
    }



    /**
     * @return the dateCreated
     */
    public Date getDateCreated()
    {
        return dateCreated;
    }



    /**
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(Date dateCreated)
    {
        this.dateCreated = dateCreated;
    }



    /**
     * @return the dateLastLogin
     */
    public Date getDateLastLogin()
    {
        return dateLastLogin;
    }



    /**
     * @param dateLastLogin the dateLastLogin to set
     */
    public void setDateLastLogin(long dateLastLogin)
    {
        this.dateLastLogin = new Date(dateLastLogin);
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



    /**
     * @return the country
     */
    public String getCountry()
    {
        return country;
    }



    /**
     * @param country the country to set
     */
    public void setCountry(String country)
    {
        this.country = country;
    }



    /**
     * @return the instituion
     */
    public Institution getInstitution()
    {
        return institution;
    }



    /**
     * @param instituion the instituion to set
     */
    public void setInstitution(Institution institution)
    {
        this.institution = institution;
    }



    /**
     * @return the products
     */
    public List<String> getProducts()
    {
        return products;
    }



    /**
     * @param products the products to set
     */
    public void setProducts(List<String> products)
    {
        this.products = products;
    }



    /**
     * @return the courses
     */
    public List<String> getCourses()
    {
        return courses;
    }



    /**
     * @param courses the courses to set
     */
    public void setCourses(List<String> courses)
    {
        this.courses = courses;
    }



    /**
     * @return the accesscodes
     */
    public List<String> getAccesscodes()
    {
        return accesscodes;
    }



    /**
     * @param accesscodes the accesscodes to set
     */
    public void setAccesscodes(List<String> accesscodes)
    {
        this.accesscodes = accesscodes;
    }
    
    public String getName(){
        return this.firstName + " " + this.lastName;
    }
   

}