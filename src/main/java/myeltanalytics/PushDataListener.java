package myeltanalytics;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;


@Service(value="retrieveEventSubscriber")
public class PushDataListener
{
    
    

    public static Map<Long,Status> USER_POSTED_STATUS_MAP = new HashMap<Long,Status>();
    
    @Autowired
    private EventBus eventBus;
    
    
    @Autowired
    private Node node;
    
    @PostConstruct
    void subscribeToBus(){
        eventBus.register(this);
    }
    
    
    @Subscribe
    public void onEvent(PushObjectEvent event) {
        Client client = node.client();
        User user = event.getUser(); 
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse
        String json;
        try
        {
            json = mapper.writeValueAsString(user);
            client.prepareIndex(event.getIndex(),event.getDocument(),event.getId()).setSource(json).execute().actionGet();
            USER_POSTED_STATUS_MAP.put(user.getId(), Status.SUCCESS);
            
            System.out.println("user: " + event.getUser().getId() + " pushed successfully");
        }
        catch (JsonProcessingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            USER_POSTED_STATUS_MAP.put(user.getId(), Status.FAILURE);
        }
        catch(Exception e){
            USER_POSTED_STATUS_MAP.put(user.getId(), Status.FAILURE);
        }
        
    }
    
}
