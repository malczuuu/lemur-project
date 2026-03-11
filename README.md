# Lemur Project

A template Spring Boot project with a dummy reference implementation of a REST endpoint, tested with Testcontainers.

> _Most of my sandbox apps these days look the same. This will make it easier to start new sandboxes without setting up
> too much boilerplate._
>
> ~ _Author_

## Tech Stack

- **Java** with **Spring Boot 4**
- **Gradle 9** (Kotlin DSL)
- **PostgreSQL** with **Flyway** migrations
- **Apache Kafka**
- **Testcontainers** for integration tests
- **Problem4J** for RFC 9457 Problem Details error responses

## Getting Started

### Prerequisites

- Java 25
- Docker (required for Testcontainers)

### Build & Test

This project uses `idea-ext` plugin to generate IntelliJ IDEA run configurations for bulding project, launching apps and
running tests. **Loading project** will automatically generate run configurations. **Reloading project** will refresh
any changes back to defaults.

Make sure a PostgreSQL instance and Kafka broker are available. See [`localhost`](./localhost) subdirectory for Docker
Compose files to quickly spin up required services (it has its own [`README.md`](./localhost/README.md) with details).

Service starts on port `:8321` by default if running from sources. In `Dockerfile`, ports are unified to port `:8080`.

<details>
<summary><b>For command-line instructions, expand...</b></summary>

1. Default Gradle tasks are `spotlessApply` and `build`. To run both formatting and tests, simply execute:
   ```bash
   ./gradlew
   ```
2. To run tests, execute:
   ```bash
   ./gradlew test
   ```
3. To run tests with Testcontainers, ensure Docker is running and add `-Pcontainers.enabled`:
   ```bash
   ./gradlew test -Pcontainers.enabled
   ```
4. To run main application, execute:
   ```bash
   ./gradlew :lemur-app:bootRun
   ```
5. To run Flyway migrations, execute:
   ```bash
   ./gradlew :lemur-flyway:bootRun
   ```

</details>
