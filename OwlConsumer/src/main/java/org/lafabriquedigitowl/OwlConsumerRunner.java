package org.lafabriquedigitowl;

import com.lafabriquedigitowl.Owl;
import org.apache.kafka.clients.consumer.Consumer;
import org.lafabriquedigitowl.config.TemplateConfiguration;
import org.lafabriquedigitowl.consumer.OwlConsumer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OwlConsumerRunner implements CommandLineRunner {

    private final Consumer<String, Owl> stringOwlConsumer;

    private final TemplateConfiguration templateConfiguration;

    public final AtomicInteger exceptionCounter = new AtomicInteger();
    public final AtomicInteger successCounter = new AtomicInteger();

    public OwlConsumerRunner(Consumer<String, Owl> stringOwlConsumer, TemplateConfiguration templateConfiguration) {
        this.stringOwlConsumer = stringOwlConsumer;
        this.templateConfiguration = templateConfiguration;
    }

    @Override
    public void run(String... args) {
        try {
            OwlConsumer objectConsumer = new OwlConsumer(stringOwlConsumer, exceptionCounter, successCounter);
            objectConsumer.subscribe(templateConfiguration.defaultTopic());
        } catch (Exception e) {
            // Do nothing
        }
    }
}
