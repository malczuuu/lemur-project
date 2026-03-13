plugins {
    id("internal.java-library-convention")
}

dependencies {
    api(platform(project(":lemur-libs:lemur-bom")))

    api(libs.jackson.annotations)
    api(libs.jakarta.validation.api)
    api(libs.jspecify)

    compileOnly(libs.kafka.clients)

    testImplementation(project(":lemur-libs:lemur-testkit"))
    testRuntimeOnly(libs.junit.platform.launcher)
}
