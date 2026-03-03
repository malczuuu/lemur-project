plugins {
    id("internal.java-library-convention")
}

dependencies {
    api(platform(project(":lemur-libs:lemur-bom")))

    api(libs.jakarta.validation.api)
    api(libs.jspecify)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}
