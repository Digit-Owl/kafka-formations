package org.lafabriquedigitowl.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KafkaProperties {
    private String schemaRegistryUrl;
    private String basicAuthCredentialsSource;
    private String schemaRegistryBasicAuthUserInfo;
}
