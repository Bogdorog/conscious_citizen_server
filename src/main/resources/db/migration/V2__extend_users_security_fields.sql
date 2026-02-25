-- ========================
-- Меняем таблицу пользователей, добавляя поля для безопасности (пароль, смена пароля)
-- ========================
ALTER TABLE users
    ADD COLUMN password_hash VARCHAR(255) NOT NULL,
    ADD COLUMN reset_token VARCHAR(255),
    ADD COLUMN reset_token_expiration TIMESTAMP,
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT true;

UPDATE users SET active = true WHERE active IS NULL;
-- ========================
-- Встраиваем документы в таблицу инцидентов
-- ========================
ALTER TABLE incidents
    ADD COLUMN file_path VARCHAR(255) NOT NULL;

DROP TABLE documents CASCADE;
