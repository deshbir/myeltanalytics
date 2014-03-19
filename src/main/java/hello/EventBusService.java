package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.eventbus.EventBus;

@Service(value="eventBusService")
public class EventBusService
{
    @Autowired
    private EventBus eventBus;
    
    
    public void registerSubscriber(Object subscriber) {
        eventBus.register(subscriber);
    }
    
    
    public void unRegisterSubscriber(Object subscriber) {
        eventBus.unregister(subscriber);
    }
     
    public void postEvent(Object e) {
        eventBus.post(e);
    }
}
