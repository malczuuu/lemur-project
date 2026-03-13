package io.github.malczuuu.lemur.testkit.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration
import java.util.Collections

internal class DefaultTestKafkaConsumer(private val consumer: KafkaConsumer<String, String>, topic: String) :
    TestKafkaConsumer {

    init {
        consumer.subscribe(listOf(topic))
    }

    override fun poll(timeout: Duration): List<ConsumerRecord<String, String>> {
        val result = mutableListOf<ConsumerRecord<String, String>>()
        consumer.poll(timeout).forEach { result.add(it) }
        return Collections.unmodifiableList(result)
    }

    override fun clear(timeout: Duration) {
        val deadlineMillis = System.currentTimeMillis() + timeout.toMillis()
        while (System.currentTimeMillis() < deadlineMillis) {
            if (consumer.poll(Duration.ofMillis(100)).isEmpty) {
                break
            }
        }
    }

    override fun close() {
        consumer.close()
    }
}
