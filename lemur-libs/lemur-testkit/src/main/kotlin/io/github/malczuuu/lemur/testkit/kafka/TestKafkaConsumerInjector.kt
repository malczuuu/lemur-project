package io.github.malczuuu.lemur.testkit.kafka

import io.github.malczuuu.lemur.testkit.annotation.TestListener
import org.springframework.test.context.TestContext
import org.springframework.test.context.TestExecutionListener

internal class TestKafkaConsumerInjector : TestExecutionListener {

    override fun prepareTestInstance(testContext: TestContext) {
        val testInstance = testContext.testInstance
        val applicationContext = testContext.applicationContext

        var clazz: Class<*>? = testInstance::class.java
        while (clazz != null && clazz != Any::class.java) {
            for (field in clazz.declaredFields) {
                val annotation = field.getAnnotation(TestListener::class.java)
                if (annotation != null && field.type == TestKafkaConsumer::class.java) {
                    val beanName = "testKafkaConsumer[${annotation.value}]"
                    val consumer = applicationContext.getBean(beanName) as TestKafkaConsumer
                    field.isAccessible = true
                    field.set(testInstance, consumer)
                }
            }
            clazz = clazz.superclass
        }
    }
}
