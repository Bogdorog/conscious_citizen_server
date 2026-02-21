-- ========================
-- Роли пользователей
-- ========================
CREATE TABLE  IF NOT EXISTS roles (
                       id INTEGER PRIMARY KEY,
                       name VARCHAR(255) NOT NULL UNIQUE
);

-- ========================
-- Типы инцидентов
-- ========================
CREATE TABLE  IF NOT EXISTS incident_types (
                                id INTEGER PRIMARY KEY,
                                name VARCHAR(255) NOT NULL UNIQUE
);

-- ========================
-- Данные пользователей
-- ========================
CREATE TABLE  IF NOT EXISTS users (
                       id BIGSERIAL PRIMARY KEY,
                       full_name VARCHAR(255) NOT NULL,
                       address VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       phone VARCHAR(50) NOT NULL UNIQUE,
                       role_id INTEGER NOT NULL,

                       CONSTRAINT fk_users_role
                           FOREIGN KEY (role_id)
                               REFERENCES roles(id)
                               ON DELETE RESTRICT
);

CREATE INDEX idx_users_role_id ON users(role_id);

-- ========================
-- Инциденты
-- ========================
CREATE TABLE  IF NOT EXISTS incidents (
                           id BIGSERIAL PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           type_id INTEGER NOT NULL,
                           message TEXT NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT fk_incidents_user
                               FOREIGN KEY (user_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE,

                           CONSTRAINT fk_incidents_type
                               FOREIGN KEY (type_id)
                                   REFERENCES incident_types(id)
                                   ON DELETE RESTRICT
);

CREATE INDEX idx_incidents_user_id ON incidents(user_id);
CREATE INDEX idx_incidents_type_id ON incidents(type_id);

-- ========================
-- Фотографии инцидентов
-- ========================
CREATE TABLE  IF NOT EXISTS photos (
                        id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        incident_id BIGINT NOT NULL,
                        file_path VARCHAR(255) NOT NULL,

                        CONSTRAINT fk_photos_user
                            FOREIGN KEY (user_id)
                                REFERENCES users(id)
                                ON DELETE CASCADE,

                        CONSTRAINT fk_photos_incident
                            FOREIGN KEY (incident_id)
                                REFERENCES incidents(id)
                                ON DELETE CASCADE
);

CREATE INDEX idx_photos_user_id ON photos(user_id);
CREATE INDEX idx_photos_incident_id ON photos(incident_id);

-- ========================
-- Документы с обращениями
-- ========================
CREATE TABLE  IF NOT EXISTS documents (
                           id BIGSERIAL PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           incident_id BIGINT NOT NULL,
                           file_path VARCHAR(255) NOT NULL,

                           CONSTRAINT fk_documents_user
                               FOREIGN KEY (user_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE,

                           CONSTRAINT fk_documents_incident
                               FOREIGN KEY (incident_id)
                                   REFERENCES incidents(id)
                                   ON DELETE CASCADE
);

CREATE INDEX idx_documents_user_id ON documents(user_id);
CREATE INDEX idx_documents_incident_id ON documents(incident_id);