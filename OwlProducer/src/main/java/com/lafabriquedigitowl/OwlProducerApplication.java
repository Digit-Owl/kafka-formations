package com.lafabriquedigitowl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class OwlProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OwlProducerApplication.class, args);
    }
}
