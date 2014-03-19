package myeltanalytics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.elasticsearch.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class HelloController {
    
 
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private EventBusService eventBusService;
    
    @Autowired
    private Node node;
    
    
    @RequestMapping("/")
    public String getIndex() {
        return "Welcome to MyElt Analytics";
    }
    
    @RequestMapping("/getUsersFromDatabase")
    @ResponseBody List<User> getFromDatabase() {
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
    
    @RequestMapping(value= "/start")
    @ResponseBody String putDataIntoElasticSearch() throws JsonProcessingException{
        List<User> users = getFromDatabase();
        for(User user : users){
            PushObjectEvent event = new PushObjectEvent("bca", "users", user.getId(),user);
            PushDataListener.USER_POSTED_STATUS_MAP.put(user.getId(),Status.WAITING);
            eventBusService.postEvent(event);
            
        }
        
        
        return "posted scuccesfully";
    }
    
    /*@RequestMapping(value= "/retrieve/{index}/{document}/{id}")
    @ResponseBody String getDataFromElasticSearch(@PathVariable(value="document")String document, @PathVariable(value="index")String index, @PathVariable(value="id")String id ){
        
        eventBusService.postEvent(event);
        
    }*/

    
}
