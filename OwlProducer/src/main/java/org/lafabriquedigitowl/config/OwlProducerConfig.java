package org.lafabriquedigitowl.config;

import com.lafabriquedigitowl.Owl;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.Setter;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
@Setter
public class OwlProducerConfig {

    private String bootstrapServers;

    private String securityProtocol;

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
    public Map<String, Object> producerConfigMap() {
        Map<String, Object> props = new HashMap<>();

        //Minimum configuration
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        //Required due to Owl usage
        props.put("schema.registry.url", kafkaProperties().getSchemaRegistryUrl());

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

        if (StringUtils.hasText(kafkaProperties().getBasicAuthCredentialsSource()) && StringUtils.hasText(kafkaProperties().getSchemaRegistryBasicAuthUserInfo())) {
            props.put("basic.auth.credentials.source", kafkaProperties().getBasicAuthCredentialsSource());
            props.put("basic.auth.user.info", kafkaProperties().getSchemaRegistryBasicAuthUserInfo());
        }

        props.put("client.id", "testowl-producer-1");

        return props;
    }

    @Bean
    public Producer<String, Owl> producerConfigurations() {
        return new KafkaProducer<>(producerConfigMap());
    }

}
