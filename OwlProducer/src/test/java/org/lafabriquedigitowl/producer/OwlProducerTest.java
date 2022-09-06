package org.lafabriquedigitowl.producer;

import com.lafabriquedigitowl.Owl;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OwlProducerTest {

    @Test
    void send() {
        MockProducer<String, Owl> mockProducer = new MockProducer<>(
                true, new StringSerializer(), new AvroSerializer<>());

        OwlProducer simpleProducer = new OwlProducer(mockProducer, (recordMetadata, e) -> {

        });

        simpleProducer.send(
                new ProducerRecord<>(
                        "42", Owl.newBuilder()
                        .setId("42")
                        .setAge(2)
                        .setSpecies("Barn")
                        .setName("Stanley")
                        .setHaveRing(true)
                        .build()));

        assertEquals(1, mockProducer.history().size());
    }
}
