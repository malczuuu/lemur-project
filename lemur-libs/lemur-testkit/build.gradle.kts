plugins {
    id("internal.errorprone-convention")
    id("internal.java-library-convention")
}

dependencies {
    api(platform(project(":lemur-libs:lemur-bom")))

    api(libs.spring.boot.starter.flyway)

    api(libs.spring.boot.starter.test)
    api(libs.spring.boot.starter.flyway.test)
    api(libs.spring.boot.starter.data.jpa.test)
    api(libs.spring.boot.starter.kafka.test)
    api(libs.spring.boot.starter.webmvc.test)
    api(libs.spring.boot.starter.validation.test)

    api(libs.spring.boot.resttestclient)
    api(libs.spring.boot.testcontainers)

    api(libs.flyway.database.postgresql)
    api(libs.testcontainers.kafka)
    api(libs.testcontainers.postgresql)

    errorprone(libs.errorprone.core)
    errorprone(libs.nullaway)
}
