package myeltanalytics;

import java.util.concurrent.Executors;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${elasticsearch.host}")
    private String elasticSearchHost;
    
    @Value("${elasticsearch.port}")
    private int elasticSearchPort;
    
    @Value("${eventbus.poolsize}")
    private int eventbusPoolSize;
    
    @Value("${elasticsearch.clustername}")
    private String clusterName;
    
    public static void main(String[] args) {        
        ApplicationContext ctx = SpringApplication.run(Application.class, args);        
        ctx.getBeanDefinitionNames();
		System.out.println("************************************");
		System.out.println("MyELT Analytics Application Started");
		System.out.println("************************************");
    }
    
    @Bean
    public EventBus eventBus() {
        return new AsyncEventBus(Executors.newFixedThreadPool(eventbusPoolSize));
    }
        
    @Bean
    public Client elasticSearchClient(){
        Settings settings = ImmutableSettings.settingsBuilder()
            .put("cluster.name", clusterName).build();

        return new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(elasticSearchHost, elasticSearchPort));
    }
}
