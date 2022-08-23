package org.lafabriquedigitowl.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumerConfiguration {
    private String groupId;
    private String enableAutoCommit;
    private String autoCommitInterval;
    private String autoOffsetReset;
}
