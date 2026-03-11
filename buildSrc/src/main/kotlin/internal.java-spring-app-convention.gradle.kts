plugins {
    id("internal.java-convention")
    id("org.springframework.boot")
}

tasks.withType<Jar>().configureEach {
    if (name != "bootJar") {
        enabled = false
    }
}
