CREATE TABLE refresh_tokens (
                                id          BIGSERIAL PRIMARY KEY,
                                token       VARCHAR(36)  NOT NULL UNIQUE,
                                user_id     BIGINT       NOT NULL UNIQUE,
                                expires_at  TIMESTAMP    NOT NULL,
                                created_at  TIMESTAMP    NOT NULL DEFAULT now(),

                                CONSTRAINT fk_refresh_tokens_user
                                    FOREIGN KEY (user_id) REFERENCES users(id)
                                        ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);