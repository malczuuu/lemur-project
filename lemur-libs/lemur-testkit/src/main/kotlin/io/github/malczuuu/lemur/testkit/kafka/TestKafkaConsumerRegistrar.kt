package io.github.malczuuu.lemur.testkit.kafka

import io.github.malczuuu.lemur.testkit.annotation.TestListener
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.support.AutowireCandidateQualifier
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.boot.kafka.autoconfigure.KafkaConnectionDetails
import org.springframework.core.env.Environment
import java.util.UUID

internal class TestKafkaConsumerRegistrar(
    private val testClass: Class<*>,
    private val environment: Environment,
    private val beanFactory: BeanFactory,
) {

    fun registerBeanDefinitions(registry: BeanDefinitionRegistry) {
        var clazz: Class<*>? = testClass
        while (clazz != null && clazz != Any::class.java) {
            for (field in clazz.declaredFields) {
                val annotation = field.getAnnotation(TestListener::class.java)
                if (annotation != null) {
                    val rawValue = annotation.value
                    val resolvedTopic = environment.resolveRequiredPlaceholders(rawValue)
                    val beanName = "testKafkaConsumer[$rawValue]"
                    if (!registry.containsBeanDefinition(beanName)) {
                        registry.registerBeanDefinition(
                            beanName,
                            buildDefinition(rawValue, resolvedTopic),
                        )
                    }
                }
            }
            clazz = clazz.superclass
        }
    }

    private fun buildDefinition(rawValue: String, resolvedTopic: String): RootBeanDefinition {
        val definition = RootBeanDefinition(TestKafkaConsumer::class.java)
        definition.setInstanceSupplier {
            val connectionDetails = beanFactory.getBean(KafkaConnectionDetails::class.java)
            val bootstrapServers =
                connectionDetails.consumer.bootstrapServers.joinToString(",")
            createClient(bootstrapServers, resolvedTopic)
        }
        definition.setDestroyMethodNames("close")
        definition.addQualifier(AutowireCandidateQualifier(TestListener::class.java, rawValue))
        return definition
    }

    private fun createClient(bootstrapServers: String, resolvedTopic: String): TestKafkaConsumer {
        val props =
            mapOf(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG to getUniqueGroupId(resolvedTopic),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            )
        val consumer = KafkaConsumer(props, StringDeserializer(), StringDeserializer())
        return DefaultTestKafkaConsumer(consumer, resolvedTopic)
    }

    private fun getUniqueGroupId(topic: String): String = "test-consumer.$topic.${UUID.randomUUID()}"
}
