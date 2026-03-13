package io.github.malczuuu.lemur.app.infra.jackson

import io.github.malczuuu.lemur.app.domain.player.PlayerStatus
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import tools.jackson.databind.exc.InvalidFormatException
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue

@SpringBootTest(classes = [JacksonAutoConfiguration::class, JacksonConfiguration::class])
class JacksonConfigurationTests {

    @Autowired
    private lateinit var jsonMapper: JsonMapper

    @EnumSource(PlayerStatus::class)
    @ParameterizedTest
    fun givenPlayerStatus_whenSerializing_thenShouldUseLabel(status: PlayerStatus) {
        val json = jsonMapper.writeValueAsString(status)

        assertThat(json).isEqualTo("\"${status.label}\"")
    }

    @EnumSource(PlayerStatus::class)
    @ParameterizedTest
    fun givenLabel_whenDeserializing_thenShouldParseToPlayerStatus(status: PlayerStatus) {
        val json = "\"${status.label}\""

        val deserializedStatus = jsonMapper.readValue<PlayerStatus>(json)

        assertEquals(status, deserializedStatus)
    }

    @EnumSource(PlayerStatus::class)
    @ParameterizedTest
    fun givenName_whenDeserializing_thenShouldFailParsing(status: PlayerStatus) {
        val json = "\"${status.name}\""

        assertThatThrownBy { jsonMapper.readValue<PlayerStatus>(json) }
            .isInstanceOf(InvalidFormatException::class.java)
            .satisfies({ ex ->
                val e = ex as InvalidFormatException
                assertThat(e.targetType).isEqualTo(PlayerStatus::class.java)
                assertThat(e.value).isEqualTo(status.name)
            })
    }
}
