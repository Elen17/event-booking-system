ALTER TABLE
    event
    ADD IF NOT EXISTS
        available_attendees_capacity INT NOT NULL DEFAULT 1;

ALTER TABLE
    app_user
    ADD IF NOT EXISTS
        is_active BOOLEAN DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS
    refresh_tokens (
        id SERIAL NOT NULL,
        user_id INTEGER NOT NULL,
        token VARCHAR(255) NOT NULL,
        expiry_date TIMESTAMP NOT NULL,
        PRIMARY KEY (id),
        FOREIGN KEY (user_id) REFERENCES app_user (id) ON DELETE CASCADE
    );

create index idx_refresh_token_user_id on refresh_tokens (user_id);

ALTER TABLE
    event
    ADD IF NOT EXISTS
        min_price DECIMAL NOT NULL DEFAULT 0;