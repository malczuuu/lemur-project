# Localhost Environment

Docker Compose setup for running Lemur and its dependencies locally.

## Prerequisites

- Docker with Compose plugin
- Built Docker images for `lemur-app` and `lemur-flyway` (run `./docker.sh` from the project root)

## Profiles

Services are organized into profiles so you can start only what you need.

| Profile       | Services                            |
|---------------|-------------------------------------|
| _(default)_   | postgres, kafka, kafka-init-job     |
| `apps`        | lemur-flyway, lemur-app             |
| `monitoring`  | prometheus, loki, promtail, grafana |

## Usage

Start infrastructure only (for local development against the IDE):

```shell
docker compose up
```

Start everything including the application containers:

```shell
docker compose --profile apps up
```

Start with full monitoring stack:

```shell
docker compose --profile apps --profile monitoring up
```

Stop and remove all containers:

```shell
docker compose --profile apps --profile monitoring down
```

Remove all containers **and volumes** (resets all data):

```shell
docker compose --profile apps --profile monitoring down -v
```

## Services

### Infrastructure (always started)

| Service            | Port   | Description                                                               |
|--------------------|--------|---------------------------------------------------------------------------|
| **postgres**       | `5432` | PostgreSQL database (`lemur` database, credentials `postgres`/`postgres`) |
| **kafka**          | `9092` | Apache Kafka broker (KRaft mode, no ZooKeeper)                            |
| **kafka-init-job** | -      | One-shot job that creates the `lemur` topic (5 partitions)                |

### Application (`apps` profile)

| Service          | Port   | Description                                             |
|------------------|--------|---------------------------------------------------------|
| **lemur-flyway** | -      | Runs Flyway database migrations, then exits             |
| **lemur-app**    | `8321` | The main application (starts after migrations complete) |

Both use the `LEMUR_VERSION` environment variable for the image tag (defaults to `snapshot`).

The startup order is enforced: `postgres` (healthy) -> `lemur-flyway` (completed) -> `lemur-app`.

### monitoring (`monitoring` profile)

| Service        | Port   | Description                                                                |
|----------------|--------|----------------------------------------------------------------------------|
| **prometheus** | `9090` | Scrapes metrics from `lemur-app` at `/actuator/prometheus` every 15 s      |
| **loki**       | `3100` | Log aggregation backend                                                    |
| **promtail**   | -      | Discovers Docker containers, parses JSON ECS logs, and ships them to Loki  |
| **grafana**    | `3000` | Dashboards and log exploration (anonymous admin access, no login required) |

Grafana is pre-provisioned with two datasources:

- **Prometheus** (default) - for metrics
- **Loki** - for logs
