package myeltanalytics;

import java.util.Date;

public class AccessCode
{
    private String code;
    private Date dateCreated;
    private String productCode;
    private String productName;
    private String discipline;
    
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
    
}
