package myeltanalytics.model;

import java.util.Date;
import java.util.List;

public abstract class AbstractUser
{
    private long id;
    private String userName;
    private String email;
    private String userType;
    private Date dateCreated;
    private Date dateLastLogin;
    private String firstName,lastName;
    private Country userCountry;
    private List<String> courses;
    
    
    
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
     * param the userType
     */
    public void setUserType(String userType)
    {
        this.userType = userType;
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
     * @param date the dateLastLogin to set
     */
    public void setDateLastLogin(Date date)
    {
        this.dateLastLogin = date;
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


    public List<String> getCourses()
    {
        return courses;
    }



    public void setCourses(List<String> courses)
    {
        this.courses = courses;
    }



    public Country getUserCountry()
    {
        return userCountry;
    }



    public void setUserCountry(Country userCountry)
    {
        this.userCountry = userCountry;
    }
}
