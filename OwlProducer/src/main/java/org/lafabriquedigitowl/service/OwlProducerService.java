package org.lafabriquedigitowl.service;

import com.lafabriquedigitowl.Owl;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.lafabriquedigitowl.config.TemplateConfiguration;
import org.lafabriquedigitowl.producer.OwlProducer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Log4j2
public class OwlProducerService {

    private static final Logger logger = LoggerFactory.getLogger(OwlProducerService.class);

    private final TemplateConfiguration templateConfiguration;

    private final Producer<String, Owl> producer;

    private final AtomicLong messageToSend = new AtomicLong();
    private final AtomicLong messageSentSuccessfully = new AtomicLong();
    private final AtomicLong messageWithError = new AtomicLong();

    public OwlProducerService(TemplateConfiguration templateConfiguration, Producer<String, Owl> producer) {
        this.templateConfiguration = templateConfiguration;
        this.producer = producer;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stop Producer gracefully");
            producer.flush();
            producer.close(Duration.ofSeconds(10L));
        }));
    }

    public Future<RecordMetadata> sendMessage(Owl owl) {
        messageToSend.incrementAndGet();

        ProducerRecord<String, Owl> producerOwlRecord = new ProducerRecord<>(templateConfiguration.defaultTopic(), owl.getId().toString(), owl);

        OwlProducer owlProducer = new OwlProducer(producer, (recordMetadata, exception) -> {
            if (exception == null) {
                messageSentSuccessfully.incrementAndGet();
                logger.info(displayMetadataForARecord(producerOwlRecord, recordMetadata));
            } else {
                messageWithError.incrementAndGet();
                logger.error(displayErrorForARecord(producerOwlRecord, exception));
            }
        });

        // Async send : the answer is managed by the Biconsumer passed as Callback param to OwlProducer constructor
        return owlProducer.send(producerOwlRecord);
    }

    public String status() {
        return String.format(
                "Messages to send %d ; Messages sent successfully %d ; Messages with error %d",
                messageToSend.get(), messageSentSuccessfully.get(), messageWithError.get());
    }

    private String displayMetadataForARecord(
            final ProducerRecord<String, Owl> producerRecord, final RecordMetadata recordMetadata
    ) {
        return String.format(
                "key=%s, value=%s sent to topic %s (partition %d offset %d at time %s",
                producerRecord.key(), producerRecord.value(), recordMetadata.topic(), recordMetadata.partition(),
                recordMetadata.offset(), new Date(recordMetadata.timestamp()));
    }

    private String displayErrorForARecord(final ProducerRecord<String, Owl> producerRecord, Exception exception) {
        return String.format(
                "Error to produce message key=%s, value=%s :: exception %s",
                producerRecord.key(), producerRecord.value(), exception.getMessage());
    }

}
