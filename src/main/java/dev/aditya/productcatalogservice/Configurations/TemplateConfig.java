package dev.aditya.productcatalogservice.Configurations;


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

    @Bean(name = "RestClient")
    public RestClient createRestClient(){
        return RestClient.create();
        /*
                .builder()
                .baseUrl("http://localhost:".concat(String.valueOf(serverProperties.getPort())))
                .build();
         */
        }

     /*
    @Bean
    public Validations createValidationCheck()
    {
        return new Validations();
    }*/
}
