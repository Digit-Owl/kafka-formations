package org.lafabriquedigitowl.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-t1.properties")
class OwlProducerConfigTest {

    @Autowired
    OwlProducerConfig owlProducerConfig;

    @Test
    public void testMapConfig() {
        assertEquals("SASL_SSL://localhost:9092", owlProducerConfig.producerConfigMap().get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));

        assertEquals(StringSerializer.class, owlProducerConfig.producerConfigMap().get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(KafkaAvroSerializer.class, owlProducerConfig.producerConfigMap().get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));

        assertEquals("https://schemaregistry", owlProducerConfig.producerConfigMap().get("schema.registry.url"));

        assertEquals("SASL_SSL", owlProducerConfig.producerConfigMap().get(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));

        assertEquals("PLAIN", owlProducerConfig.producerConfigMap().get("sasl.mechanism"));
        assertEquals("org.apache.kafka.common.security.plain.PlainLoginModule   required username='test'   password='pwd';", owlProducerConfig.producerConfigMap().get("sasl.jaas.config"));

        assertEquals("USER_INFO", owlProducerConfig.producerConfigMap().get("basic.auth.credentials.source"));
        assertEquals("testSR:pwdSR", owlProducerConfig.producerConfigMap().get("basic.auth.user.info"));

        assertEquals("testowl-producer-1", owlProducerConfig.producerConfigMap().get("client.id"));
    }

    @Test
    public void producerShouldNotNull() {
        assertNotNull(owlProducerConfig.producerConfigurations());
    }

}