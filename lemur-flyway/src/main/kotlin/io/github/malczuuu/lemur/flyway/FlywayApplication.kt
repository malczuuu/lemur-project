package io.github.malczuuu.lemur.flyway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FlywayApplication

fun main(args: Array<String>) {
    runApplication<FlywayApplication>(*args)
}
