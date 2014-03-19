package myeltanalytics;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;


@Service(value="retrieveEventSubscriber")
public class RetrieveEventSubscriber
{
    @Autowired
    private EventBus eventBus;
    
    
    @Autowired
    private Node node;
    
    @PostConstruct
    void subscribeToBus(){
        eventBus.register(this);
    }
    
    
    @Subscribe
    public void onEvent(RetrieveObjectEvent event) {
        Client client = node.client();
        GetResponse response = client.prepareGet(event.getIndex(),event.getDocument(), event.getId())
            .execute()
            .actionGet();
        System.out.println(response.getSourceAsString());
    }
    
}
