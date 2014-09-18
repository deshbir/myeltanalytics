package myeltanalytics.model;

import java.util.HashMap;
import java.util.List;

import myeltanalytics.service.HelperService;

import org.dom4j.Node;

public abstract class AbstractUser
{
    private long id;
    private String databaseURL;
    private String userName;
    private String email;
    private String userType;
    private String dateCreated;
    private String dateLastLogin;
    private String firstName,lastName;
    private Country userCountry;
    private List<String> courses;
    private HashMap<String ,Milestone> milestones;
    private String lastMilestoneId;
    private String lastMilestoneStatus;
    private String lastMilestoneLevel;
    private String lastMilestoneStartedDate;
    private String lastMilestoneAccessedDate;
    private String lastMilestoneIsActive;
    private String lastMilestoneTestName;
    private String lastMilestonePassPercent;
    private String lastMilestonePassAction;
    private String lastMilestoneExpiry;
    

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
    public String getDateCreated()
    {
        return dateCreated;
    }

    /**
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(String dateCreated)
    {
        this.dateCreated = dateCreated;
    }
    
    /**
     * @return the dateLastLogin
     */
    public String getDateLastLogin()
    {
        return dateLastLogin;
    }
    
    /**
     * @param date the dateLastLogin to set
     */
    public void setDateLastLogin(String date)
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
    
    public void setUserCountry(String countryCode)
    {
        if (countryCode != null && !countryCode.equals("")) {
            String xPath  = "//country/code[text()=\"" +  countryCode.toUpperCase() + "\"]";
            Node node = HelperService.countryDocument.selectSingleNode( xPath );
            if(node != null){
                this.userCountry = new Country(node.getParent().valueOf("name"), countryCode);
            }    
        }
    }

    public String getDatabaseURL()
    {
        return databaseURL;
    }

    public void setDatabaseURL(String databaseURL)
    {
        this.databaseURL = databaseURL;
    }
    
	
	public HashMap<String, Milestone> getMilestones() {
		return milestones;
	}



	public void setMilestones(HashMap<String, Milestone> milestones) {
		this.milestones = milestones;
	}



	public String getLastMilestoneId()
    {
        return lastMilestoneId;
    }

    public void setLastMilestoneId(String lastMilestoneId)
    {
        this.lastMilestoneId = lastMilestoneId;
    }

    public String getLastMilestoneStatus()
    {
        return lastMilestoneStatus;
    }

    public void setLastMilestoneStatus(String lastMilestoneStatus)
    {
        this.lastMilestoneStatus = lastMilestoneStatus;
    }

    public String getLastMilestoneLevel()
    {
        return lastMilestoneLevel;
    }

    public void setLastMilestoneLevel(String lastMilestoneLevel)
    {
        this.lastMilestoneLevel = lastMilestoneLevel;
    }
    
    public String getLastMilestoneAccessedDate()
    {
        return lastMilestoneAccessedDate;
    }

    public void setLastMilestoneAccessedDate(String lastMilestoneAccessedDate)
    {
        this.lastMilestoneAccessedDate = lastMilestoneAccessedDate;
    }
    
	public String getLastMilestoneStartedDate() {
		return lastMilestoneStartedDate;
	}

	public void setLastMilestoneStartedDate(String lastMilestoneStartedDate) {
		this.lastMilestoneStartedDate = lastMilestoneStartedDate;
	}
	
	public String getLastMilestoneIsActive() {
		return lastMilestoneIsActive;
	}
	
	public void setLastMilestoneIsActive(String lastMilestoneIsActive) {
		this.lastMilestoneIsActive = lastMilestoneIsActive;
	}

	public String getLastMilestoneTestName() {
		return lastMilestoneTestName;
	}

	public void setLastMilestoneTestName(String lastMilestoneTestName) {
		this.lastMilestoneTestName = lastMilestoneTestName;
	}

	public String getLastMilestonePassPercent() {
		return lastMilestonePassPercent;
	}

	public void setLastMilestonePassPercent(String lastMilestonePassPercent) {
		this.lastMilestonePassPercent = lastMilestonePassPercent;
	}

	public String getLastMilestonePassAction() {
		return lastMilestonePassAction;
	}
	
	public void setLastMilestonePassAction(String lastMilestonePassAction) {
		this.lastMilestonePassAction = lastMilestonePassAction;
	}

	public String getLastMilestoneExpiry() {
		return lastMilestoneExpiry;
	}
	
	public void setLastMilestoneExpiry(String lastMilestoneExpiry) {
		this.lastMilestoneExpiry = lastMilestoneExpiry;
	}
	
}
