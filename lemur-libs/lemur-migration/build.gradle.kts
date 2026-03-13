plugins {
    id("internal.jacoco-convention")
    id("internal.kotlin-library-convention")
}

dependencies {
    testImplementation(platform(project(":lemur-libs:lemur-bom")))

    testImplementation(project(":lemur-libs:lemur-testkit"))
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<ProcessResources>().configureEach {
    exclude("**/.gitkeep")
    includeEmptyDirs = false
}
