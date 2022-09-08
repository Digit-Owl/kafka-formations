package com.lafabriquedigitowl.kstreams;

import com.lafabriquedigitowl.Owl;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Properties;

@Log4j2
public record OwlKStreamStatefulTopology(String inputTopicName, String outputTopicName, Properties kstreamProperties,
                                         SpecificAvroSerde<Owl> owlSpecificAvroSerde) {
    public Topology buildTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        streamsBuilder
                .stream(inputTopicName, Consumed.with(Serdes.String(), owlSpecificAvroSerde))
                .peek((k, v) -> log.info("Observed event: {}::{}", k, v))
                .groupBy((s, owl) -> owl.getSpecies().toString())
                .count()
                .toStream()
                .peek((k, v) -> log.info("Transformed event: {}::{}", k, v))
                .to(outputTopicName, Produced.with(Serdes.String(), Serdes.Long()));

        return streamsBuilder.build();
    }
}
