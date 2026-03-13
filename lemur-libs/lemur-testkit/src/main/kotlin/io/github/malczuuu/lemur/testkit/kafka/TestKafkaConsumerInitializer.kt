package io.github.malczuuu.lemur.testkit.kafka

import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextCustomizer
import org.springframework.test.context.MergedContextConfiguration

internal class TestKafkaConsumerInitializer(private val testClass: Class<*>) : ContextCustomizer {

    override fun customizeContext(context: ConfigurableApplicationContext, mergedConfig: MergedContextConfiguration) {
        val registry = context.beanFactory as BeanDefinitionRegistry
        TestKafkaConsumerRegistrar(testClass, context.environment, context.beanFactory)
            .registerBeanDefinitions(registry)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TestKafkaConsumerInitializer) return false
        return testClass == other.testClass
    }

    override fun hashCode(): Int = testClass.hashCode()
}
