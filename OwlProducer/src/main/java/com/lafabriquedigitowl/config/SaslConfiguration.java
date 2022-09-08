package com.lafabriquedigitowl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.sasl")
public record SaslConfiguration(String mechanism, String jaasConfig) {

}
