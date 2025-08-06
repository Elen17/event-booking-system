START TRANSACTION;

CREATE TABLE IF NOT EXISTS country
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS city
(
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL UNIQUE,
    country_id INT          NOT NULL,
    FOREIGN KEY (country_id) REFERENCES country (id)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS venue
(
    id      SERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    city_id INT          NOT NULL,
    address VARCHAR(255) NOT NULL,
    FOREIGN KEY (city_id) REFERENCES city (id)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS user_role
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
    );

INSERT INTO user_role (name)
VALUES ('ADMIN'),
       ('USER');

CREATE TABLE IF NOT EXISTS event_status
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
    );

INSERT INTO event_status (name)
VALUES ('PLANNED'),
       ('CANCELLED'),
       ('COMPLETED');

CREATE TABLE IF NOT EXISTS event_type
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
    );

INSERT INTO event_type (name)
VALUES ('Concert'),
       ('Cinema'),
       ('Theater'),
       ('Opera and Ballet');

CREATE TABLE IF NOT EXISTS seat_status
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
    );

INSERT INTO seat_status (name)
VALUES ('AVAILABLE'),
       ('RESERVED'),
       ('PURCHASED');

CREATE TABLE IF NOT EXISTS app_user
(
    id            SERIAL PRIMARY KEY,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id       INT          NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    country_id    INT          NOT NULL,
    FOREIGN KEY (country_id) REFERENCES country (id),
    FOREIGN KEY (role_id) REFERENCES user_role (id)
    );

