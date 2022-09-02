package org.lafabriquedigitowl.config;

import com.lafabriquedigitowl.Owl;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Properties;

@Configuration
public class OwlConsumerConfig {

    @Autowired
    private SpringKafkaConfig springKafkaConfig;

    @Autowired
    private ConsumerConfiguration consumerConfiguration;

    @Autowired
    public SaslConfiguration saslConfiguration;

    @Autowired
    public SslConfiguration sslConfiguration;

    @Autowired
    public KafkaProperties kafkaProperties;

    @Bean
    public Properties consumerProperties() {
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, springKafkaConfig.bootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);

        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerConfiguration.groupId());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerConfiguration.enableAutoCommit());
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, consumerConfiguration.autoCommitInterval());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerConfiguration.autoOffsetReset());

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

        return props;
    }

    @Bean
    public Consumer<String, Owl> stringOwlConsumer() {
        return new KafkaConsumer<>(consumerProperties());
    }

}
