CREATE EXTENSION IF NOT EXISTS "pgcrypto";


CREATE TABLE roles (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       name VARCHAR(20) NOT NULL UNIQUE
);

INSERT INTO roles(name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');


