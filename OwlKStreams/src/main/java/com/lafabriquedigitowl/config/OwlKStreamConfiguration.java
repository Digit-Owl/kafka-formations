package com.lafabriquedigitowl.config;

import com.lafabriquedigitowl.Owl;
import io.confluent.common.utils.TestUtils;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
public class OwlKStreamConfiguration {

    @Autowired
    private SpringKafkaConfiguration springKafkaConfig;

    @Autowired
    public SaslConfiguration saslConfiguration;

    @Autowired
    public SslConfiguration sslConfiguration;

    @Autowired
    public KafkaPropertiesConfiguration kafkaProperties;

    @Bean
    public Properties kstreamProperties() {
        Properties props = new Properties();

        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, springKafkaConfig.bootstrapServers());

        if (StringUtils.hasText(springKafkaConfig.securityProtocol())) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, springKafkaConfig.securityProtocol());
        }

        if (StringUtils.hasText(saslConfiguration.mechanism()) && StringUtils.hasText(saslConfiguration.jaasConfig())) {
            props.put("sasl.mechanism", saslConfiguration.mechanism());
            props.put("sasl.jaas.config", saslConfiguration.jaasConfig());
        }

        if (StringUtils.hasText(sslConfiguration.trustStoreLocation()) && StringUtils.hasText(sslConfiguration.trustStorePassword())) {
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslConfiguration.trustStoreLocation());
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, sslConfiguration.trustStorePassword());
        }

        props.put("schema.registry.url", kafkaProperties.schemaRegistryUrl());

        if (StringUtils.hasText(kafkaProperties.basicAuthCredentialsSource()) && StringUtils.hasText(kafkaProperties.schemaRegistryBasicAuthUserInfo())) {
            props.put("basic.auth.credentials.source", kafkaProperties.basicAuthCredentialsSource());
            props.put("basic.auth.user.info", kafkaProperties.schemaRegistryBasicAuthUserInfo());
        }

        props.put("specific.avro.reader", true);

        //KStreams
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, springKafkaConfig.applicationId());
        props.put(StreamsConfig.STATE_DIR_CONFIG, TestUtils.tempDirectory().getPath()); // To change
        props.put("default.deserialization.exception.handler", LogAndContinueExceptionHandler.class);

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
