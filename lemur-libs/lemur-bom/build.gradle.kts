plugins {
    id("internal.java-platform-convention")
}

dependencies {
    api(platform(libs.jackson.bom))
    api(platform(libs.jackson2.bom))
    api(platform(libs.junit.bom))
    api(platform(libs.problem4j.spring.bom))
    api(platform(libs.spring.boot.dependencies))
    api(platform(libs.springdoc.openapi.bom))

    constraints {
        api(libs.archunit)
        api(libs.jspecify)
        api(libs.kafka.clients)
        api(libs.kotlin.reflect)
        api(libs.kotlin.test.junit5)
    }
}
