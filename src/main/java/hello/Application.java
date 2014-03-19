package hello;

import java.util.concurrent.Executors;

import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {
    
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        
        ctx.getBeanDefinitionNames();

		System.out.println("----------------------");
		System.out.println("----------------------");
		System.out.println("----------------------");
		System.out.println("----------------------");
		System.out.println("----------------------");

		System.out.println("Game Started");

		System.out.println("----------------------");
		System.out.println("----------------------");
		System.out.println("----------------------");
		System.out.println("----------------------");

       
    }
    
    @Bean
    public EventBus EventBusBuilder() {
        return new AsyncEventBus(Executors.newCachedThreadPool());
    }
    
    
    @Bean
    public Node NodeBuilder(){
        Node node = NodeBuilder.nodeBuilder().node();
        return node;
    }

}
