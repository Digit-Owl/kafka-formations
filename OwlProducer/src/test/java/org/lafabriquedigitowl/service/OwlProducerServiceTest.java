package org.lafabriquedigitowl.service;

import com.lafabriquedigitowl.Owl;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.lafabriquedigitowl.producer.AvroSerializer;
import org.lafabriquedigitowl.service.OwlProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
class OwlProducerServiceTest {

    @TestConfiguration
    public static class SimpleProducerTestConfig {

        @Bean
        public Producer<String, Owl> producerConfigurations() {
            return new MockProducer<>(true, new StringSerializer(), new AvroSerializer<>());
        }
    }

    @Autowired
    private OwlProducerService producerService;

    @Autowired
    private Producer<String, Owl> producerConfigurations;

    private static final Owl owl = Owl.newBuilder()
            .setId("42")
            .setAge(2)
            .setSpecies("Barn")
            .setName("Stanley")
            .setHaveRing(true)
            .build();

    @Test
    void sendMessage() {
        producerService.sendMessage(owl);
        assertEquals(1, ((MockProducer<String, Owl>) producerConfigurations).history().size());
    }
}
