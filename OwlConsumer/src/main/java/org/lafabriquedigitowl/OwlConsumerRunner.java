package org.lafabriquedigitowl;

import com.lafabriquedigitowl.Owl;
import org.apache.kafka.clients.consumer.Consumer;
import org.lafabriquedigitowl.consumer.OwlConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OwlConsumerRunner implements CommandLineRunner {

    private final Consumer<String, Owl> stringOwlConsumer;

    @Value("${spring.kafka.template.default-topic}")
    private String topic;

    public final AtomicInteger exceptionCounter = new AtomicInteger();
    public final AtomicInteger successCounter = new AtomicInteger();

    public OwlConsumerRunner(Consumer<String, Owl> stringOwlConsumer) {
        this.stringOwlConsumer = stringOwlConsumer;
    }

    @Override
    public void run(String... args) {
        try {
            OwlConsumer objectConsumer = new OwlConsumer(stringOwlConsumer, exceptionCounter, successCounter);
            objectConsumer.subscribe(topic);
        } catch (Exception e) {
            // Do nothing
        }
    }
}
