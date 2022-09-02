package org.lafabriquedigitowl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class OwlConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OwlConsumerApplication.class, args);
    }

}
