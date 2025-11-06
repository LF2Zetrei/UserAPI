CREATE TABLE jwt_blacklist (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               token VARCHAR(512) NOT NULL UNIQUE,
                               blacklisted_at TIMESTAMP NOT NULL
);
