package org.lafabriquedigitowl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.kafka.template")
public record TemplateConfiguration(String defaultTopic) {
}