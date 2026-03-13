package io.github.malczuuu.lemur.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LemurApplication

fun main(args: Array<String>) {
    runApplication<LemurApplication>(*args)
}
