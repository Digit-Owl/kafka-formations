package com.lafabriquedigitowl.kstreams;

import com.lafabriquedigitowl.Owl;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OwlKStreamTopologyTest {

    private static final String inputTopicName = "input";
    private static final String outputTopicName = "output";
    private static final String MOCK_SCHEMA_REGISTRY_URL = "mock://dummyurl";

    private TestInputTopic<String, Owl> inputTopic;
    private TestOutputTopic<String, Owl> outputTopic;

    @BeforeEach
    void setUp() {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        props.put("schema.registry.url", MOCK_SCHEMA_REGISTRY_URL);

        final SpecificAvroSerde<Owl> owlSpecificAvroSerde = new SpecificAvroSerde<>();
        owlSpecificAvroSerde.configure(props
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                e -> String.valueOf(e.getKey()),
                                e -> String.valueOf(e.getValue()),
                                (prev, next) -> next, HashMap::new)), false);

        OwlKStreamTopology owlKStreamTopology = new OwlKStreamTopology(inputTopicName, outputTopicName, props, owlSpecificAvroSerde);

        TopologyTestDriver testDriver = new TopologyTestDriver(owlKStreamTopology.buildTopology(), props);

        inputTopic = testDriver.createInputTopic(inputTopicName, Serdes.String().serializer(), owlSpecificAvroSerde.serializer());
        outputTopic = testDriver.createOutputTopic(outputTopicName, Serdes.String().deserializer(), owlSpecificAvroSerde.deserializer());
    }

    @Test
    public void shouldFilter() {

        for (int i = 0; i < 20; i++) {
            inputTopic.pipeInput("key" + i, Owl.newBuilder().setId("" + i).setAge(i).setSpecies("Barn").setName("Stanley" + i).setHaveRing(i % 5 != 0).build());
        }

        assertEquals(4, outputTopic.readValuesToList().size());
    }
}