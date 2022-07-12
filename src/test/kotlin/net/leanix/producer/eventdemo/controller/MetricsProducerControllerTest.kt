package net.leanix.producer.eventdemo.controller

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.Collections.singleton


@SpringBootTest
@TestInstance(PER_CLASS)
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092", "spring.cloud.stream.kafka.binder.brokers"],
    topics = ["metric-items"]
)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class MetricsProducerControllerTest {

    val metric: String = "great metric"

    @Autowired
    lateinit var context: WebApplicationContext

    @Autowired
    lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    lateinit var mockMvc: MockMvc
    lateinit var consumer: Consumer<String, String>

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build()
        val configs: Map<String, Any> = HashMap(KafkaTestUtils.consumerProps("localTest", "false", embeddedKafkaBroker))

        consumer = DefaultKafkaConsumerFactory(configs, StringDeserializer(), StringDeserializer()).createConsumer()
        consumer.subscribe(singleton("metric-items"))
    }

    @Test
    fun `produce a metric should end up on metric-items topic`() {

        mockMvc.perform(
            post("/")
                .contentType(TEXT_PLAIN)
                .content(metric)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andReturn()

        val responseMessage: String = KafkaTestUtils
            .getSingleRecord(consumer, "metric-items")
            .value()

        Assertions.assertEquals(metric, responseMessage)
    }
}