package io.github.malczuuu.lemur.testkit.kafka

import io.github.malczuuu.lemur.testkit.annotation.ContainerTest
import io.github.malczuuu.lemur.testkit.annotation.TestListener
import io.github.malczuuu.lemur.testkit.container.KafkaAwareTest
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaOperations
import org.springframework.test.context.ActiveProfiles
import java.nio.charset.StandardCharsets
import java.time.Duration

@ActiveProfiles(profiles = ["test"])
@ContainerTest
@SpringBootTest(classes = [TestKafkaApplication::class])
internal class TestKafkaConsumerTests : KafkaAwareTest {

    @Autowired
    private lateinit var kafkaOperations: KafkaOperations<String, String>

    @TestListener(TestKafkaApplication.TOPIC_IN)
    private lateinit var consumerIn: TestKafkaConsumer

    @TestListener(TestKafkaApplication.TOPIC_OUT)
    private lateinit var consumerOut: TestKafkaConsumer

    @BeforeEach
    fun beforeEach() {
        consumerIn.clear()
        consumerOut.clear()
    }

    @Test
    fun givenPublishedMessage_whenPoll_thenReturnsRecord() {
        kafkaOperations.send(TestKafkaApplication.TOPIC_IN, "key-1", "value-1").join()

        await()
            .atMost(Duration.ofSeconds(15))
            .pollInterval(Duration.ofMillis(200))
            .untilAsserted {
                val records = consumerIn.poll(Duration.ofMillis(500))
                assertThat(records).anySatisfy { r ->
                    assertThat(r.key()).isEqualTo("key-1")
                    assertThat(r.value()).isEqualTo("value-1")
                }
            }
    }

    @Test
    fun givenPublishedMessages_whenClear_thenSubsequentPollReturnsEmpty() {
        kafkaOperations.send(TestKafkaApplication.TOPIC_IN, "key-2", "value-2").join()

        await()
            .atMost(Duration.ofSeconds(15))
            .pollInterval(Duration.ofMillis(200))
            .untilAsserted { assertThat(consumerIn.poll(Duration.ofMillis(500))).isNotEmpty() }

        consumerIn.clear()

        assertThat(consumerIn.poll(Duration.ofMillis(300))).isEmpty()
    }

    @Test
    fun givenPublishedMessageWithHeader_whenPoll_thenHeaderIsAccessible() {
        val record =
            ProducerRecord<String, String>(TestKafkaApplication.TOPIC_IN, "key-3", "value-3")
        record.headers().add("event_type", "TestEvent".toByteArray(StandardCharsets.UTF_8))
        kafkaOperations.send(record).join()

        await()
            .atMost(Duration.ofSeconds(15))
            .pollInterval(Duration.ofMillis(200))
            .untilAsserted {
                val records = consumerIn.poll(Duration.ofMillis(500))
                assertThat(records).anySatisfy { r ->
                    assertThat(r.headers().lastHeader("event_type"))
                        .isNotNull()
                        .satisfies({ h: Header ->
                            assertThat(String(h.value(), StandardCharsets.UTF_8))
                                .isEqualTo("TestEvent")
                        })
                }
            }
    }

    @Test
    fun givenMessageOnTopicIn_whenForwarded_thenAppearsOnTopicOutOnly() {
        consumerIn.clear()
        consumerOut.clear()
        kafkaOperations.send(TestKafkaApplication.TOPIC_IN, "key-x", "msg-x").join()

        await()
            .atMost(Duration.ofSeconds(15))
            .pollInterval(Duration.ofMillis(200))
            .untilAsserted {
                assertThat(consumerIn.poll(Duration.ofMillis(500))).isEmpty()
                assertThat(consumerOut.poll(Duration.ofMillis(500)))
                    .anySatisfy { r -> assertThat(r.value()).isEqualTo("forwarded-msg-x") }
            }
    }

    @Test
    fun givenNoTopicConsumerFields_whenCreateContextCustomizer_thenReturnsNull() {
        val factory = TestKafkaConsumerContextCustomizerFactory()
        assertThat(factory.createContextCustomizer(NoListenerClass::class.java, emptyList()))
            .isNull()
    }

    @Test
    fun givenTopicConsumerField_whenCreateContextCustomizer_thenReturnsCustomizer() {
        val factory = TestKafkaConsumerContextCustomizerFactory()
        assertThat(
            factory.createContextCustomizer(TestKafkaConsumerTests::class.java, emptyList()),
        )
            .isNotNull()
    }

    class NoListenerClass
}
