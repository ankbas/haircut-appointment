INSERT INTO services (audience, service_type, price, duration_minutes) VALUES
    ('MEN', 'HAIRCUT', 55.00, 45),
    ('MEN', 'BEARD_TRIM', 30.00, 30),
    ('MEN', 'HAIR_AND_BEARD', 78.00, 60),
    ('WOMEN', 'HAIRCUT', 85.00, 60),
    ('WOMEN', 'HAIR_STYLING', 70.00, 60),
    ('WOMEN', 'WAXING', 45.00, 45),
    ('WOMEN', 'THREADING', 28.00, 30),
    ('WOMEN', 'FACIAL', 110.00, 75),
    ('WOMEN', 'CLEANSING', 80.00, 60),
    ('WOMEN', 'MAKEUP', 125.00, 90),
    ('WOMEN', 'NAILS', 65.00, 60)
ON CONFLICT (audience, service_type) DO NOTHING;

INSERT INTO professionals (name, bio, active)
SELECT 'Amara Cole', 'Precision cuts and effortless, lived-in styling with an editorial eye.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM professionals WHERE name = 'Amara Cole');
INSERT INTO professionals (name, bio, active)
SELECT 'Julian Reyes', 'Modern grooming specialist known for clean shapes and considered detail.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM professionals WHERE name = 'Julian Reyes');
INSERT INTO professionals (name, bio, active)
SELECT 'Sofia Laurent', 'Skin and beauty specialist creating restorative, luminous results.', TRUE
WHERE NOT EXISTS (SELECT 1 FROM professionals WHERE name = 'Sofia Laurent');

INSERT INTO professional_services (professional_id, service_id)
SELECT p.id, s.id FROM professionals p CROSS JOIN services s
WHERE p.name = 'Amara Cole' AND s.service_type IN ('HAIRCUT', 'HAIR_STYLING')
ON CONFLICT DO NOTHING;
INSERT INTO professional_services (professional_id, service_id)
SELECT p.id, s.id FROM professionals p CROSS JOIN services s
WHERE p.name = 'Julian Reyes' AND s.audience = 'MEN'
ON CONFLICT DO NOTHING;
INSERT INTO professional_services (professional_id, service_id)
SELECT p.id, s.id FROM professionals p CROSS JOIN services s
WHERE p.name = 'Sofia Laurent' AND s.audience = 'WOMEN' AND s.service_type IN ('WAXING','THREADING','FACIAL','CLEANSING','MAKEUP','NAILS')
ON CONFLICT DO NOTHING;

INSERT INTO professional_working_hours (professional_id, day_of_week, start_time, end_time)
SELECT p.id, day_name, TIME '09:00', TIME '18:00'
FROM professionals p
CROSS JOIN (VALUES ('MONDAY'),('TUESDAY'),('WEDNESDAY'),('THURSDAY'),('FRIDAY'),('SATURDAY'),('SUNDAY')) days(day_name)
WHERE p.name IN ('Amara Cole','Julian Reyes','Sofia Laurent')
ON CONFLICT (professional_id, day_of_week) DO NOTHING;
