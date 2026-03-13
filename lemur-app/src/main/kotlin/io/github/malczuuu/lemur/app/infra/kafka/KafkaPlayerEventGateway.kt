package io.github.malczuuu.lemur.app.infra.kafka

import io.github.malczuuu.lemur.app.common.getLogger
import io.github.malczuuu.lemur.app.domain.player.PlayerEvent
import io.github.malczuuu.lemur.app.domain.player.PlayerEventGateway
import io.github.malczuuu.lemur.contract.TransportMessage
import io.github.malczuuu.lemur.contract.message.MessageHeaders.EVENT_TYPE_HEADER
import io.github.malczuuu.lemur.contract.message.player.PlayerBannedMessage
import io.github.malczuuu.lemur.contract.message.player.PlayerCreatedMessage
import io.github.malczuuu.lemur.contract.message.player.PlayerRatingChangedMessage
import io.github.malczuuu.lemur.contract.message.player.PlayerUnbannedMessage
import io.github.malczuuu.lemur.contract.message.player.PlayerUpdatedMessage
import io.micrometer.core.instrument.MeterRegistry
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaOperations
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper
import java.nio.charset.StandardCharsets

@Component
internal class KafkaPlayerEventGateway(
    private val kafkaOperations: KafkaOperations<String, String>,
    private val jsonMapper: JsonMapper,
    private val meterRegistry: MeterRegistry,
    @Value($$"${lemur-app.kafka.topic.player-events}") private val topic: String,
) : PlayerEventGateway {

    companion object {
        private val log = getLogger(KafkaPlayerEventGateway::class)
        private const val PLAYER_EVENT_SUCCESS = "lemur.kafka.player.event.publish.success"
        private const val PLAYER_EVENT_FAILURE = "lemur.kafka.player.event.publish.failure"
        private const val EVENT_TYPE_TAG = "event_type"
    }

    override fun publish(event: PlayerEvent.PlayerCreated) {
        val eventType = "PlayerCreated"
        try {
            send(event.playerId, eventType, PlayerCreatedMessage(event.playerId))
            incrementSuccessMetric(eventType)
        } catch (e: Exception) {
            log.error("Failed to publish {} to Kafka", eventType, e)
            incrementFailureMetric(eventType)
            throw KafkaGatewayException(makeExceptionMessage(eventType), e)
        }
    }

    override fun publish(event: PlayerEvent.PlayerUpdated) {
        val eventType = "PlayerUpdated"
        try {
            send(event.playerId, eventType, PlayerUpdatedMessage(event.playerId))
            incrementSuccessMetric(eventType)
        } catch (e: Exception) {
            log.error("Failed to publish {} to Kafka", eventType, e)
            incrementFailureMetric(eventType)
            throw KafkaGatewayException(makeExceptionMessage(eventType), e)
        }
    }

    override fun publish(event: PlayerEvent.PlayerBanned) {
        val eventType = "PlayerBanned"
        try {
            send(event.playerId, eventType, PlayerBannedMessage(event.playerId))
            incrementSuccessMetric(eventType)
        } catch (e: Exception) {
            log.error("Failed to publish {} to Kafka", eventType, e)
            incrementFailureMetric(eventType)
            throw KafkaGatewayException(makeExceptionMessage(eventType), e)
        }
    }

    override fun publish(event: PlayerEvent.PlayerUnbanned) {
        val eventType = "PlayerUnbanned"
        try {
            send(event.playerId, eventType, PlayerUnbannedMessage(event.playerId))
            incrementSuccessMetric(eventType)
        } catch (e: Exception) {
            log.error("Failed to publish {} to Kafka", eventType, e)
            incrementFailureMetric(eventType)
            throw KafkaGatewayException(makeExceptionMessage(eventType), e)
        }
    }

    override fun publish(event: PlayerEvent.PlayerRatingChanged) {
        val eventType = "PlayerRatingChanged"
        try {
            send(
                event.playerId,
                eventType,
                PlayerRatingChangedMessage(event.playerId, event.oldRating, event.newRating),
            )
            incrementSuccessMetric(eventType)
        } catch (e: Exception) {
            log.error("Failed to publish {} to Kafka", eventType, e)
            incrementFailureMetric(eventType)
            throw KafkaGatewayException(makeExceptionMessage(eventType), e)
        }
    }

    private fun send(key: String, eventTypeName: String, message: TransportMessage) {
        val value = jsonMapper.writeValueAsString(message)
        val headers = listOf(eventTypeHeader(eventTypeName))
        val record = ProducerRecord(topic, null, key, value, headers)
        kafkaOperations.send(record).join()
    }

    private fun eventTypeHeader(eventTypeName: String) =
        RecordHeader(EVENT_TYPE_HEADER, eventTypeName.toByteArray(StandardCharsets.UTF_8))

    private fun makeExceptionMessage(type: String) = "Failed to publish $type event to Kafka"

    private fun incrementSuccessMetric(eventType: String) {
        meterRegistry.counter(PLAYER_EVENT_SUCCESS, EVENT_TYPE_TAG, eventType).increment()
    }

    private fun incrementFailureMetric(eventType: String) {
        meterRegistry.counter(PLAYER_EVENT_FAILURE, EVENT_TYPE_TAG, eventType).increment()
    }
}
