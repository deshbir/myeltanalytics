package hello;

import hello.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
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
    
    Node node = NodeBuilder.nodeBuilder().node();
    Client client = node.client();
    /*String home() {
        
    }*/
    
    @RequestMapping("/")
    public String index(){
        return "";
    }
    
    private void storeInDatabase() {
        jdbcTemplate.execute("drop table customers if exists");
        jdbcTemplate.execute("create table customers(" +
                "id serial, first_name varchar(255), last_name varchar(255))");

        String[] names = "John Woo;Jeff Dean;Josh Bloch;Josh Long".split(";");
        for (String fullname : names) {
            String[] name = fullname.split(" ");
            System.out.printf("Inserting customer record for %s %s\n", name[0], name[1]);
            jdbcTemplate.update(
                    "INSERT INTO customers(first_name,last_name) values(?,?)",
                    name[0], name[1]);
        }
    }
    
    private List<Customer> getFromDatabase() {
        List<Customer> results = jdbcTemplate.query(
            "select * from customers where first_name = ?", new Object[] { "Josh" },
            new RowMapper<Customer>() {
                @Override
                public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Customer(rs.getLong("id"), rs.getString("first_name"),
                            rs.getString("last_name"));
                }
            });

        for (Customer customer : results) {
            System.out.println(customer);
        }
        return results;
    }
    
    @RequestMapping(value= "/store/{document}/{index}/{id}")
    @ResponseBody List<Customer> putDataIntoElasticSearch(@PathVariable(value="document")String document, @PathVariable(value="index")String index ,@PathVariable(value="id")int id) throws JsonProcessingException{
        storeInDatabase();
        List<Customer> objects = getFromDatabase();
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse
        String json;
        for(Customer c : objects){
            json = mapper.writeValueAsString(c);
            System.out.println(json);
            IndexResponse response = client.prepareIndex(document, index,String.valueOf(id++)).setSource(json).execute().actionGet();
        }
        
        
        return objects;
    }
    
    @RequestMapping(value= "/retrieve/{document}/{index}/{id}")
    @ResponseBody String getDataFromElasticSearch(@PathVariable(value="document")String document, @PathVariable(value="index")String index, @PathVariable(value="id")String id ){
        GetResponse response = client.prepareGet(document, index, id)
            .execute()
            .actionGet();
        System.out.println(response.getSourceAsString());
        return response.getSourceAsString();
    }

    
}
