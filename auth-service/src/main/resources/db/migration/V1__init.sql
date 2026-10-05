CREATE TABLE app_user (
                          id BIGSERIAL PRIMARY KEY,
                          email VARCHAR(255) UNIQUE NOT NULL,
                          password_hash VARCHAR(100) NOT NULL,
                          roles VARCHAR(100) NOT NULL DEFAULT 'USER',
                          enabled BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE refresh_token (
                               id BIGSERIAL PRIMARY KEY,
                               token_hash VARCHAR(64) UNIQUE NOT NULL,
                               user_id BIGINT NOT NULL REFERENCES app_user(id),
                               family_id UUID NOT NULL,
                               used BOOLEAN NOT NULL DEFAULT FALSE,
                               revoked BOOLEAN NOT NULL DEFAULT FALSE,
                               expires_at TIMESTAMP NOT NULL,
                               created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_rt_family ON refresh_token(family_id);
CREATE INDEX idx_rt_user ON refresh_token(user_id);