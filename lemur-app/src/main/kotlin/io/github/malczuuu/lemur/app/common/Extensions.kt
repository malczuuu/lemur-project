package io.github.malczuuu.lemur.app.common

// Kotlin extension methods for various classes used in the application.

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

internal fun getLogger(`class`: KClass<*>): Logger = LoggerFactory.getLogger(`class`.java)

internal fun <T : Any, K : Any> ConsumerRecord<T, K>.findHeader(key: String): String? =
    headers().lastHeader(key)?.value()?.let { String(it, Charsets.UTF_8) }
