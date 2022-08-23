package org.lafabriquedigitowl.consumer;

import com.lafabriquedigitowl.Owl;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lafabriquedigitowl.OwlConsumerRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
class OwlConsumerRunnerTest {

    @TestConfiguration
    public static class SimpleConsumerTestConfig {

        @Bean
        public Properties consumerProperties() {
            return new Properties();
        }

        @Bean
        public Consumer<String, Owl> stringOwlConsumer() {
            MockConsumer<String, Owl> mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);

            ConsumerRecord<String, Owl> record = new ConsumerRecord<>(
                    "test", 0, 0, "key", Owl.newBuilder()
                    .setId("42")
                    .setName("Stanley")
                    .setSpecies("Barn")
                    .setAge(42)
                    .setHaveRing(false)
                    .build());

            HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
            TopicPartition topicPartition = new TopicPartition("test", 0);
            startOffsets.put(topicPartition, 0L);

            mockConsumer.schedulePollTask(() -> {
                mockConsumer.rebalance(Collections.singletonList(new TopicPartition("test", 0)));
                mockConsumer.addRecord(record);

            });
            mockConsumer.schedulePollTask(mockConsumer::wakeup);

            mockConsumer.updateBeginningOffsets(startOffsets);

            return mockConsumer;
        }
    }

    @Autowired
    private Consumer<String, Owl> stringOwlConsumer;

    @Autowired
    private OwlConsumerRunner owlConsumerRunner;

    @Test
    void consume() {
        assertEquals(0, owlConsumerRunner.exceptionCounter.get());
        assertEquals(1, owlConsumerRunner.successCounter.get());

        assertTrue(((MockConsumer<String, Owl>) stringOwlConsumer).closed());
    }
}
