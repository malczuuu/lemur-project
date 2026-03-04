plugins {
    id("internal.spring-app-convention")
}

dependencies {
    implementation(platform(project(":lemur-libs:lemur-bom")))

    implementation(project(":lemur-libs:lemur-log4j2"))
    implementation(project(":lemur-libs:lemur-migration"))

    implementation(libs.flyway.database.postgresql)
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.spring.boot.starter.log4j2)
    runtimeOnly(libs.postgresql)

    testImplementation(project(":lemur-libs:lemur-testkit"))
    testImplementation(libs.spring.boot.starter.flyway.test)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.postgresql)
}

configurations.all {
    exclude(group = libs.spring.boot.starter.logging.get().group, module = libs.spring.boot.starter.logging.get().name)
}
