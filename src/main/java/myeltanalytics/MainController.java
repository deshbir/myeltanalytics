package myeltanalytics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class MainController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private EventBusService eventBusService;
    
    @RequestMapping("/")
    public String getIndex() {
        return "Welcome to MyElt Analytics";
    }
    
    @RequestMapping("/getUsersFromDatabase")
    @ResponseBody List<User> getFromDatabase() {
        List<User> results = jdbcTemplate.query(
            "select id from users LIMIT 5",
            new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new User(rs.getLong("id"));
                }
            });
        return results;
    }
    
    @RequestMapping(value= "/start")
    @ResponseBody String putDataIntoElasticSearch() throws JsonProcessingException{
        
        jdbcTemplate.query(
            "select id from users where type=0",
            new RowCallbackHandler()
            {
                @Override
                public void processRow(ResultSet rs) throws SQLException
                {
                    PushObjectEvent event = new PushObjectEvent("bca", "users", rs.getLong("id"));
                    PushDataListener.USER_POSTED_STATUS_MAP.put(rs.getLong("id"),Status.WAITING);
                    eventBusService.postEvent(event);
                    
                }
            });         
        return "Started sync process...";
    }
    
    /*@RequestMapping(value= "/retrieve/{index}/{document}/{id}")
    @ResponseBody String getDataFromElasticSearch(@PathVariable(value="document")String document, @PathVariable(value="index")String index, @PathVariable(value="id")String id ){
        
        eventBusService.postEvent(event);
        
    }*/

    
}
