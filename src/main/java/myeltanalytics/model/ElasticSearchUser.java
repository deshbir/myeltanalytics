package myeltanalytics.model;

import java.util.LinkedList;
import java.util.List;

public class ElasticSearchUser extends AbstractUser {
        private long jobId;
        private String recordType; 
        private String studentType;
        private AccessCode accessCode;
        private List<String> productNames;
        private List<String> productCodes;
        private List<String> disciplines;
        private Country country;
        private String region;
        private ElasticSearchInstitution institution;
        
        public static ElasticSearchUser transformUser(User user, AccessCode ac,String recordType,long jobId)
        {
            ElasticSearchUser esUser = new ElasticSearchUser();
            esUser.setJobId(jobId);
            esUser.setAccessCode(ac);
            esUser.setId(user.getId());
            esUser.setFirstName(user.getFirstName());
            esUser.setLastName(user.getLastName());
            esUser.setEmail(user.getEmail());
            esUser.setUserName(user.getUserName());
            esUser.setUserCountry(user.getUserCountry());
            esUser.setCourses(user.getCourses());
            esUser.setDateCreated(user.getDateCreated());
            esUser.setDateLastLogin(user.getDateLastLogin());
            esUser.setUserType(user.getUserType());
            esUser.setUserCountry(user.getUserCountry());
            ElasticSearchInstitution esi = ElasticSearchInstitution.transformInstitution(user.getInstitution());
            esUser.setInstitution(esi);
            esUser.setRecordType(recordType);
            esUser.setStudentType(user.getStudentType());
            esUser.setCountry(user.getCountry());
            esUser.setRegion(user.getRegion());
            List<String> productCodes = new LinkedList<String>();
            List<String> productNames = new LinkedList<String>();
            List<String> disciplines = new LinkedList<String>();
            for(AccessCode accessCode : user.getAccesscodes()){
                productNames.add(accessCode.getProductName());
                disciplines.add(accessCode.getDiscipline());
                productCodes.add(accessCode.getProductCode());
            }
            esUser.setProductCodes(productCodes);
            esUser.setProductNames(productNames);
            esUser.setDisciplines(disciplines);
            return esUser;
        }
      


       
        /**
         * @return the products
         */
        public List<String> getProductNames()
        {
            return productNames;
        }
        
        /**
         * @return the total name(first name + last name)
         */
        public String getName()
        {
            return getFirstName() + " "  + getLastName();
        }



        /**
         * @param products the products to set
         */
        public void setProductNames(List<String> productNames)
        {
            this.productNames = productNames;
        }



        public AccessCode getAccessCode()
        {
            return accessCode;
        }

        public void setAccessCode(AccessCode accessCode)
        {
            this.accessCode = accessCode;
        }




        public List<String> getDisciplines()
        {
            return disciplines;
        }




        public void setDisciplines(List<String> disciplines)
        {
            this.disciplines = disciplines;
        }




        public List<String> getProductCodes()
        {
            return productCodes;
        }




        public void setProductCodes(List<String> productCodes)
        {
            this.productCodes = productCodes;
        }




        public String getRecordType()
        {
            return recordType;
        }




        public void setRecordType(String recordType2)
        {
            this.recordType = recordType2;
        }




        public String getStudentType()
        {
            return studentType;
        }




        public void setStudentType(String studentType)
        {
            this.studentType = studentType;
        }




        public Country getCountry()
        {
            return country;
        }




        public void setCountry(Country country)
        {
            this.country = country;
        }




        public String getRegion()
        {
            return region;
        }




        public void setRegion(String region)
        {
            this.region = region;
        }




        public ElasticSearchInstitution getInstitution()
        {
            return institution;
        }




        public void setInstitution(ElasticSearchInstitution institution)
        {
            this.institution = institution;
        }




        public long getJobId()
        {
            return jobId;
        }




        public void setJobId(long jobId)
        {
            this.jobId = jobId;
        }

        
        
 }