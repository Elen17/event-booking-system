ALTER TABLE event
    ADD COLUMN created_by INT NOT NULL
        REFERENCES app_user (id) ON DELETE CASCADE;
