ALTER TABLE event_status
    ADD COLUMN display_name VARCHAR(255);

ALTER TABLE booking_status
    ADD COLUMN display_name VARCHAR(255);

ALTER TABLE seat_status
    ADD COLUMN display_name VARCHAR(255);

UPDATE event_status SET display_name = 'Planned' where name = 'PLANNED';
UPDATE event_status SET display_name = 'Cancelled' where name = 'CANCELLED';
UPDATE event_status SET display_name = 'Completed' where name = 'COMPLETED';


UPDATE booking_status SET display_name = 'Temporary Hold' where name = 'TEMPORARY_HOLD';
UPDATE booking_status SET display_name = 'Purchased' where name = 'PURCHASED';
UPDATE booking_status SET display_name = 'Expired' where name = 'EXPIRED';
UPDATE booking_status SET display_name = 'Cancelled' where name = 'CANCELLED';

UPDATE seat_status SET display_name = 'Available' where name = 'AVAILABLE';
UPDATE seat_status SET display_name = 'Reserved' where name = 'RESERVED';
UPDATE seat_status SET display_name = 'Purchased' where name = 'PURCHASED';

