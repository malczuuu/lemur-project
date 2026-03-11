plugins {
    id("internal.errorprone-convention")
    id("internal.jacoco-convention")
    id("internal.java-library-convention")
}

dependencies {
    testImplementation(platform(project(":lemur-libs:lemur-bom")))

    testImplementation(project(":lemur-libs:lemur-testkit"))
    testRuntimeOnly(libs.junit.platform.launcher)

    errorprone(libs.errorprone.core)
    errorprone(libs.nullaway)
}

tasks.withType<ProcessResources>().configureEach {
    exclude("**/.gitkeep")
    includeEmptyDirs = false
}
