plugins {
    id("internal.java-platform-convention")
}

dependencies {
    api(platform(libs.spring.boot.dependencies))
}
