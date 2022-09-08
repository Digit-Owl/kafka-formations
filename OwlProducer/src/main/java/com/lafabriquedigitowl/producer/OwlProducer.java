package com.lafabriquedigitowl.producer;

import com.lafabriquedigitowl.Owl;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.function.BiConsumer;

public class OwlProducer extends AbstractProducer<String, Owl> {

    public OwlProducer(Producer<String, Owl> producer, BiConsumer<RecordMetadata, Exception> onCompletion) {
        super(producer, onCompletion);
    }
}
