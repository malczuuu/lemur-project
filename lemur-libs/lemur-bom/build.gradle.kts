plugins {
    id("internal.java-platform-convention")
}

dependencies {
    api(platform(libs.jackson2.bom))
    api(platform(libs.jackson3.bom))
    api(platform(libs.junit.bom))
    api(platform(libs.problem4j.spring.bom))
    api(platform(libs.spring.boot.dependencies))
    api(platform(libs.springdoc.openapi.bom))

    constraints {
        api(libs.archunit)
        api(libs.errorprone.core)
        api(libs.jspecify)
        api(libs.kafka.clients)
        api(libs.lombok)
        api(libs.nullaway)
    }
}
