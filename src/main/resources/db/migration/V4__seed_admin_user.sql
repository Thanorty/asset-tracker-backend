-- =========================
-- DEFAULT ADMIN USER
-- username: admin
-- password: admin123
-- =========================
INSERT INTO users (username, email, password, role)
VALUES (
    'admin',
    'admin@assettracker.com',
    '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36PQm1z5C9USArOOoqHHPm2',
    'ADMIN'
) ON CONFLICT (username) DO NOTHING;

