package myeltanalytics;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class PropertiesToXMLConverter
{
    public static final String COUNTRY_XML_FILE_NAME = "country.xml";
    
    
    public static final String COUNTRY_PROPERTTIES_FILE_NAME = "country.properties";
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println("*****Starting Converting Properties File Into XML Process*****");
        Properties props = new Properties();
        InputStream input = null;
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "countries" );
        try {
            System.out.println("Reading properties file....");
            input = new FileInputStream(COUNTRY_PROPERTTIES_FILE_NAME);
     
            // load a properties file
            props.load(input);
            Enumeration<?> e = props.propertyNames();
            Element country =  null;
            String code = null;
            String name = null;
            while (e.hasMoreElements()) {
                country = root.addElement( "country" );
                code = (String) e.nextElement();
                name = props.getProperty(code);
                country.addElement("code").addText(code);
                country.addElement("name").addText(name);
                country.addElement("region").addText("North America");
            }
            writeDocument(document);
        } catch (IOException ex) {
            System.out.println("Error occured while reading properties file....");
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("*****Finished Converting Properties File Into XML Process*****");
    }

    private static void writeDocument(Document document)
    {
        // Pretty print the document to System.out
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = null;
        try
        {
            writer = new XMLWriter( new FileWriter( COUNTRY_XML_FILE_NAME), format );
            System.out.println("Writing XML file....");
            writer.write( document );
        }
        catch (IOException e)
        {
            System.out.println("Error occured while writing XML file....");
            e.printStackTrace();
        }finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        
    }

}
