package myeltanalytics.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
@RequestMapping("/reports")
public class ReportsController
{

    @RequestMapping("")
    public String index(Model model) {        
        return "reports";
    }
    
    
    @RequestMapping(value="/generateUsersCSVReport",method=RequestMethod.POST)
    public void convertJSONToCSV(@RequestBody String resultData,HttpServletResponse response) throws IOException {
        // take out json-data from the request data
        resultData = URLDecoder.decode(resultData, "UTF-8");
        resultData = resultData.substring(0,resultData.indexOf("="));
        
        // set relevant headers and cookies for file-downloading
        response.setContentType("application/csv");
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                "users.csv");
        response.setHeader(headerKey, headerValue);
        Cookie cookie = new Cookie("fileDownload","true");;
        response.addCookie(cookie);
        
        //create a JSON-parser to parse JSOn data into object
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory(); 
        JsonParser jp = factory.createParser(resultData);
        
        
        ICsvListWriter listWriter = null;
        try {
            listWriter = new CsvListWriter(response.getWriter(),
                    CsvPreference.STANDARD_PREFERENCE);
            
            // the header elements are used to map the bean values to each column (names must match)
            final String[] header = new String[] { "FIRST_NAME","LAST_NAME", "EMAIL" };
            // write the headers
            listWriter.writeHeader(header);
            
            JsonNode actualObj = mapper.readTree(jp);
            if(actualObj.isArray()){
                Iterator<JsonNode> iterator = actualObj.iterator();
                while(iterator.hasNext()){
                    JsonNode node = iterator.next();
                    JsonNode dataObject = node.get("_source");
                    List<String> list = new ArrayList<String>();
                    list.add(dataObject.get("firstName").asText());
                    list.add(dataObject.get("lastName").asText());
                    list.add(dataObject.get("email").asText());
                    listWriter.write(list);
                }
            } else {
                JsonNode node = actualObj;
                JsonNode dataObject = node.get("_source");
                List<String> list = new ArrayList<String>();
                list.add(dataObject.get("firstName").asText());
                list.add(dataObject.get("lastName").asText());
                list.add(dataObject.get("email").asText());
                listWriter.write(list);
            }
            
                
        }
        finally {
                if( listWriter != null ) {
                    listWriter.close();
                }
        }
    }

}
