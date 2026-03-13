package io.github.malczuuu.lemur.testkit.annotation

/**
 * Marks a [io.github.malczuuu.lemur.testkit.kafka.TestKafkaConsumer] field with the Kafka topic it
 * should consume from and allows injecting it in test classes.
 *
 * Example usage in a test class:
 * ```
 * @TestListener("\${lemur-app.kafka.topic.player-events}")
 * private lateinit var kafkaConsumer: TestKafkaConsumer
 * ```
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class TestListener(val value: String)
