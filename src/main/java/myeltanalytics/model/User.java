package myeltanalytics.model;

import java.util.ArrayList;

public class User extends AbstractUser {

    private ArrayList<Access> accessList;
    private Institution institution;
    private boolean isCapes;

    
    public enum UserType{
        STUDENT(1,"STUDENT"),INSTRUCTOR(2,"INSTRUCTOR"),AUTHOR(3,"AUTHOR"),ADMINISTRATOR(4,"ADMINISTRATOR"),DISTRICT(5,"DISTRICT"),SUPERUSER(6,"SUPERUSER");
        private int code;
        private String typeString;
        UserType(int code, String typeString){
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
    
    
    /**
     * @param userType the userType to set
     */
    public void setUserType(int userType)
    {
        switch(userType){
            case 1:
                setUserType(UserType.STUDENT.getTypeString());
                break;
            case 2:
                setUserType(UserType.INSTRUCTOR.getTypeString());
                break;
            case 3:
                setUserType(UserType.AUTHOR.getTypeString());
                break;
            case 4:
                setUserType(UserType.ADMINISTRATOR.getTypeString());
                break;
            case 5:
                setUserType(UserType.DISTRICT.getTypeString());
                break;
            case 6:
                setUserType(UserType.SUPERUSER.getTypeString());
                break;
        }
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
    
    public String getUserModel()
    {
    	if(isCapes){
            return Constants.CAPES_MODEL;
        }  else {
        	if((institution.getId().equals("SELF_LEARNER")) || (institution.getOther().indexOf("MyELTHideAssignment=true") != -1)){
                return "self_paced";
            } else if(institution.getOther().indexOf("MyELTSelfLearner=false") != -1){
                return "classroom";
            } else {
            	return "hybrid_learner";
            }            
        }    	
    }
    
    
    public Country getUserCountry()
    {
        return super.getUserCountry();
    }
    
    public Country getCountry()
    {
        if(getUserCountry() != null && getUserCountry().getCode() !=null){
            return getUserCountry();
        } else if (institution.getCountry() != null && institution.getCountry().getCode() != null) {
            return institution.getCountry();
        } else {           
           return new Country("No Country","##");
        }
        
    }

	public ArrayList<Access> getAccessList() {
		return accessList;
	}

	public void setAccessList(ArrayList<Access> accessList) {
		this.accessList = accessList;
	}

	public boolean isCapes() {
		return isCapes;
	}

	public void setCapes(boolean isCapes) {
		this.isCapes = isCapes;
	}
}