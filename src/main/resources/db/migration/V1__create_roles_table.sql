CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS roles (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     name VARCHAR(20) NOT NULL UNIQUE
);

INSERT INTO roles(name) VALUES ('ROLE_USER'), ('ROLE_ADMIN')
ON CONFLICT DO NOTHING;



