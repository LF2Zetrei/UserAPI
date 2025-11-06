CREATE TABLE refresh_tokens (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                user_id UUID UNIQUE NOT NULL,
                                token VARCHAR(255) NOT NULL UNIQUE,
                                expiry_date TIMESTAMP NOT NULL,
                                CONSTRAINT fk_user_refresh FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
