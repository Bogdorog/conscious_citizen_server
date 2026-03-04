-- ========================
-- Токены для смены пароля будут храниться в отдельной таблице
-- ========================
ALTER TABLE users DROP COLUMN IF EXISTS reset_token, DROP COLUMN IF EXISTS reset_token_expiration;
CREATE TABLE IF NOT EXISTS password_reset_tokens (
                                       id BIGSERIAL PRIMARY KEY,
                                       user_id BIGINT NOT NULL,
                                       token_hash VARCHAR(255) NOT NULL UNIQUE,
                                       expires_at TIMESTAMP NOT NULL,
                                       used BOOLEAN NOT NULL DEFAULT false
);

CREATE INDEX idx_password_reset_user_id
    ON password_reset_tokens(user_id);