ALTER TABLE
    EVENT
    ADD
        available_attendees_capacity INT NOT NULL DEFAULT 0;

ALTER TABLE
    app_user
    ADD
        is_active BOOLEAN DEFAULT FALSE;

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
