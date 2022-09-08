package com.lafabriquedigitowl.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "spring.kafka.consumer")
public record ConsumerConfiguration(String groupId, String enableAutoCommit, String autoCommitInterval,
                                    String autoOffsetReset) {

}
