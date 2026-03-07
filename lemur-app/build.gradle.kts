import internal.lombok

plugins {
    id("internal.errorprone-convention")
    id("internal.jacoco-convention")
    id("internal.spring-app-convention")
}

dependencies {
    implementation(platform(project(":lemur-libs:lemur-bom")))

    implementation(project(":lemur-libs:lemur-log4j2"))

    implementation(libs.problem4j.spring.webmvc)

    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.kafka)
    implementation(libs.spring.boot.starter.log4j2)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.micrometer.registry.prometheus)

    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    runtimeOnly(libs.postgresql)

    testImplementation(project(":lemur-libs:lemur-migration"))
    testImplementation(project(":lemur-libs:lemur-testkit"))
    testImplementation(libs.awaitility)
    testImplementation(libs.spring.boot.starter.actuator.test)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.postgresql)

    errorprone(libs.errorprone.core)
    errorprone(libs.nullaway)

    lombok(libs.lombok)
}

configurations.all {
    exclude(group = libs.spring.boot.starter.logging.get().group, module = libs.spring.boot.starter.logging.get().name)
}
