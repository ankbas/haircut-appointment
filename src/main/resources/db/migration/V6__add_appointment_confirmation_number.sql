ALTER TABLE appointments ADD COLUMN confirmation_number VARCHAR(20);

UPDATE appointments
SET confirmation_number = 'ATL-' || UPPER(SUBSTRING(MD5(id::TEXT || start_time::TEXT), 1, 8));

ALTER TABLE appointments ALTER COLUMN confirmation_number SET NOT NULL;
ALTER TABLE appointments ADD CONSTRAINT uk_appointment_confirmation_number UNIQUE (confirmation_number);
