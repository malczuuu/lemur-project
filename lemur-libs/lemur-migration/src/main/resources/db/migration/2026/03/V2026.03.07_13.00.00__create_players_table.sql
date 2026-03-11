CREATE
    SEQUENCE seq_players_player_id START 1;

CREATE
    TABLE
        players(
            player_id BIGINT PRIMARY KEY DEFAULT nextval('seq_players_player_id'),
            player_name VARCHAR(200) NOT NULL,
            player_rating INT NOT NULL DEFAULT 0,
            player_status VARCHAR(10) NOT NULL,
            player_created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            player_last_modified_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            player_version BIGINT NOT NULL DEFAULT 0
        );
