package org.lafabriquedigitowl;

import com.lafabriquedigitowl.Owl;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.streams.KafkaStreams;
import org.lafabriquedigitowl.config.SpringKafkaConfiguration;
import org.lafabriquedigitowl.kstreams.OwlKStreamTopology;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@Component
@Log4j2
public class OwlKStreamRunner implements CommandLineRunner {

    private final SpringKafkaConfiguration springKafkaConfiguration;

    private final SpecificAvroSerde<Owl> owlSpecificAvroSerde;

    private final Properties kstreamProperties;

    public OwlKStreamRunner(SpringKafkaConfiguration springKafkaConfiguration, SpecificAvroSerde<Owl> owlSpecificAvroSerde, Properties kstreamProperties) {
        this.springKafkaConfiguration = springKafkaConfiguration;
        this.owlSpecificAvroSerde = owlSpecificAvroSerde;
        this.kstreamProperties = kstreamProperties;
    }

    @Override
    public void run(String... args) {
        try {
            OwlKStreamTopology owlKStreamTopology = new OwlKStreamTopology(springKafkaConfiguration.inputTopicName(), springKafkaConfiguration.outputTopicName(), kstreamProperties, owlSpecificAvroSerde);

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
            //log.error(e);
        }
    }


}
