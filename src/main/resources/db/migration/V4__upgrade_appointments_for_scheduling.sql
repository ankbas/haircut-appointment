INSERT INTO services (audience, service_type, price, duration_minutes)
SELECT 'MEN', 'HAIRCUT', 0.00, 30
WHERE EXISTS (SELECT 1 FROM appointments)
ON CONFLICT (audience, service_type) DO NOTHING;

INSERT INTO professionals (name, bio, active)
SELECT 'Legacy Professional', 'Assigned to appointments created before professional scheduling', FALSE
WHERE EXISTS (SELECT 1 FROM appointments)
  AND NOT EXISTS (SELECT 1 FROM professionals WHERE name = 'Legacy Professional');

INSERT INTO professional_services (professional_id, service_id)
SELECT p.id, s.id
FROM professionals p
JOIN services s ON s.audience = 'MEN' AND s.service_type = 'HAIRCUT'
WHERE p.name = 'Legacy Professional'
ON CONFLICT DO NOTHING;

ALTER TABLE appointments RENAME COLUMN name TO customer_name;
ALTER TABLE appointments RENAME COLUMN phone_number TO customer_phone;
ALTER TABLE appointments RENAME COLUMN email TO customer_email;

ALTER TABLE appointments
    ADD COLUMN professional_id BIGINT,
    ADD COLUMN service_id BIGINT,
    ADD COLUMN start_time TIMESTAMP,
    ADD COLUMN end_time TIMESTAMP,
    ADD COLUMN status VARCHAR(20);

UPDATE appointments a
SET professional_id = (SELECT MIN(id) FROM professionals WHERE name = 'Legacy Professional'),
    service_id = (SELECT id FROM services WHERE audience = 'MEN' AND service_type = 'HAIRCUT'),
    start_time = a.appointment_date + a.appointment_time,
    end_time = a.appointment_date + a.appointment_time + INTERVAL '30 minutes',
    status = 'BOOKED';

ALTER TABLE appointments
    ALTER COLUMN professional_id SET NOT NULL,
    ALTER COLUMN service_id SET NOT NULL,
    ALTER COLUMN start_time SET NOT NULL,
    ALTER COLUMN end_time SET NOT NULL,
    ALTER COLUMN status SET NOT NULL;

ALTER TABLE appointments DROP CONSTRAINT uk_appointment_slot;
ALTER TABLE appointments DROP COLUMN appointment_date;
ALTER TABLE appointments DROP COLUMN appointment_time;

ALTER TABLE appointments
    ADD CONSTRAINT fk_appointments_professional
        FOREIGN KEY (professional_id) REFERENCES professionals (id),
    ADD CONSTRAINT fk_appointments_service
        FOREIGN KEY (service_id) REFERENCES services (id),
    ADD CONSTRAINT ck_appointment_times CHECK (end_time > start_time),
    ADD CONSTRAINT ck_appointment_status
        CHECK (status IN ('BOOKED', 'COMPLETED', 'CANCELLED', 'NO_SHOW'));

CREATE INDEX idx_appointments_professional_time
    ON appointments (professional_id, start_time, end_time);
