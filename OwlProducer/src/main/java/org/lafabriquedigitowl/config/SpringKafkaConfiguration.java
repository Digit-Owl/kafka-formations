package org.lafabriquedigitowl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka")
public record SpringKafkaConfiguration(String bootstrapServers, String securityProtocol) {
}
