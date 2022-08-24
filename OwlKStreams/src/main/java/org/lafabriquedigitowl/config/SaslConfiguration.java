package org.lafabriquedigitowl.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaslConfiguration {
    private String mechanism;
    private String jaasConfig;
}
