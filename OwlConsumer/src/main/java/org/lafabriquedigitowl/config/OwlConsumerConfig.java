package org.lafabriquedigitowl.config;

import com.lafabriquedigitowl.Owl;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.Setter;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
@Setter
public class OwlConsumerConfig {
    private String bootstrapServers;

    private String securityProtocol;

    @Bean
    @ConfigurationProperties(prefix = "spring.kafka.consumer")
    public ConsumerConfiguration consumerConfiguration() {
        return new ConsumerConfiguration();
    }

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
    public Properties consumerProperties() {
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);

        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerConfiguration().getGroupId());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerConfiguration().getEnableAutoCommit());
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, consumerConfiguration().getAutoCommitInterval());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerConfiguration().getAutoOffsetReset());

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

        return props;
    }

    @Bean
    public Consumer<String, Owl> stringOwlConsumer() {
        return new KafkaConsumer<>(consumerProperties());
    }

}
