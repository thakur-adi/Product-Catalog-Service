package dev.aditya.productcatalogservice.Configurations;


//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TemplateConfig {

    //use this when you don't own the source code (like a third-party library) or need custom initialization logic. Otherwise, use @Component.
    @Bean(name = "RestTemplate")
    public RestTemplate createRestTemplate(){
        return new RestTemplate();
    }

    @Bean(name = "LoadBalancedRestTemplate")
    @LoadBalanced
    public RestTemplate createLoadBalancedRestTemplate(){
        return new RestTemplate();
    }


    @Bean(name = "RestClient")
    public RestClient createRestClient(){
        return RestClient.create();
        /*
                .builder()
                .baseUrl("http://localhost:".concat(String.valueOf(serverProperties.getPort())))
                .build();
         */
        }

//    Unfortunately RestClient causes issues with Eureka server.
//    As we know Eureka needs Load balanced instance, its a little tricky for rest client as it needs special Loadbalancer to be created for  it to work.

//    @Bean(name = "LoadBalancedRestClient")
//    //@LoadBalanced this only works with rest template not client
//    public RestClient createLoadBalancedRestClient(){
//        return RestClient.create();
//    }


     /*
    @Bean
    public Validations createValidationCheck()
    {
        return new Validations();
    }*/
}
