package org.lafabriquedigitowl.config;

import com.lafabriquedigitowl.Owl;
import io.confluent.common.utils.TestUtils;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.Setter;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
@Setter
public class OwlKStreamStatefulConfig {
    private String bootstrapServers;

    private String securityProtocol;

    private String applicationId;

    @Bean
    @ConfigurationProperties(prefix = "spring.kafka.sasl")
    public SaslConfiguration saslConfiguration() {
        return new SaslConfiguration();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.kafka.ssl")
    public SslConfiguration sslConfiguration() {
        return new SslConfiguration();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.kafka.properties")
    public KafkaProperties kafkaProperties() {
        return new KafkaProperties();
    }

    @Bean
    public Properties kstreamProperties() {
        Properties props = new Properties();

        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        if (StringUtils.hasText(securityProtocol)) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        }

        if (StringUtils.hasText(saslConfiguration().getMechanism()) && StringUtils.hasText(saslConfiguration().getJaasConfig())) {
            props.put("sasl.mechanism", saslConfiguration().getMechanism());
            props.put("sasl.jaas.config", saslConfiguration().getJaasConfig());
        }

        if (StringUtils.hasText(sslConfiguration().getTrustStoreLocation()) && StringUtils.hasText(sslConfiguration().getTrustStorePassword())) {
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslConfiguration().getTrustStoreLocation());
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, sslConfiguration().getTrustStorePassword());
        }

        props.put("schema.registry.url", kafkaProperties().getSchemaRegistryUrl());

        if (StringUtils.hasText(kafkaProperties().getBasicAuthCredentialsSource()) && StringUtils.hasText(kafkaProperties().getSchemaRegistryBasicAuthUserInfo())) {
            props.put("basic.auth.credentials.source", kafkaProperties().getBasicAuthCredentialsSource());
            props.put("basic.auth.user.info", kafkaProperties().getSchemaRegistryBasicAuthUserInfo());
        }

        props.put("specific.avro.reader", true);

        //KStreams
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(StreamsConfig.STATE_DIR_CONFIG, TestUtils.tempDirectory().getPath()); // To change
        props.put("default.deserialization.exception.handler", LogAndContinueExceptionHandler.class);

        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());

        return props;
    }

    @Bean
    public SpecificAvroSerde<Owl> owlSpecificAvroSerde() {
        final SpecificAvroSerde<Owl> owlSpecificAvroSerde = new SpecificAvroSerde<>();
        owlSpecificAvroSerde.configure(kstreamProperties()
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                e -> String.valueOf(e.getKey()),
                                e -> String.valueOf(e.getValue()),
                                (prev, next) -> next, HashMap::new)), false);
        return owlSpecificAvroSerde;
    }
}
