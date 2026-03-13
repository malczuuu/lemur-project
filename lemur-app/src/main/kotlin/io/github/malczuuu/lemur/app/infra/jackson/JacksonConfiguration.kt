package io.github.malczuuu.lemur.app.infra.jackson

import io.github.malczuuu.lemur.app.domain.player.PlayerStatus
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfiguration {

    @Bean
    fun enumLabelMixInCustomizer(): JsonMapperBuilderCustomizer = {
        it.addMixIn(PlayerStatus::class.java, EnumLabelMixIn::class.java)
    }
}
