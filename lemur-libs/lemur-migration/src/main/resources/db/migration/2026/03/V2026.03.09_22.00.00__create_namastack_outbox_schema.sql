-- spotless:off

-- =============================================================================
--     outbox_record
-- =============================================================================
-- Central store for the transactional outbox pattern.
--
-- Each row represents a single pending or processed event payload that must be
-- reliably delivered to exactly one registered handler.  When OutboxService
-- schedules a payload it creates one row per discovered handler, so multiple
-- rows can share the same record_key but carry different handler_id values.
--
-- Lifecycle:
--   NEW  →  COMPLETED   (happy path: handler executed successfully)
--   NEW  →  NEW         (retry: failure_count < max-retries, next_retry_at bumped)
--   NEW  →  FAILED      (terminal: max-retries exhausted, no further attempts)
--
-- Partition routing:
--   partition_no is derived deterministically from record_key via MurmurHash3
--   (mod 256 total partitions).  The PartitionCoordinator assigns contiguous
--   partition ranges to each live application instance so that records sharing
--   the same key are always processed by the same instance, preserving ordering.
-- =============================================================================
CREATE TABLE IF NOT EXISTS outbox_record
(
    -- Universally unique record identifier (UUID v4 string).
    id             VARCHAR(255)             NOT NULL,

    -- Processing lifecycle state: NEW | COMPLETED | FAILED.
    -- Indexed individually and in composite indexes to support efficient polling
    -- and status-based queries by the OutboxRecordProcessor chain.
    status         VARCHAR(20)              NOT NULL,

    -- Logical grouping key supplied by the caller (e.g. aggregate ID).
    -- Records with the same key are routed to the same partition, guaranteeing
    -- in-order processing within a key.  Indexed together with created_at and
    -- completed_at for ordering and deduplication queries.
    record_key     VARCHAR(255)             NOT NULL,

    -- Fully qualified class name of the payload (e.g. "com.example.OrderCreatedEvent").
    -- Used by the Jackson deserialiser to reconstruct the typed payload object
    -- before dispatching to the handler.
    record_type    VARCHAR(255)             NOT NULL,

    -- Serialised JSON representation of the event payload.
    -- The OutboxJacksonSerializer writes and reads this field.
    payload        TEXT                     NOT NULL,

    -- Optional JSON map of arbitrary key/value propagation metadata
    -- (e.g. trace IDs, tenant IDs) collected by OutboxContextProvider beans
    -- plus any additional context supplied at schedule time.  NULL when no
    -- context was provided.
    context        TEXT,

    -- Wall-clock timestamp (UTC) at which this record was persisted.
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Wall-clock timestamp (UTC) set when the record transitions to COMPLETED.
    -- NULL until the record is successfully processed (or deleted if
    -- processing.deleteCompletedRecords = true).
    completed_at   TIMESTAMP WITH TIME ZONE,

    -- Number of failed processing attempts so far.  Incremented by
    -- RetryOutboxRecordProcessor on each transient failure.  When this reaches
    -- the configured retry.maxRetries the record is marked FAILED.
    failure_count  INT                      NOT NULL,

    -- Human-readable description of the last processing error (exception message).
    -- Truncated to 1000 characters.  NULL on first attempt or after success.
    failure_reason VARCHAR(1000),

    -- Earliest UTC timestamp at which this record may be re-attempted.
    -- Calculated by the active retry policy (fixed / linear / exponential + jitter).
    -- Polled together with partition_no and status in the hot composite index.
    next_retry_at  TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Partition bucket (0–255) derived from MurmurHash3(record_key) mod 256.
    -- The PartitionCoordinator maps partitions to instance IDs; the processor
    -- only fetches records whose partition_no belongs to the current instance.
    partition_no   INTEGER                  NOT NULL,

    -- Unique identifier of the OutboxHandlerMethod responsible for processing
    -- this record.  Derived from bean class + method name + parameter types,
    -- allowing the OutboxHandlerInvoker to resolve the exact handler at runtime.
    -- One payload may produce several rows with different handler_id values when
    -- multiple handlers are registered for the payload type or its superclasses.
    handler_id     VARCHAR(1000)            NOT NULL,

    PRIMARY KEY (id)
);

