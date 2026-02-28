plugins {
    id("internal.spring-app-convention")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(project(":lemur-libs:lemur-bom")))

    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.kafka)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.problem4j.spring.webmvc)

    implementation(libs.micrometer.registry.prometheus)

    runtimeOnly(libs.postgresql)

    testImplementation(project(":lemur-libs:lemur-migration"))
    testImplementation(project(":lemur-libs:lemur-testkit"))
    testImplementation(libs.spring.boot.starter.actuator.test)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.postgresql)
}
