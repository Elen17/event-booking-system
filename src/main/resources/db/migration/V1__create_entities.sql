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
    name    VARCHAR(255) NOT NULL, -- Added missing venue name
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

CREATE TABLE IF NOT EXISTS event_status
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS event_type
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS seat_status
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS app_user
(
    id            SERIAL PRIMARY KEY,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL, -- Added password field
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

-- Clear and populate booking_status
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
VALUES ('Afghanistan'),
       ('Aland Islands'),
       ('Albania'),
       ('Algeria'),
       ('American Samoa'),
       ('Andorra'),
       ('Angola'),
       ('Anguilla'),
       ('Antarctica'),
       ('Antigua and Barbuda'),
       ('Argentina'),
       ('Armenia'),
       ('Aruba'),
       ('Australia'),
       ('Austria'),
       ('Azerbaijan'),
       ('The Bahamas'),
       ('Bahrain'),
       ('Bangladesh'),
       ('Barbados'),
       ('Belarus'),
       ('Belgium'),
       ('Belize'),
       ('Benin'),
       ('Bermuda'),
       ('Bhutan'),
       ('Bolivia'),
       ('Bosnia and Herzegovina'),
       ('Botswana'),
       ('Bouvet Island'),
       ('Brazil'),
       ('British Indian Ocean Territory'),
       ('Brunei'),
       ('Bulgaria'),
       ('Burkina Faso'),
       ('Burundi'),
       ('Cambodia'),
       ('Cameroon'),
       ('Canada'),
       ('Monaco'),
       ('Cape Verde'),
       ('Cayman Islands'),
       ('Central African Republic'),
       ('Chad'),
       ('Chile'),
       ('China'),
       ('Christmas Island'),
       ('Cocos (Keeling) Islands'),
       ('Colombia'),
       ('Comoros'),
       ('Congo'),
       ('Democratic Republic of the Congo'),
       ('Cook Islands'),
       ('Costa Rica'),
       ('Cote D''Ivoire (Ivory Coast)'),
       ('Croatia'),
       ('Cuba'),
       ('Cyprus'),
       ('Czech Republic'),
       ('Denmark'),
       ('Djibouti'),
       ('Dominica'),
       ('Dominican Republic'),
       ('Timor-Leste'),
       ('Ecuador'),
       ('Egypt'),
       ('El Salvador'),
       ('Equatorial Guinea'),
       ('Eritrea'),
       ('Estonia'),
       ('Ethiopia'),
       ('Falkland Islands'),
       ('Faroe Islands'),
       ('Fiji Islands'),
       ('Finland'),
       ('France'),
       ('French Guiana'),
       ('French Polynesia'),
       ('French Southern Territories'),
       ('Gabon'),
       ('The Gambia'),
       ('Georgia'),
       ('Germany'),
       ('Ghana'),
       ('Gibraltar'),
       ('Greece'),
       ('Greenland'),
       ('Grenada'),
       ('Guadeloupe'),
       ('Guam'),
       ('Guatemala'),
       ('Guernsey'),
       ('Guinea'),
       ('Guinea-Bissau'),
       ('Guyana'),
       ('Haiti'),
       ('Heard Island and McDonald Islands'),
       ('Honduras'),
       ('Hong Kong S.A.R.'),
       ('Hungary'),
       ('Iceland'),
       ('India'),
       ('Mongolia'),
       ('Indonesia'),
       ('Iran'),
       ('Iraq'),
       ('Ireland'),
       ('Israel'),
       ('Italy'),
       ('Jamaica'),
       ('Japan'),
       ('Jersey'),
       ('Jordan'),
       ('Micronesia'),
       ('Kazakhstan'),
       ('Kenya'),
       ('Kiribati'),
       ('North Korea'),
       ('South Korea'),
       ('Kuwait'),
       ('Kyrgyzstan'),
       ('Laos'),
       ('Latvia'),
       ('Lebanon'),
       ('Lesotho'),
       ('Liberia'),
       ('Libya'),
       ('Liechtenstein'),
       ('Lithuania'),
       ('Luxembourg'),
       ('Macau S.A.R.'),
       ('North Macedonia'),
       ('Madagascar'),
       ('Malawi'),
       ('Malaysia'),
       ('Maldives'),
       ('Mali'),
       ('Malta'),
       ('Man (Isle of)'),
       ('Marshall Islands'),
       ('Martinique'),
       ('Mauritania'),
       ('Mauritius'),
       ('Mayotte'),
       ('Mexico'),
       ('Moldova'),
       ('Montenegro'),
       ('Montserrat'),
       ('Morocco'),
       ('Mozambique'),
       ('Myanmar'),
       ('Namibia'),
       ('Nauru'),
       ('Nepal'),
       ('Bonaire, Sint Eustatius and Saba'),
       ('Netherlands'),
       ('New Caledonia'),
       ('New Zealand'),
       ('Nicaragua'),
       ('Niger'),
       ('Nigeria'),
       ('Niue'),
       ('Norfolk Island'),
       ('Northern Mariana Islands'),
       ('Norway'),
       ('Oman'),
       ('Pakistan'),
       ('Palau'),
       ('Palestinian Territory Occupied'),
       ('Panama'),
       ('Papua New Guinea'),
       ('Paraguay'),
       ('Peru'),
       ('Philippines'),
       ('Pitcairn Island'),
       ('Poland'),
       ('Portugal'),
       ('Puerto Rico'),
       ('Qatar'),
       ('Reunion'),
       ('Romania'),
       ('Russia'),
       ('Rwanda'),
       ('Saint Helena'),
       ('Saint Kitts and Nevis'),
       ('Saint Lucia'),
       ('Saint Pierre and Miquelon'),
       ('Saint Vincent and the Grenadines'),
       ('Saint-Barthelemy'),
       ('Saint-Martin (French part)'),
       ('Samoa'),
       ('San Marino'),
       ('Sao Tome and Principe'),
       ('Saudi Arabia'),
       ('Senegal'),
       ('Serbia'),
       ('Seychelles'),
       ('Sierra Leone'),
       ('Singapore'),
       ('Slovakia'),
       ('Slovenia'),
       ('Solomon Islands'),
       ('Somalia'),
       ('South Africa'),
       ('South Georgia'),
       ('South Sudan'),
       ('Spain'),
       ('Sri Lanka'),
       ('Sudan'),
       ('Suriname'),
       ('Svalbard and Jan Mayen Islands'),
       ('Eswatini'),
       ('Sweden'),
       ('Switzerland'),
       ('Syria'),
       ('Taiwan'),
       ('Tajikistan'),
       ('Tanzania'),
       ('Thailand'),
       ('Togo'),
       ('Tokelau'),
       ('Tonga'),
       ('Trinidad and Tobago'),
       ('Tunisia'),
       ('Turkey'),
       ('Turkmenistan'),
       ('Turks and Caicos Islands'),
       ('Tuvalu'),
       ('Uganda'),
       ('Ukraine'),
       ('United Arab Emirates'),
       ('United Kingdom'),
       ('United States'),
       ('United States Minor Outlying Islands'),
       ('Uruguay'),
       ('Uzbekistan'),
       ('Vanuatu'),
       ('Vatican City State (Holy See)'),
       ('Venezuela'),
       ('Vietnam'),
       ('Virgin Islands (British)'),
       ('Virgin Islands (US)'),
       ('Wallis and Futuna Islands'),
       ('Western Sahara'),
       ('Yemen'),
       ('Zambia'),
       ('Zimbabwe'),
       ('Kosovo'),
       ('Curaçao'),
       ('Sint Maarten (Dutch part)');

INSERT INTO city (name, country_id)
VALUES ('Washington, D.C.', (SELECT id FROM country WHERE name = 'United States')),
       ('New York', (SELECT id FROM country WHERE name = 'United States')),
       ('Los Angeles', (SELECT id FROM country WHERE name = 'United States')),
       ('Chicago', (SELECT id FROM country WHERE name = 'United States')),
       ('Toronto', (SELECT id FROM country WHERE name = 'Canada')),
       ('Vancouver', (SELECT id FROM country WHERE name = 'Canada')),
       ('Montreal', (SELECT id FROM country WHERE name = 'Canada')),
       ('Mexico City', (SELECT id FROM country WHERE name = 'Mexico')),
       ('Guadalajara', (SELECT id FROM country WHERE name = 'Mexico'));

-- South America
INSERT INTO city (name, country_id)
VALUES ('Buenos Aires', (SELECT id FROM country WHERE name = 'Argentina')),
       ('Rio de Janeiro', (SELECT id FROM country WHERE name = 'Brazil')),
       ('Sao Paulo', (SELECT id FROM country WHERE name = 'Brazil')),
       ('Brasília', (SELECT id FROM country WHERE name = 'Brazil')),
       ('Lima', (SELECT id FROM country WHERE name = 'Peru')),
       ('Santiago', (SELECT id FROM country WHERE name = 'Chile')),
       ('Bogotá', (SELECT id FROM country WHERE name = 'Colombia')),
       ('Caracas', (SELECT id FROM country WHERE name = 'Venezuela'));

-- Europe
INSERT INTO city (name, country_id)
VALUES ('Yerevan', (SELECT id FROM country WHERE name = 'Armenia')),
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
       ('Rome', (SELECT id FROM country WHERE name = 'Italy')),
       ('Milan', (SELECT id FROM country WHERE name = 'Italy')),
       ('Venice', (SELECT id FROM country WHERE name = 'Italy')),
       ('Amsterdam', (SELECT id FROM country WHERE name = 'Netherlands')),
       ('Brussels', (SELECT id FROM country WHERE name = 'Belgium')),
       ('Vienna', (SELECT id FROM country WHERE name = 'Austria')),
       ('Prague', (SELECT id FROM country WHERE name = 'Czech Republic')),
       ('Warsaw', (SELECT id FROM country WHERE name = 'Poland')),
       ('Moscow', (SELECT id FROM country WHERE name = 'Russia'));

-- Asia
INSERT INTO city (name, country_id)
VALUES ('Tokyo', (SELECT id FROM country WHERE name = 'Japan')),
       ('Osaka', (SELECT id FROM country WHERE name = 'Japan')),
       ('Kyoto', (SELECT id FROM country WHERE name = 'Japan')),
       ('Beijing', (SELECT id FROM country WHERE name = 'China')),
       ('Shanghai', (SELECT id FROM country WHERE name = 'China')),
       ('Hong Kong', (SELECT id FROM country WHERE name = 'China')),
       ('Seoul', (SELECT id FROM country WHERE name = 'South Korea')),
       ('Busan', (SELECT id FROM country WHERE name = 'South Korea')),
       ('Delhi', (SELECT id FROM country WHERE name = 'India')),
       ('Mumbai', (SELECT id FROM country WHERE name = 'India')),
       ('Bangalore', (SELECT id FROM country WHERE name = 'India')),
       ('Singapore', (SELECT id FROM country WHERE name = 'Singapore')),
       ('Bangkok', (SELECT id FROM country WHERE name = 'Thailand')),
       ('Kuala Lumpur', (SELECT id FROM country WHERE name = 'Malaysia')),
       ('Jakarta', (SELECT id FROM country WHERE name = 'Indonesia')),
       ('Manila', (SELECT id FROM country WHERE name = 'Philippines')),
       ('Hanoi', (SELECT id FROM country WHERE name = 'Vietnam'));

-- Middle East
INSERT INTO city (name, country_id)
VALUES ('Dubai', (SELECT id FROM country WHERE name = 'United Arab Emirates')),
       ('Abu Dhabi', (SELECT id FROM country WHERE name = 'United Arab Emirates')),
       ('Riyadh', (SELECT id FROM country WHERE name = 'Saudi Arabia')),
       ('Jeddah', (SELECT id FROM country WHERE name = 'Saudi Arabia')),
       ('Doha', (SELECT id FROM country WHERE name = 'Qatar')),
       ('Tehran', (SELECT id FROM country WHERE name = 'Iran')),
       ('Istanbul', (SELECT id FROM country WHERE name = 'Turkey')),
       ('Jerusalem', (SELECT id FROM country WHERE name = 'Israel'));

-- Africa
INSERT INTO city (name, country_id)
VALUES ('Cairo', (SELECT id FROM country WHERE name = 'Egypt')),
       ('Alexandria', (SELECT id FROM country WHERE name = 'Egypt')),
       ('Nairobi', (SELECT id FROM country WHERE name = 'Kenya')),
       ('Cape Town', (SELECT id FROM country WHERE name = 'South Africa')),
       ('Lagos', (SELECT id FROM country WHERE name = 'Nigeria')),
       ('Casablanca', (SELECT id FROM country WHERE name = 'Morocco'));

-- Oceania
INSERT INTO city (name, country_id)
VALUES ('Sydney', (SELECT id FROM country WHERE name = 'Australia')),
       ('Melbourne', (SELECT id FROM country WHERE name = 'Australia')),
       ('Brisbane', (SELECT id FROM country WHERE name = 'Australia')),
       ('Perth', (SELECT id FROM country WHERE name = 'Australia')),
       ('Auckland', (SELECT id FROM country WHERE name = 'New Zealand')),
       ('Wellington', (SELECT id FROM country WHERE name = 'New Zealand'));

END TRANSACTION;