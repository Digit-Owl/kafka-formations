package org.lafabriquedigitowl.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SslConfiguration {
    private String trustStoreLocation;
    private String trustStorePassword;
}