-- =============================================================================
--     outbox_instance
-- =============================================================================
-- Registry of all known application instances participating in outbox processing.
--
-- Each running application registers itself here on startup and keeps its row
-- alive via periodic heartbeats (configurable via
-- namastack.outbox.instance.heartbeatIntervalSeconds, default 5 s).
--
-- The PartitionCoordinator reads this table to determine the active topology
-- before every rebalance cycle.  Instances whose last_heartbeat exceeds
-- namastack.outbox.instance.staleInstanceTimeoutSeconds (default 30 s) are
-- treated as dead and their partitions are reclaimed by surviving instances.
--
-- Status values: ACTIVE | SHUTTING_DOWN | DEAD
-- =============================================================================
CREATE TABLE IF NOT EXISTS outbox_instance
(
    -- Unique instance identifier generated at startup (UUID v4 string).
    instance_id    VARCHAR(255) PRIMARY KEY,

    -- Hostname of the machine running this instance, used for diagnostics.
    hostname       VARCHAR(255)             NOT NULL,

    -- HTTP/management port of this instance, used for diagnostics.
    port           INTEGER                  NOT NULL,

    -- Lifecycle state of the instance: ACTIVE | SHUTTING_DOWN | DEAD.
    -- Set to SHUTTING_DOWN during graceful shutdown; surviving instances
    -- will not wait for DEAD instances during rebalance.
    status         VARCHAR(50)              NOT NULL,

    -- Wall-clock timestamp (UTC) when this instance registered itself.
    started_at     TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Wall-clock timestamp (UTC) of the most recent successful heartbeat.
    -- Used by the stale-instance detector; if now() - last_heartbeat exceeds
    -- staleInstanceTimeoutSeconds the instance is considered dead.
    last_heartbeat TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Wall-clock timestamp (UTC) when this row was first inserted.
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Wall-clock timestamp (UTC) of the last status or heartbeat update.
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL
);

