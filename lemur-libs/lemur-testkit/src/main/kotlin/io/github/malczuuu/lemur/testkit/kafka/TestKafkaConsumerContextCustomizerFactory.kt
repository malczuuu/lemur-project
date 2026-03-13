package io.github.malczuuu.lemur.testkit.kafka

import io.github.malczuuu.lemur.testkit.annotation.TestListener
import org.springframework.test.context.ContextConfigurationAttributes
import org.springframework.test.context.ContextCustomizer
import org.springframework.test.context.ContextCustomizerFactory

internal class TestKafkaConsumerContextCustomizerFactory : ContextCustomizerFactory {

    override fun createContextCustomizer(
        testClass: Class<*>,
        configAttributes: List<ContextConfigurationAttributes>,
    ): ContextCustomizer? {
        if (collectTopicAnnotatedFieldTypes(testClass).isEmpty()) {
            return null
        }
        return TestKafkaConsumerInitializer(testClass)
    }

    private fun collectTopicAnnotatedFieldTypes(testClass: Class<*>): Set<String> {
        val raw = linkedSetOf<String>()
        var clazz: Class<*>? = testClass
        while (clazz != null && clazz != Any::class.java) {
            clazz.declaredFields
                .filter { it.isAnnotationPresent(TestListener::class.java) }
                .map { it.getAnnotation(TestListener::class.java).value }
                .forEach { raw.add(it) }
            clazz = clazz.superclass
        }
        return raw
    }
}
