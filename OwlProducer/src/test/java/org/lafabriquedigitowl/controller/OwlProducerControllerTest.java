package org.lafabriquedigitowl.controller;

import com.lafabriquedigitowl.Owl;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.lafabriquedigitowl.producer.AvroSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
class OwlProducerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    public static class SimpleProducerTestConfig {

        @Bean
        public Producer<String, Owl> producerConfigurations() {
            return new MockProducer<>(true, new StringSerializer(), new AvroSerializer<>());
        }
    }

    @Test
    void postMessageForTopic()
            throws Exception {
        mockMvc.perform(post("/owl/send?id=test&name=name&species=barn&age=2&haveRing=true"))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(get("/owl/status"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(result.getResponse().getContentAsString(), "Messages to send 1 ; Messages sent successfully 1 ; Messages with error 0");
    }
}
