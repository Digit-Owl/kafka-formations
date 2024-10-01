package org.lafabriquedigitowl.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lafabriquedigitowl.Owl;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.Consumer;

import java.util.concurrent.atomic.AtomicInteger;


public class OwlConsumer extends AbstractConsumer<String, Owl> {

    private static final Logger logger = LoggerFactory.getLogger(OwlConsumer.class);
    public OwlConsumer(Consumer<String, Owl> consumer, AtomicInteger exceptionCounter, AtomicInteger successCounter) {
        super(consumer, throwable -> {
            exceptionCounter.incrementAndGet();
            //logger.error(throwable);
        }, record -> {
            successCounter.incrementAndGet();
            logger.info("{} [{}] offset={}, key={}, value={}",
                    record.topic(), record.partition(),
                    record.offset(), record.key(), record.value());
        });
    }

}