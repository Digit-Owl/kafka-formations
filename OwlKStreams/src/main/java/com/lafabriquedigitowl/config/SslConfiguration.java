package com.lafabriquedigitowl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.ssl")
public record SslConfiguration(String trustStoreLocation, String trustStorePassword) {

}
