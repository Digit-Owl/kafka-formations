package org.lafabriquedigitowl.service;

import com.lafabriquedigitowl.Owl;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.lafabriquedigitowl.producer.AvroSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
class OwlProducerServiceErrorTest {

    @TestConfiguration
    public static class SimpleProducerTestConfig {

        @Bean
        public Producer<String, Owl> producerConfigurations() {
            return new MockProducer<>(false, new StringSerializer(), new AvroSerializer<>());
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
        Future<RecordMetadata> recordMetadataFuture = producerService.sendMessage(owl);
        RuntimeException e = new RuntimeException("Oups something went wrong");

        ((MockProducer<String, Owl>) producerConfigurations).errorNext(e);

        try {
            recordMetadataFuture.get();
        } catch (ExecutionException | InterruptedException ex) {
            assertEquals(e, ex.getCause());
            assertEquals("java.lang.RuntimeException: Oups something went wrong", ex.getMessage());
        }
        assertTrue(recordMetadataFuture.isDone());
    }
}
