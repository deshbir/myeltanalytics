package myeltanalytics.model;


public class Access
{
    private String code;
    private String dateCreated;
    private String productCode;
    private String productName;
    private String discipline;
    private String accessType;
    
    /**
     * @return the code
     */
    public String getCode()
    {
        return code;
    }
    /**
     * @param code the code to set
     */
    public void setCode(String code)
    {
        this.code = code;
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
     * @return the productCode
     */
    public String getProductCode()
    {
        return productCode;
    }
    /**
     * @param productCode the productCode to set
     */
    public void setProductCode(String productCode)
    {
        this.productCode = productCode;
    }
    /**
     * @return the productName
     */
    public String getProductName()
    {
        return productName;
    }
    /**
     * @param productName the productName to set
     */
    public void setProductName(String productName)
    {
        this.productName = productName;
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
	public String getAccessType() {
		return accessType;
	}
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}
    
}
