package com.lafabriquedigitowl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.properties")
public record KafkaPropertiesConfiguration(String schemaRegistryUrl, String basicAuthCredentialsSource,
                                           String schemaRegistryBasicAuthUserInfo) {

}
