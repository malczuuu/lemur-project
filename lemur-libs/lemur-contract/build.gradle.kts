plugins {
    id("internal.kotlin-library-convention")
}

dependencies {
    api(platform(project(":lemur-libs:lemur-bom")))

    api(libs.jackson.annotations)
    api(libs.jakarta.validation.api)

    testImplementation(project(":lemur-libs:lemur-testkit"))
    testRuntimeOnly(libs.junit.platform.launcher)
}
