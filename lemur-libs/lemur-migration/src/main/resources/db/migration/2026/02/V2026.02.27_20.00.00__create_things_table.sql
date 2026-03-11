CREATE
    SEQUENCE seq_things_thing_id START 1;

CREATE
    TABLE
        things(
            thing_id BIGINT PRIMARY KEY DEFAULT nextval('seq_things_thing_id'),
            thing_name VARCHAR(256) NOT NULL,
            thing_description VARCHAR(2048) NOT NULL,
            thing_created_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            thing_last_modified_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
            thing_version BIGINT NOT NULL DEFAULT 0
        );
