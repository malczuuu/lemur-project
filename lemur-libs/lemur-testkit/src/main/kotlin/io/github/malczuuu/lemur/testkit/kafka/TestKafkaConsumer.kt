package io.github.malczuuu.lemur.testkit.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import java.time.Duration

interface TestKafkaConsumer : AutoCloseable {

    fun poll(timeout: Duration): List<ConsumerRecord<String, String>>

    fun clear() = clear(Duration.ofMillis(300))

    fun clear(timeout: Duration)

    override fun close()
}