CREATE TABLE IF NOT EXISTS user_password_history
(
    id             SERIAL PRIMARY KEY,
    user_id        INT          NOT NULL,
    password_hash  VARCHAR(255) NOT NULL,
    salt           VARCHAR(255) NOT NULL,
    hash_algorithm VARCHAR(50)  NOT NULL DEFAULT 'bcrypt',
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user (id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS booking_status
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_active   BOOLEAN DEFAULT TRUE
    );

TRUNCATE TABLE booking_status;

INSERT INTO booking_status (name, description)
VALUES ('TEMPORARY_HOLD', 'Seat temporarily reserved (2 days max)'),
       ('CONFIRMED_BOOKING', 'Booking confirmed but not yet paid'),
       ('PURCHASED', 'Seat purchased and paid for'),
       ('EXPIRED', 'Temporary booking expired'),
       ('CANCELLED', 'Booking cancelled by user');

CREATE TABLE IF NOT EXISTS event
(
    id          SERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    event_date  DATE         NOT NULL,
    start_time  TIME         NOT NULL,
    end_time    TIME         NOT NULL,
    status_id   INT          NOT NULL,
    type_id     INT          NOT NULL,
    venue_id    INT          NOT NULL,
    FOREIGN KEY (status_id) REFERENCES event_status (id),
    FOREIGN KEY (type_id) REFERENCES event_type (id),
    FOREIGN KEY (venue_id) REFERENCES venue (id),
    -- Add constraints for business rules
    CONSTRAINT check_event_times CHECK (end_time > start_time),
    CONSTRAINT check_event_date_future CHECK (event_date >= CURRENT_DATE)
    );

-- Create booking table BEFORE seat table since seat references it
CREATE TABLE IF NOT EXISTS booking
(
    id                SERIAL PRIMARY KEY,
    booking_reference VARCHAR(20) UNIQUE NOT NULL,
    user_id           INT                NOT NULL,
    event_id          INT                NOT NULL,
    seat_id           INT                NOT NULL,
    booking_status_id INT                NOT NULL,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at        TIMESTAMP, -- For temporary bookings (2 days)
    confirmed_at      TIMESTAMP,
    purchased_at      TIMESTAMP,
    cancelled_at      TIMESTAMP,
    price             DECIMAL(10, 2)     NOT NULL,
    payment_date      TIMESTAMP,
    -- Constraints
    FOREIGN KEY (user_id) REFERENCES app_user (id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES event (id),
    -- seat_id FK will be added after seat table creation
    FOREIGN KEY (booking_status_id) REFERENCES booking_status (id),

    CONSTRAINT check_dates CHECK (
(expires_at IS NULL OR expires_at > created_at) AND
(confirmed_at IS NULL OR confirmed_at >= created_at) AND
(purchased_at IS NULL OR purchased_at >= created_at) AND
(cancelled_at IS NULL OR cancelled_at >= created_at)
    ),
    CONSTRAINT check_price CHECK (price > 0)
    );

CREATE TABLE IF NOT EXISTS seat
(
    id                  SERIAL PRIMARY KEY,
    venue_id            INT            NOT NULL,
    section             VARCHAR(50),
    row_number          INT            NOT NULL,
    seat_number         INT            NOT NULL,
    base_price          DECIMAL(10, 2) NOT NULL,
    status_id           INT            NOT NULL,
    current_booking_id  INT, -- Current active booking/purchase
    is_available        BOOLEAN   DEFAULT TRUE,
    last_booking_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (venue_id) REFERENCES venue (id) ON DELETE CASCADE,
    FOREIGN KEY (status_id) REFERENCES seat_status (id),
    FOREIGN KEY (current_booking_id) REFERENCES booking (id),
    UNIQUE (venue_id, section, row_number, seat_number),
    CONSTRAINT check_seat_price CHECK (base_price > 0),
    CONSTRAINT check_seat_numbers CHECK (row_number > 0 AND seat_number > 0)
    );

-- Add the missing FK constraint to booking table
ALTER TABLE booking
    ADD FOREIGN KEY (seat_id) REFERENCES seat (id);

CREATE TABLE IF NOT EXISTS event_seat
(
    event_id INT NOT NULL,
    seat_id  INT NOT NULL,
    PRIMARY KEY (event_id, seat_id),
    FOREIGN KEY (event_id) REFERENCES event (id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES seat (id)
    );

-- Indexes for performance
CREATE INDEX idx_user_country_id ON app_user (country_id);
CREATE INDEX idx_city_country_id ON city (country_id);
CREATE INDEX idx_venue_city_id ON venue (city_id);
CREATE INDEX idx_event_venue_id ON event (venue_id);
CREATE INDEX idx_event_seat_seat_id ON event_seat (seat_id);
CREATE INDEX idx_event_seat_event_id ON event_seat (event_id);
CREATE INDEX idx_booking_user_status ON booking (user_id, booking_status_id);
CREATE INDEX idx_booking_event_status ON booking (event_id, booking_status_id);
CREATE INDEX idx_event_date_status ON event (event_date, status_id);
CREATE INDEX idx_seat_venue_status ON seat (venue_id, status_id);
CREATE INDEX idx_booking_expires_at ON booking (expires_at) WHERE expires_at IS NOT NULL;
CREATE INDEX idx_seat_available ON seat (venue_id, is_available);

-- Function to generate booking references
CREATE SEQUENCE IF NOT EXISTS booking_reference_seq START 1;

CREATE OR REPLACE FUNCTION generate_booking_reference()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.booking_reference = 'BK' || LPAD(nextval('booking_reference_seq')::TEXT, 8, '0');
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_generate_booking_reference
    BEFORE INSERT
    ON booking
    FOR EACH ROW
    WHEN (NEW.booking_reference IS NULL OR NEW.booking_reference = '')
    EXECUTE FUNCTION generate_booking_reference();

-- Function to set expiration for temporary bookings
CREATE OR REPLACE FUNCTION set_booking_expiration()
    RETURNS TRIGGER AS
$$
BEGIN
    -- Set 2-day expiration for temporary holds
    IF NEW.booking_status_id = (SELECT id FROM booking_status WHERE name = 'TEMPORARY_HOLD')
        AND NEW.expires_at IS NULL THEN
        NEW.expires_at = NEW.created_at + INTERVAL '2 days';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_set_booking_expiration
    BEFORE INSERT OR UPDATE
                         ON booking
                         FOR EACH ROW
                         EXECUTE FUNCTION set_booking_expiration();

-- Clear existing data before inserting
TRUNCATE TABLE country CASCADE;

INSERT INTO country (name)
VALUES ('Armenia'),
       ('Australia'),
       ('Austria'),
       ('Belgium'),
       ('France'),
       ('Georgia'),
       ('Germany'),
       ('Italy'),
       ('Poland'),
       ('Spain'),
       ('Portugal'),
       ('United Kingdom'),
       ('United States');

INSERT INTO city (name, country_id)
VALUES ('Washington, D.C.', (SELECT id FROM country WHERE name = 'United States')),
       ('New York', (SELECT id FROM country WHERE name = 'United States')),
       ('Los Angeles', (SELECT id FROM country WHERE name = 'United States')),
       ('Chicago', (SELECT id FROM country WHERE name = 'United States')),
       ('Yerevan', (SELECT id FROM country WHERE name = 'Armenia')),
       ('Tbilisi', (SELECT id FROM country WHERE name = 'Georgia')),
       ('London', (SELECT id FROM country WHERE name = 'United Kingdom')),
       ('Manchester', (SELECT id FROM country WHERE name = 'United Kingdom')),
       ('Liverpool', (SELECT id FROM country WHERE name = 'United Kingdom')),
       ('Paris', (SELECT id FROM country WHERE name = 'France')),
       ('Marseille', (SELECT id FROM country WHERE name = 'France')),
       ('Lyon', (SELECT id FROM country WHERE name = 'France')),
       ('Berlin', (SELECT id FROM country WHERE name = 'Germany')),
       ('Munich', (SELECT id FROM country WHERE name = 'Germany')),
       ('Hamburg', (SELECT id FROM country WHERE name = 'Germany')),
       ('Madrid', (SELECT id FROM country WHERE name = 'Spain')),
       ('Barcelona', (SELECT id FROM country WHERE name = 'Spain')),
       ('Lisbon', (SELECT id FROM country WHERE name = 'Portugal')),
       ('Rome', (SELECT id FROM country WHERE name = 'Italy')),
       ('Milan', (SELECT id FROM country WHERE name = 'Italy')),
       ('Venice', (SELECT id FROM country WHERE name = 'Italy')),
       ('Brussels', (SELECT id FROM country WHERE name = 'Belgium')),
       ('Vienna', (SELECT id FROM country WHERE name = 'Austria')),
       ('Warsaw', (SELECT id FROM country WHERE name = 'Poland')),
       ('Sydney', (SELECT id FROM country WHERE name = 'Australia')),
       ('Melbourne', (SELECT id FROM country WHERE name = 'Australia')),
       ('Brisbane', (SELECT id FROM country WHERE name = 'Australia')),
       ('Perth', (SELECT id FROM country WHERE name = 'Australia'));

END
TRANSACTION;


ALTER TABLE
    EVENT
    ADD
        available_attendees_capacity INT NOT NULL DEFAULT 0;

ALTER TABLE
    app_user
    ADD
        is_active BOOLEAN DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS ticket (
                                      id SERIAL PRIMARY KEY,
                                      ticket_number VARCHAR(20) UNIQUE NOT NULL,
    ticket_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP,
    qr_code TEXT,
    barcode TEXT,

    booking_id INT NOT NULL,
    seat_id INT NOT NULL,
    event_id INT NOT NULL,

    CONSTRAINT fk_ticket_booking FOREIGN KEY (booking_id) REFERENCES booking(id) ON DELETE CASCADE,
    CONSTRAINT fk_ticket_seat FOREIGN KEY (seat_id) REFERENCES seat(id),
    CONSTRAINT fk_ticket_event FOREIGN KEY (event_id) REFERENCES event(id),

    CONSTRAINT uq_booking_seat UNIQUE (booking_id, seat_id),
    CONSTRAINT chk_ticket_status CHECK (ticket_status IN ('ACTIVE', 'USED', 'CANCELLED'))
    );


CREATE TABLE IF NOT EXISTS booking_group (
                                             id SERIAL PRIMARY KEY,
                                             group_reference VARCHAR(20) UNIQUE NOT NULL,
    user_id INT NOT NULL,
    event_id INT NOT NULL,
    total_tickets INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES event(id),

    CONSTRAINT check_total_tickets CHECK (total_tickets > 0),
    CONSTRAINT check_total_amount CHECK (total_amount > 0)
    );

ALTER TABLE booking
    ADD COLUMN booking_group_id INT,
ADD FOREIGN KEY (booking_group_id) REFERENCES booking_group(id);

-- Generate group references
CREATE SEQUENCE IF NOT EXISTS booking_group_ref_seq START 1;

CREATE OR REPLACE FUNCTION generate_booking_group_reference()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.group_reference = 'BG' || LPAD(nextval('booking_group_ref_seq')::TEXT, 8, '0');
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_generate_booking_group_reference
    BEFORE INSERT
    ON booking_group
    FOR EACH ROW
    WHEN (NEW.group_reference IS NULL OR NEW.group_reference = '')
    EXECUTE FUNCTION generate_booking_group_reference();

-- Add indexes for performance
CREATE INDEX idx_ticket_booking_id ON ticket(booking_id);
CREATE INDEX idx_ticket_event_status ON ticket(event_id, ticket_status);
CREATE INDEX idx_booking_group_user_event ON booking_group(user_id, event_id);
CREATE INDEX idx_booking_group_id ON booking(booking_group_id);

-- Example views for easier querying

-- View to get booking summary with ticket count
CREATE VIEW booking_summary AS
SELECT
    bg.group_reference,
    bg.user_id,
    bg.event_id,
    bg.total_tickets,
    bg.total_amount,
    bg.created_at,
    COUNT(b.id) as individual_bookings,
    e.title as event_title,
    e.event_date,
    u.first_name,
    u.last_name,
    u.email
FROM booking_group bg
         LEFT JOIN booking b ON bg.id = b.booking_group_id
         JOIN event e ON bg.event_id = e.id
         JOIN app_user u ON bg.user_id = u.id
GROUP BY bg.id, e.id, u.id;

-- View to get ticket details
CREATE VIEW ticket_details AS
SELECT
    t.ticket_number,
    t.ticket_status,
    t.issued_at,
    t.used_at,
    b.booking_reference,
    s.section,
    s.row_number,
    s.seat_number,
    e.title as event_title,
    e.event_date,
    e.start_time,
    v.name as venue_name,
    v.address as venue_address
FROM ticket t
         JOIN booking b ON t.booking_id = b.id
         JOIN seat s ON t.seat_id = s.id
         JOIN event e ON t.event_id = e.id
         JOIN venue v ON e.venue_id = v.id;
