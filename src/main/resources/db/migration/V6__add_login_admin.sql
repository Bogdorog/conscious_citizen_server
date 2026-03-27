TRUNCATE users RESTART IDENTITY CASCADE ;
ALTER TABLE users
    ADD COLUMN login VARCHAR(255) NOT NULL UNIQUE;
INSERT INTO users VALUES (1, 'Админ', 'Россия', 'conscious-citizen2026@yandex.ru', '89879528722', '1', '$2a$12$nPheSOQoeJQlLuoFdhBqBu07YL86saJLK4aCYB94DpMuYJvJeymDa', true, 'admin')
ON CONFLICT (id) DO UPDATE SET full_name = EXCLUDED.full_name, address = EXCLUDED.address, email = EXCLUDED.email, phone = EXCLUDED.phone, password_hash = EXCLUDED.password_hash;