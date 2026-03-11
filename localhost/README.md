# Localhost Environment

Docker Compose setup for running service dependencies locally.

## Prerequisites

- Docker with Compose plugin (Compose V2).

## Services

### Infrastructure (always started)

| Service            | Port   | Description                                                               |
|--------------------|--------|---------------------------------------------------------------------------|
| **postgres**       | `5432` | PostgreSQL database (`lemur` database, credentials `postgres`/`postgres`) |
| **kafka**          | `9092` | Apache Kafka broker (KRaft mode, no ZooKeeper)                            |
| **kafka-init-job** | -      | One-shot job that creates initial topic (5 partitions)                    |
