package dev.aditya.productcatalogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication//(exclude  = {DataSourceAutoConfiguration.class}) // "exclude = "Disables the initialization for DB, remove it when finally decided adding a DB.
public class ProductCatalogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogServiceApplication.class, args);
    }

}
