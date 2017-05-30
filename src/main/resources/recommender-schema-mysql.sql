CREATE TABLE IF NOT EXISTS taste_preferences (
  user_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  preference FLOAT NOT NULL,
  timestamp BIGINT NOT NULL,
  PRIMARY KEY (user_id, item_id)
);
CREATE INDEX IF NOT EXISTS idx_taste_preferences_user_id ON taste_preferences (user_id);
CREATE INDEX IF NOT EXISTS idx_taste_preferences_item_id ON taste_preferences (item_id);


CREATE TABLE IF NOT EXISTS taste_id_migration (
  long_id BIGINT NOT NULL PRIMARY KEY,
  string_id VARCHAR(36) NOT NULL UNIQUE
);
