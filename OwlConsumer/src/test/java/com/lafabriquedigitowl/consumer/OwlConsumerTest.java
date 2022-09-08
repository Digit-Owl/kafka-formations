package com.lafabriquedigitowl.consumer;

import com.lafabriquedigitowl.Owl;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class OwlConsumerTest {

    private static final String topicName = "test";

    private final MockConsumer<String, Owl> mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);

    @AfterEach
    void clean() {
        mockConsumer.wakeup();
        mockConsumer.close();
    }

    @Test
    void consume() {
        ConsumerRecord<String, Owl> record = new ConsumerRecord<>(
                topicName, 0, 0, "key", Owl.newBuilder()
                .setId("42")
                .setName("Stanley")
                .setSpecies("Barn")
                .setAge(42)
                .setHaveRing(false)
                .build());

        Runtime.getRuntime().removeShutdownHook(Thread.currentThread());

        HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
        TopicPartition topicPartition = new TopicPartition(topicName, 0);
        startOffsets.put(topicPartition, 0L);

        mockConsumer.schedulePollTask(() -> {
            mockConsumer.rebalance(Collections.singletonList(new TopicPartition(topicName, 0)));
            mockConsumer.addRecord(record);

        });
        mockConsumer.schedulePollTask(mockConsumer::wakeup);
        mockConsumer.schedulePollTask(mockConsumer::close);

        mockConsumer.updateBeginningOffsets(startOffsets);

        AtomicInteger errorCounter = new AtomicInteger();
        AtomicInteger successCounter = new AtomicInteger();

        OwlConsumer objectConsumer = new OwlConsumer(mockConsumer, errorCounter, successCounter);

        objectConsumer.subscribe(topicName);

        assertEquals(0, errorCounter.get());
        assertEquals(1, successCounter.get());

        assertTrue(mockConsumer.closed());

    }
}
