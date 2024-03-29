package myeltanalytics.model;

import java.util.LinkedList;
import java.util.List;

import myeltanalytics.service.HelperService;

import org.dom4j.Node;

public class ElasticSearchUser extends AbstractUser {
        private String recordType; 
        private String userModel;
        private Access access;
        private List<String> productNames;
        private List<String> productCodes;
        private List<String> disciplines;
        private Country country;
        private String region;
        private ElasticSearchInstitution institution;
        
        public static ElasticSearchUser transformUser(User user, Access ac,String recordType)
        {
            ElasticSearchUser esUser = new ElasticSearchUser();
            esUser.setAccess(ac);
            esUser.setId(user.getId());
            esUser.setDatabaseURL(user.getDatabaseURL());
            esUser.setFirstName(user.getFirstName());
            esUser.setLastName(user.getLastName());
            esUser.setEmail(user.getEmail());
            esUser.setUserName(user.getUserName());
            esUser.setCourses(user.getCourses());
            esUser.setDateCreated(user.getDateCreated());
            esUser.setDateLastLogin(user.getDateLastLogin());
            esUser.setUserType(user.getUserType());
            esUser.setUserCountry(user.getUserCountry());
            ElasticSearchInstitution esi = ElasticSearchInstitution.transformInstitution(user.getInstitution());
            esUser.setInstitution(esi);
            esUser.setRecordType(recordType);
            esUser.setUserModel(user.getUserModel());
            esUser.setCountry(user.getCountry());
            esUser.setRegion();
            List<String> productCodes = new LinkedList<String>();
            List<String> productNames = new LinkedList<String>();
            List<String> disciplines = new LinkedList<String>();
            for(Access access : user.getAccessList()){
                productNames.add(access.getProductName());
                disciplines.add(access.getDiscipline());
                productCodes.add(access.getProductCode());
            }
            esUser.setProductCodes(productCodes);
            esUser.setProductNames(productNames);
            esUser.setDisciplines(disciplines);
            esUser.setMilestones(user.getMilestones());
            esUser.setLastMilestoneLevel(user.getLastMilestoneLevel());
            esUser.setLastMilestoneId(user.getLastMilestoneId());
            esUser.setLastMilestoneStatus(user.getLastMilestoneStatus());
            esUser.setLastMilestoneAccessedDate(user.getLastMilestoneAccessedDate());
            esUser.setLastMilestoneIsActive(user.getLastMilestoneIsActive());
            esUser.setLastMilestoneStartedDate(user.getLastMilestoneStartedDate());
            esUser.setLastMilestonecompletedDate(user.getLastMilestonecompletedDate());
            esUser.setLastMilestoneMaxScore(user.getLastMilestoneMaxScore());
            esUser.setLastMilestoneScore(user.getLastMilestoneScore());
            if(user.getLastMilestoneTestName() != null){
	            esUser.setLastMilestoneTestName(user.getLastMilestoneTestName());
	            esUser.setLastMilestoneExpiry(user.getLastMilestoneExpiry());
	            esUser.setLastMilestonePassAction(user.getLastMilestonePassAction());
	            esUser.setLastMilestonePassPercent(user.getLastMilestonePassPercent());
            }

            esUser.setSyncInfo(user.getSyncInfo());
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




        public String getUserModel()
        {
            return userModel;
        }




        public void setUserModel(String userModel)
        {
            this.userModel = userModel;
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

        public void setRegion()
        {
            try {
                String xPath  = "//country/code[text()=\"" +  getCountry().getCode().toUpperCase() + "\"]";
                Node node = HelperService.countryDocument.selectSingleNode( xPath );
                if(node != null){
                    this.region = node.getParent().getParent().getParent().valueOf("name");
                } else {
                    this.region = Constants.DEFAULT_REGION;
                }
            } catch (Exception e) {
                System.out.println("HELLO");
            }
            
        }

        public ElasticSearchInstitution getInstitution()
        {
            return institution;
        }

        public void setInstitution(ElasticSearchInstitution institution)
        {
            this.institution = institution;
        }




		public Access getAccess() {
			return access;
		}




		public void setAccess(Access access) {
			this.access = access;
		}
       
 }