package org.lafabriquedigitowl.consumer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.lafabriquedigitowl.utils.ShutdownHookManager;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.StreamSupport;

public abstract class AbstractConsumer<K, V> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractConsumer.class);
    protected final Consumer<K, V> consumer;
    protected final java.util.function.Consumer<Throwable> exceptionHandler;
    protected final java.util.function.Consumer<ConsumerRecord<K, V>> processor;

    public AbstractConsumer(Consumer<K, V> consumer, java.util.function.Consumer<Throwable> exceptionHandler, java.util.function.Consumer<ConsumerRecord<K, V>> processor) {
        this.consumer = consumer;
        this.exceptionHandler = exceptionHandler;
        this.processor = processor;
    }

    public void subscribe(String topic) {
        consume(() -> consumer.subscribe(Collections.singleton(topic)));
    }

    private void consume(Runnable runBeforePolling) {

        AtomicBoolean isConsumming = new AtomicBoolean(true);

        final Thread mainThread = Thread.currentThread();

        final Thread consumerWakeup = new Thread(() -> {
            consumer.wakeup();
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                logger.error("Unexpected error {}", e);
            }
        });

        // Adding a ShutdownHook to gracefully close consumer and producer
        ShutdownHookManager.get().addShutdownHook(consumerWakeup, 0);


        try {
            runBeforePolling.run();

            while (isConsumming.get()) {
                ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(1000));
                StreamSupport.stream(records.spliterator(), false).forEach(processor);

                //Use async to be faster, but use en callback to log if an issue happens
                consumer.commitAsync((offsets, exception) -> {
                    if (exception != null) {
                        logger.error("Commit failed for offsets {}", offsets, exception);
                    } else {
                        if (logger.isTraceEnabled()) {
                            logger.trace("Offets {} are succesfully committed", offsets);
                        }
                    }
                });
            }
        } catch (WakeupException e) {
            logger.warn("Shutting down...");
        } catch (RuntimeException ex) {
            exceptionHandler.accept(ex);
        } finally {
            isConsumming.set(false);
            ShutdownHookManager.get().removeShutdownHook(consumerWakeup);
            try {
                //Try to commit not yet committed offsets
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }
}
