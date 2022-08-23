package org.lafabriquedigitowl.producer;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;
import java.util.function.BiConsumer;

public abstract class AbstractProducer<K, V> {

    protected final Producer<K, V> producer;
    protected final BiConsumer<RecordMetadata, Exception> onCompletion;

    protected AbstractProducer(Producer<K, V> producer, BiConsumer<RecordMetadata, Exception> onCompletion) {
        this.producer = producer;
        this.onCompletion = onCompletion;
    }

    public Future<RecordMetadata> send(ProducerRecord<K, V> producerRecord) {
        return producer.send(producerRecord, onCompletion::accept);
    }
}
