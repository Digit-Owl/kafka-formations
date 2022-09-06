package org.lafabriquedigitowl.kstreams;

import com.lafabriquedigitowl.Owl;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Properties;

public record OwlKStreamTopology(String inputTopicName, String outputTopicName, Properties kstreamProperties,
                                 SpecificAvroSerde<Owl> owlSpecificAvroSerde) {
    public Topology buildTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        streamsBuilder
                .stream(inputTopicName, Consumed.with(Serdes.String(), owlSpecificAvroSerde))
                .filter(((key, value) -> !value.getHaveRing()))
                .to(outputTopicName, Produced.with(Serdes.String(), owlSpecificAvroSerde));
        return streamsBuilder.build();
    }
}
