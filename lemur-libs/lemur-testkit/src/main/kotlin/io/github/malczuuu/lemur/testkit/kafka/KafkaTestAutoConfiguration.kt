package io.github.malczuuu.lemur.testkit.kafka

import org.awaitility.Awaitility.await
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import java.time.Duration

@AutoConfiguration
@ConditionalOnBean(KafkaListenerEndpointRegistry::class)
class KafkaTestAutoConfiguration {

    @Bean
    fun kafkaAssignmentAwaiter(registry: KafkaListenerEndpointRegistry): ApplicationListener<ContextRefreshedEvent> =
        ApplicationListener { registry.awaitAssignment() }

    private fun KafkaListenerEndpointRegistry.awaitAssignment() {
        await()
            .atMost(Duration.ofSeconds(30))
            .pollInterval(Duration.ofMillis(200))
            .until { hasAllPartitionsAssigned() }
    }

    private fun KafkaListenerEndpointRegistry.hasAllPartitionsAssigned(): Boolean =
        allListenerContainers.all { container ->
            val partitions = container.assignedPartitions
            container.isRunning && !partitions.isNullOrEmpty()
        }
}
