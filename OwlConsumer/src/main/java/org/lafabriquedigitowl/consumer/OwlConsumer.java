package org.lafabriquedigitowl.consumer;

import com.lafabriquedigitowl.Owl;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.Consumer;

import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class OwlConsumer extends AbstractConsumer<String, Owl> {

    public OwlConsumer(Consumer<String, Owl> consumer, AtomicInteger exceptionCounter, AtomicInteger successCounter) {
        super(consumer, throwable -> {
            exceptionCounter.incrementAndGet();
            log.error(throwable);
        }, record -> {
            successCounter.incrementAndGet();
            log.info("{} [{}] offset={}, key={}, value={}",
                    record.topic(), record.partition(),
                    record.offset(), record.key(), record.value());
        });
    }

}