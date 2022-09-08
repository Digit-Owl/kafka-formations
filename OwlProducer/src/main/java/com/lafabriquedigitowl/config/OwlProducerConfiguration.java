package com.lafabriquedigitowl.config;

import com.lafabriquedigitowl.Owl;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class OwlProducerConfiguration {

    @Autowired
    private SpringKafkaConfiguration springKafkaConfiguration;

    @Autowired
    public SaslConfiguration saslConfiguration;

    @Autowired
    public SslConfiguration sslConfiguration;

    @Autowired
    public KafkaPropertiesConfiguration kafkaPropertiesConfiguration;

    @Bean
    public Map<String, Object> producerConfigMap() {
        Map<String, Object> props = new HashMap<>();

        //Minimum configuration
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, springKafkaConfiguration.bootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        //Required due to Owl usage
        props.put("schema.registry.url", kafkaPropertiesConfiguration.schemaRegistryUrl());

        if (StringUtils.hasText(springKafkaConfiguration.securityProtocol())) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, springKafkaConfiguration.securityProtocol());
        }

        if (StringUtils.hasText(saslConfiguration.mechanism()) && StringUtils.hasText(saslConfiguration.jaasConfig())) {
            props.put("sasl.mechanism", saslConfiguration.mechanism());
            props.put("sasl.jaas.config", saslConfiguration.jaasConfig());
        }

        if (StringUtils.hasText(sslConfiguration.trustStoreLocation()) && StringUtils.hasText(sslConfiguration.trustStorePassword())) {
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslConfiguration.trustStoreLocation());
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, sslConfiguration.trustStorePassword());
        }

        if (StringUtils.hasText(kafkaPropertiesConfiguration.basicAuthCredentialsSource()) && StringUtils.hasText(kafkaPropertiesConfiguration.schemaRegistryBasicAuthUserInfo())) {
            props.put("basic.auth.credentials.source", kafkaPropertiesConfiguration.basicAuthCredentialsSource());
            props.put("basic.auth.user.info", kafkaPropertiesConfiguration.schemaRegistryBasicAuthUserInfo());
        }

        props.put("client.id", "testowl-producer-1");

        return props;
    }

    @Bean
    public Producer<String, Owl> producerConfigurations() {
        return new KafkaProducer<>(producerConfigMap());
    }

}
