package hello;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.elasticsearch.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class HelloController {
    
 
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private EventBusService eventBusService;
    
    @Autowired
    private Node node;
    /*String home() {
        
    }*/
    
    
    
    
    @RequestMapping("/")
    @ResponseBody List<User> getFromDatabase() {
        System.out.println(eventBusService);
        List<User> results = jdbcTemplate.query(
            "select id,firstName,lastName from users where institutionId = ?", new Object[] { "MindApps" },
            new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new User(rs.getLong("id"), rs.getString("firstName"),
                            rs.getString("lastName"));
                }
            });

       
        return results;
    }
    
    @RequestMapping(value= "/start}")
    @ResponseBody List<User> putDataIntoElasticSearch() throws JsonProcessingException{
        List<User> objects = getFromDatabase();
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse
        String json;
        for(User c : objects){
            json = mapper.writeValueAsString(c);
            System.out.println(json);
            node.client().prepareIndex("bca", "users",String.valueOf(c.getId())).setSource(json).execute().actionGet();
        }
        
        
        return objects;
    }
    
    @RequestMapping(value= "/retrieve/{index}/{document}/{id}")
    @ResponseBody String getDataFromElasticSearch(@PathVariable(value="document")String document, @PathVariable(value="index")String index, @PathVariable(value="id")String id ){
        RetrieveObjectEvent event = new RetrieveObjectEvent(document, index, id);
        eventBusService.postEvent(event);
        return "posted scuccesfully";
    }

    
}
