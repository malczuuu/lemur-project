plugins {
    id("internal.spring-app-convention")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

dependencies {
    implementation(platform(project(":lemur-libs:lemur-bom")))

    implementation(project(":lemur-libs:lemur-log4j2"))
    implementation(project(":lemur-libs:lemur-model"))

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
    testImplementation(libs.spring.boot.starter.actuator.test)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.postgresql)
}

configurations.all {
    exclude(group = libs.spring.boot.starter.logging.get().group, module = libs.spring.boot.starter.logging.get().name)
}
