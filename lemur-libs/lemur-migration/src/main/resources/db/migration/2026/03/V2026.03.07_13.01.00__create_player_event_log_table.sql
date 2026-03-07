CREATE
    SEQUENCE seq_player_event_logs_log_id START 1;

CREATE
    TABLE
        player_event_log(
            log_id BIGINT PRIMARY KEY DEFAULT nextval('seq_player_event_logs_log_id'),
            player_id BIGINT,
            log_event_type VARCHAR(100) NOT NULL,
            log_payload TEXT NOT NULL,
            log_published_date TIMESTAMPTZ NOT NULL,
            log_received_date TIMESTAMPTZ NOT NULL,
            log_created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
        );
