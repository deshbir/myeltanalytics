package myeltanalytics.model;

import java.util.Date;
import java.util.List;

public class User extends AbstractUser {

    private List<AccessCode> accesscodes;
    private Institution institution;

    /**
     * @return the accesscodes
     */
    public List<AccessCode> getAccesscodes()
    {
        return accesscodes;
    }

    /**
     * @param accesscodes the accesscodes to set
     */
    public void setAccesscodes(List<AccessCode> accesscodes)
    {
        this.accesscodes = accesscodes;
    }
    
    
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
    
    
    /**
     * @param userType the userType to set
     */
    public void setUserType(int userType)
    {
        switch(userType){
            case 1:
                setUserType(StudentType.STUDENT.getTypeString());
                break;
            case 2:
                setUserType(StudentType.INSTRUCTOR.getTypeString());
                break;
            case 3:
                setUserType(StudentType.AUTHOR.getTypeString());
                break;
            case 4:
                setUserType(StudentType.ADMINISTRATOR.getTypeString());
                break;
            case 5:
                setUserType(StudentType.DISTRICT.getTypeString());
                break;
            case 6:
                setUserType(StudentType.SUPERUSER.getTypeString());
                break;
        }
    }

    public void setDateLastLogin(long date)
    {
        setDateLastLogin(new Date(date));
        
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
    
    public String getStudentType()
    {
        if((institution.getName().equals("SELF_LEARNER")) || (institution.getOther().indexOf("MyELTSelfLearner") != -1)){
            return "SELF_PACED";
        } else if((accesscodes.size() == 0) && (institution.getDistrict().equals("CAPES"))){
            return "CAPES";
        } else if(accesscodes.size() == 0 && !(institution.getDistrict().equals("CAPES"))){
            return "INSTRUCTOR_LED";
        }
        return "OTHERS";
    }

    public Country getCountry()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getRegion()
    {
        // TODO Auto-generated method stub
        return null;
    }

}