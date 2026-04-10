ALTER TABLE photos DROP COLUMN IF EXISTS incident_id,
                   DROP COLUMN IF EXISTS user_id;

CREATE TABLE IF NOT EXISTS incident_media (
                                id BIGSERIAL PRIMARY KEY,
                                media_asset_id UUID NOT NULL REFERENCES photos(id) ON DELETE CASCADE,
                                incident_id BIGINT REFERENCES incidents(id) ON DELETE CASCADE,
                                user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                UNIQUE(incident_id, media_asset_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_incident_media_incident ON incident_media(incident_id);
CREATE INDEX IF NOT EXISTS idx_incident_media_asset ON incident_media(media_asset_id);
CREATE INDEX IF NOT EXISTS idx_incident_media_user ON incident_media(user_id);