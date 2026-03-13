package io.github.malczuuu.lemur.testkit.annotation

import io.github.malczuuu.lemur.testkit.TestkitTags.TESTCONTAINERS
import org.junit.jupiter.api.Tag

@Tag(TESTCONTAINERS)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ContainerTest
