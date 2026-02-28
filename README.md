# Lemur Project

![Banner](https://raw.githubusercontent.com/malczuuu/lemur-project/refs/heads/main/docs/img/banner.png)

[![Gradle Build](https://github.com/malczuuu/lemur-project/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/malczuuu/lemur-project/actions/workflows/gradle-build.yml)

A template Spring Boot project with a dummy reference implementation of a REST endpoint, tested with Testcontainers.

> _Most of my sandbox apps these days look the same. This will make it easier to start new sandboxes without setting up
> too much boilerplate._
>
> ~ _Author_

## Tech Stack

- **Java** with **Spring Boot 4.0**
- **Gradle 9** (Kotlin DSL)
- **PostgreSQL** with **Flyway** migrations
- **Apache Kafka**
- **Testcontainers** for integration tests
- **Problem4J** for RFC 9457 Problem Details error responses

## Project Structure

| Module                       | Description                                     |
|------------------------------|-------------------------------------------------|
| `lemur-app`                  | Main application with REST endpoints and domain |
| `lemur-flyway`               | Standalone Flyway runner                        |
| `lemur-libs/lemur-bom`       | Bill of Materials for dependency management     |
| `lemur-libs/lemur-migration` | Database migration scripts                      |
| `lemur-libs/lemur-testkit`   | Shared test utilities and Testcontainers setup  |

## Reference Implementation

The project includes a simple `Thing` resource as a reference CRUD endpoint:

- `GET /api/things` — list all things
- `GET /api/things/{id}` — get a thing by ID
- `POST /api/things` — create a new thing

This serves as a starting point — replace or extend it with your own domain.

## Getting Started

### Prerequisites

- JDK 17+ (Gradle 9 requires Java 17 or higher, but code is compiled with Java 25 Toolchain)
- Docker (required for Testcontainers)

### Build & Test

Default Gradle tasks are `spotlessApply` and `build`. To run both formatting and tests, simply execute:

```bash
./gradlew
```

To run tests, execute:

```bash
./gradlew test
```

To run tests with Testcontainers, ensure Docker is running and add `-Pcontainers.enabled`:

```bash
./gradlew test -Pcontainers.enabled
```

To run individual tests via IntelliJ IDEA, set `containers.enabled` in `gradle.properties` for convenience.

### Run

Importing to IntelliJ IDEA will automatically set up run configuration, thanks to `idea-ext` plugin.

If running from command line, execute:

```bash
./gradlew :lemur-app:bootRun
```

Make sure a PostgreSQL instance and Kafka broker are available, or configure them via `application.yml`.

Service starts on port `8321` by default if running from sources. In `Dockerfile`, port is normalized to a semi-standard
port `8080`.
