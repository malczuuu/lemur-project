plugins {
    id("internal.errorprone-convention")
    id("internal.jacoco-convention")
    id("internal.java-library-convention")
}

dependencies {
    api(platform(project(":lemur-libs:lemur-bom")))

    api(libs.jackson.annotations)
    api(libs.jakarta.validation.api)
    api(libs.jspecify)
    api(libs.kafka.clients)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    errorprone(libs.errorprone.core)
    errorprone(libs.nullaway)
}
