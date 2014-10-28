package myeltanalytics;

import java.io.IOException;

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

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application {
    
    /******************************
    * ElasticSearch Properties
    ******************************/
    @Value("${elasticsearch.host}")
    private String elasticSearchHost;
    
    @Value("${elasticsearch.port}")
    private int elasticSearchPort;
    
    @Value("${elasticsearch.clustername}")
    private String clusterName;
    
    
    public static void main(String[] args) throws IOException {  
        ApplicationContext ctx = SpringApplication.run(Application.class, args);        
        ctx.getBeanDefinitionNames();
		System.out.println("************************************");
		System.out.println("MyELT Analytics Application Started");
		System.out.println("************************************");
    }
    
    @Bean
    public Client elasticSearchClient(){
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
        TransportClient transportClient = new TransportClient(settings);
        transportClient.addTransportAddress(new InetSocketTransportAddress(elasticSearchHost, elasticSearchPort));
        return transportClient;
    }
}
