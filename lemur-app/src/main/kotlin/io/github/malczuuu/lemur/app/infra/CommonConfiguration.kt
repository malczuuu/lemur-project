package io.github.malczuuu.lemur.app.infra

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import java.time.Clock
import java.time.ZoneId

@Configuration
@EnableAsync
@EnableScheduling
class CommonConfiguration {

    @Bean
    fun clock(): Clock? = Clock.system(ZoneId.systemDefault())
}
