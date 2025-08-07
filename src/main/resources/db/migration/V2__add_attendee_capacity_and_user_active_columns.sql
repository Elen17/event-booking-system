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


INSERT INTO app_user (first_name, last_name, email, role_id, country_id, is_active)
VALUES ('Admin', 'User', 'admin@example.com',
        1, 1, true);

INSERT INTO user_password_history (user_id, password_hash, salt, created_at)
VALUES (1,
        '$2a$10$xDMPQ5t1K5JX7WJkQ8LzEeX1Zv8Jp9X0fVJvQ2LbW5cD7eY3g2z1C',
        '', NOW());