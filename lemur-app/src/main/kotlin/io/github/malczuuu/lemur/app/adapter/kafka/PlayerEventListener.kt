package io.github.malczuuu.lemur.app.adapter.kafka

import io.github.malczuuu.lemur.app.common.findHeader
import io.github.malczuuu.lemur.app.common.getLogger
import io.github.malczuuu.lemur.app.infra.data.jpa.player.PlayerEventLogEntity
import io.github.malczuuu.lemur.app.infra.data.jpa.player.PlayerEventLogJpaRepository
import io.github.malczuuu.lemur.contract.message.MessageHeaders.EVENT_TYPE_HEADER
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import tools.jackson.core.JacksonException
import tools.jackson.databind.json.JsonMapper
import java.time.Clock
import java.time.Instant

@Component
class PlayerEventListener(
    private val logRepository: PlayerEventLogJpaRepository,
    private val jsonMapper: JsonMapper,
    private val clock: Clock,
) {
    companion object {
        private val log = getLogger(PlayerEventListener::class)
    }

    @KafkaListener(topics = [$$"${lemur-app.kafka.topic.player-events}"])
    fun onPlayerEvent(record: ConsumerRecord<String, String>) {
        try {
            val eventType = record.findHeader(EVENT_TYPE_HEADER) ?: "unknown"
            val node = jsonMapper.readTree(record.value())
            val playerId = node.path("playerId").asString()

            val eventLogEntity = PlayerEventLogEntity(
                playerId = playerId.toLongOrNull() ?: return logInvalidId(record, eventType, playerId),
                eventType = eventType,
                payload = record.value(),
                publishedDate = Instant.ofEpochMilli(record.timestamp()),
                receivedDate = clock.instant(),
            )
            logRepository.save(eventLogEntity)

            logPlayerEvent(record, eventType, playerId)
        } catch (e: JacksonException) {
            logJacksonException(record, e)
        }
    }

    private fun logInvalidId(record: ConsumerRecord<String, String>, eventType: String, playerId: String) {
        log.atWarn()
            .addKeyValue("topic", record.topic())
            .addKeyValue("partition", record.partition())
            .addKeyValue("offset", record.offset())
            .addKeyValue("eventType", eventType)
            .addKeyValue("playerId", playerId)
            .log("Ignoring player event with invalid playerId")
    }

    private fun logPlayerEvent(record: ConsumerRecord<String, String>, eventType: String, playerId: String) {
        log.atInfo()
            .addKeyValue("topic", record.topic())
            .addKeyValue("partition", record.partition())
            .addKeyValue("offset", record.offset())
            .addKeyValue("eventType", eventType)
            .addKeyValue("playerId", playerId)
            .log("Player event logged")
    }

    private fun logJacksonException(record: ConsumerRecord<String, String>, e: JacksonException) {
        log.atError()
            .addKeyValue("topic", record.topic())
            .addKeyValue("partition", record.partition())
            .addKeyValue("offset", record.offset())
            .setCause(e)
            .log("Failed to deserialize player event from Kafka")
    }
}