-- =============================================================================
--     outbox_partition
-- =============================================================================
-- Ownership map for the 256 fixed partition buckets (0–255).
--
-- Rows are bootstrapped once by the first instance that starts up: it inserts
-- all 256 rows claiming ownership.  Concurrent bootstrap attempts are resolved
-- via a DataIntegrityViolationException on the PRIMARY KEY, which is silently
-- ignored by PartitionCoordinator.bootstrapPartitions().
--
-- Rebalancing algorithm (runs on every instance at configurable intervals):
--   1. Fetch active instance IDs from outbox_instance.
--   2. Claim partitions whose instance_id is NULL or belongs to a dead instance.
--   3. Release surplus partitions when new instances join, until each instance
--      owns roughly (256 / activeCount) partitions.
--
-- Optimistic locking: the version column is incremented on every ownership
-- transfer.  The repository performs a WHERE version = :expectedVersion check
-- so that two instances racing for the same partition converge safely.
-- =============================================================================
CREATE TABLE IF NOT EXISTS outbox_partition
(
    -- Partition bucket number in the range [0, 255].
    -- Assigned to a record via MurmurHash3(record_key) mod 256.
    partition_number INTEGER PRIMARY KEY,

    -- instance_id of the application instance currently owning this partition.
    -- NULL when no instance has claimed it (e.g. immediately after bootstrap
    -- race or after an owning instance was removed without rebalancing).
    instance_id      VARCHAR(255),

    -- Optimistic-lock counter, incremented on every ownership change.
    -- Prevents two concurrent instances from simultaneously claiming the same
    -- partition by requiring the writer to supply the version it last read.
    version          BIGINT                   NOT NULL DEFAULT 0,

    -- Wall-clock timestamp (UTC) of the last ownership transfer.
    updated_at       TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Supports deduplication and ordered retrieval of records for a given aggregate
-- (record_key), e.g. "has this event already been scheduled?".
CREATE INDEX IF NOT EXISTS idx_outbox_record_record_key_created
    ON outbox_record (record_key, created_at);

-- Hot polling index used by JdbcOutboxRecordRepository to fetch the next batch
-- of processable records for the current instance's assigned partitions.
-- The leading partition_no column allows the planner to use an index-only scan
-- when filtering by the instance's partition set; status and next_retry_at
-- filter to NEW records that are ready for their next attempt.
CREATE INDEX IF NOT EXISTS idx_outbox_record_partition_status_retry
    ON outbox_record (partition_no, status, next_retry_at);

-- Global NEW-record sweep used by monitoring queries and the adaptive poller
-- to estimate the total backlog size across all partitions.
CREATE INDEX IF NOT EXISTS idx_outbox_record_status_retry
    ON outbox_record (status, next_retry_at);

-- Fast status-only filter for actuator health/metrics endpoints and for bulk
-- operations such as deleteByStatus.
CREATE INDEX IF NOT EXISTS idx_outbox_record_status
    ON outbox_record (status);

-- Supports completed-record queries ordered by completion time within a key,
-- e.g. "show last N successfully delivered events for aggregate X".
CREATE INDEX IF NOT EXISTS idx_outbox_record_record_key_completed_created
    ON outbox_record (record_key, completed_at, created_at);

-- Used by PartitionCoordinator to identify stale instances: selects ACTIVE rows
-- with a last_heartbeat older than staleInstanceTimeoutSeconds.
CREATE INDEX IF NOT EXISTS idx_outbox_instance_status_heartbeat
    ON outbox_instance (status, last_heartbeat);

-- Supports bulk queries that scan all instances sorted by heartbeat recency,
-- e.g. "find the most recently seen instances" for diagnostic dashboards.
CREATE INDEX IF NOT EXISTS idx_outbox_instance_last_heartbeat
    ON outbox_instance (last_heartbeat);

-- Fast status-only filter used by the instance registry to count ACTIVE instances
-- and by the dead-instance cleanup job to find DEAD rows to remove.
CREATE INDEX IF NOT EXISTS idx_outbox_instance_status
    ON outbox_instance (status);

-- Used by PartitionCoordinator during rebalance to load all partitions owned by
-- a specific instance (e.g. to release surplus or reclaim from a dead instance).
CREATE INDEX IF NOT EXISTS idx_outbox_partition_instance_id
    ON outbox_partition (instance_id);

-- =============================================================================
-- COMMENT ON – machine-readable metadata visible in pg_description / psql \d+
-- =============================================================================

-- outbox_record
COMMENT ON TABLE outbox_record IS
    'Central store for the transactional outbox pattern. Each row is a single '
    'event payload pending delivery to one registered handler. Multiple rows '
    'may share the same record_key when several handlers are registered for a '
    'payload type (one row per handler). Lifecycle: NEW → COMPLETED | FAILED.';

COMMENT ON COLUMN outbox_record.id IS
    'Universally unique record identifier (UUID v4 string).';

COMMENT ON COLUMN outbox_record.status IS
    'Processing lifecycle state. Allowed values: NEW (ready for processing), '
    'COMPLETED (handler executed successfully), FAILED (max retries exhausted).';

COMMENT ON COLUMN outbox_record.record_key IS
    'Logical grouping key supplied by the caller (e.g. aggregate ID). Records '
    'sharing the same key are routed to the same partition, preserving '
    'in-order processing within a key.';

COMMENT ON COLUMN outbox_record.record_type IS
    'Fully qualified class name of the payload '
    '(e.g. "com.example.OrderCreatedEvent"). Used by the Jackson deserialiser '
    'to reconstruct the typed object before dispatching to the handler.';

COMMENT ON COLUMN outbox_record.payload IS
    'Serialised JSON representation of the event payload, written and read by '
    'OutboxJacksonSerializer.';

COMMENT ON COLUMN outbox_record.context IS
    'Optional JSON map of arbitrary key/value propagation metadata '
    '(e.g. trace IDs, tenant IDs). Collected from OutboxContextProvider beans '
    'and merged with any caller-supplied additional context. NULL when absent.';

COMMENT ON COLUMN outbox_record.created_at IS
    'Wall-clock timestamp (UTC) at which this record was persisted.';

COMMENT ON COLUMN outbox_record.completed_at IS
    'Wall-clock timestamp (UTC) set when the record transitions to COMPLETED. '
    'NULL until successfully processed, or absent when deleteCompletedRecords = true.';

COMMENT ON COLUMN outbox_record.failure_count IS
    'Number of failed processing attempts so far. Incremented by '
    'RetryOutboxRecordProcessor on each transient failure. When this reaches '
    'retry.maxRetries the record is permanently marked FAILED.';

COMMENT ON COLUMN outbox_record.failure_reason IS
    'Human-readable description of the last processing error (exception message), '
    'truncated to 1000 characters. NULL on first attempt or after success.';

COMMENT ON COLUMN outbox_record.next_retry_at IS
    'Earliest UTC timestamp at which this record may be re-attempted. '
    'Calculated by the active retry policy (fixed / linear / exponential + optional jitter).';

COMMENT ON COLUMN outbox_record.partition_no IS
    'Partition bucket in the range [0, 255] derived from '
    'MurmurHash3(record_key) mod 256. The PartitionCoordinator maps partitions '
    'to instance IDs; the processor only fetches records assigned to the '
    'current instance.';

COMMENT ON COLUMN outbox_record.handler_id IS
    'Unique identifier of the OutboxHandlerMethod responsible for processing '
    'this record. Derived from bean class + method name + parameter types. '
    'The OutboxHandlerInvoker uses this to resolve the exact handler at runtime. '
    'Multiple rows with different handler_id values can exist for the same '
    'payload when several handlers are registered for the payload type or its '
    'superclasses.';

-- outbox_instance
COMMENT ON TABLE outbox_instance IS
    'Registry of all application instances participating in outbox processing. '
    'Each instance registers on startup and maintains its row via periodic '
    'heartbeats. The PartitionCoordinator uses this table to determine the '
    'active topology before each rebalance cycle. Instances silent for longer '
    'than staleInstanceTimeoutSeconds are treated as dead and their partitions '
    'are reclaimed.';

COMMENT ON COLUMN outbox_instance.instance_id IS
    'Unique instance identifier generated at startup (UUID v4 string).';

COMMENT ON COLUMN outbox_instance.hostname IS
    'Hostname of the machine running this instance, used for diagnostics.';

COMMENT ON COLUMN outbox_instance.port IS
    'HTTP/management port of this instance, used for diagnostics.';

COMMENT ON COLUMN outbox_instance.status IS
    'Lifecycle state of the instance. Allowed values: ACTIVE (processing records), '
    'SHUTTING_DOWN (graceful shutdown in progress), DEAD (heartbeat timed out).';

COMMENT ON COLUMN outbox_instance.started_at IS
    'Wall-clock timestamp (UTC) when this instance registered itself.';

COMMENT ON COLUMN outbox_instance.last_heartbeat IS
    'Wall-clock timestamp (UTC) of the most recent successful heartbeat. '
    'If now() - last_heartbeat > staleInstanceTimeoutSeconds the instance is '
    'considered dead and its partitions become eligible for reclaim.';

COMMENT ON COLUMN outbox_instance.created_at IS
    'Wall-clock timestamp (UTC) when this row was first inserted.';

COMMENT ON COLUMN outbox_instance.updated_at IS
    'Wall-clock timestamp (UTC) of the last status or heartbeat update.';

-- outbox_partition
COMMENT ON TABLE outbox_partition IS
    'Ownership map for the 256 fixed partition buckets (0–255). Bootstrapped '
    'once by the first instance to start; concurrent bootstrap races are '
    'resolved via PRIMARY KEY violation handled silently by '
    'PartitionCoordinator. Ownership transfers use optimistic locking on the '
    'version column to prevent split-brain between concurrently rebalancing '
    'instances.';

COMMENT ON COLUMN outbox_partition.partition_number IS
    'Partition bucket in the range [0, 255]. '
    'Assigned to a record via MurmurHash3(record_key) mod 256.';

COMMENT ON COLUMN outbox_partition.instance_id IS
    'instance_id of the application instance currently owning this partition. '
    'NULL when unclaimed (e.g. after a dead instance is removed and before the '
    'next rebalance cycle assigns it to a surviving instance).';

COMMENT ON COLUMN outbox_partition.version IS
    'Optimistic-lock counter incremented on every ownership change. The '
    'repository issues UPDATE ... WHERE version = :expected so that two '
    'instances racing for the same partition converge safely without explicit '
    'locking.';

COMMENT ON COLUMN outbox_partition.updated_at IS
    'Wall-clock timestamp (UTC) of the last ownership transfer.';

-- spotless:on
