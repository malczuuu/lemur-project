plugins {
    id("internal.jacoco-convention")
    id("internal.kotlin-library-convention")
}

dependencies {
    api(platform(project(":lemur-libs:lemur-bom")))

    testImplementation(project(":lemur-libs:lemur-testkit"))
    testRuntimeOnly(libs.junit.platform.launcher)
}
