package org.lafabriquedigitowl;

import com.lafabriquedigitowl.Owl;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.streams.KafkaStreams;
import org.lafabriquedigitowl.kstreams.OwlKStreamStatefulTopology;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@Component
@Log4j2
public class OwlKStreamStatefulRunner implements CommandLineRunner {

    @Value("${spring.kafka.input-topic-name}")
    private String inputTopicName;

    @Value("${spring.kafka.output-topic-name}")
    private String outputTopicName;

    private final SpecificAvroSerde<Owl> owlSpecificAvroSerde;

    private final Properties kstreamProperties;

    public OwlKStreamStatefulRunner(SpecificAvroSerde<Owl> owlSpecificAvroSerde, Properties kstreamProperties) {
        this.owlSpecificAvroSerde = owlSpecificAvroSerde;
        this.kstreamProperties = kstreamProperties;
    }

    @Override
    public void run(String... args) {
        try {
            OwlKStreamStatefulTopology owlKStreamTopology = new OwlKStreamStatefulTopology(inputTopicName, outputTopicName, kstreamProperties, owlSpecificAvroSerde);

            KafkaStreams kafkaStreams = new KafkaStreams(owlKStreamTopology.buildTopology(), kstreamProperties);
            final CountDownLatch latch = new CountDownLatch(1);

            Runtime.getRuntime().addShutdownHook(new Thread("kstreams-shutdown-hook") {
                @Override
                public void run() {
                    kafkaStreams.close(Duration.ofSeconds(5));
                    latch.countDown();
                }
            });

            kafkaStreams.start();
            latch.await();

        } catch (Exception e) {
            log.error(e);
        }
    }


}
