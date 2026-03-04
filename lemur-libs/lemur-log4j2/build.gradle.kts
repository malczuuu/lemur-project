plugins {
    id("internal.java-library-convention")
}

dependencies {
    api(platform(project(":lemur-libs:lemur-bom")))

    testImplementation(project(":lemur-libs:lemur-testkit"))
    testRuntimeOnly(libs.junit.platform.launcher)
}
