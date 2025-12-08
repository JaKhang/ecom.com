CREATE TABLE users
(
    id                              CHAR(26) PRIMARY KEY,
    created_at                      TIMESTAMP    NOT NULL,
    updated_at                      TIMESTAMP    NOT NULL,
    email                           VARCHAR(255) NOT NULL UNIQUE,
    password_hash                   VARCHAR(255) NOT NULL,
    full_name                       VARCHAR(255) NOT NULL,
    is_active                       BOOLEAN      NOT NULL DEFAULT TRUE,
    deleted_at                      TIMESTAMP,
    verified_at                     TIMESTAMP,
    verify_token                    VARCHAR(255),
    verify_token_expired_at         TIMESTAMP,
    reset_password_token            VARCHAR(255),
    avatar                          VARCHAR(255),
    reset_password_token_expired_at TIMESTAMP
);

CREATE INDEX idx_users_email ON users (email);

CREATE TABLE roles
(
    id         CHAR(26) PRIMARY KEY, -- Assuming ULID is a 26-character string
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    name       VARCHAR(255) NOT NULL,
    code       VARCHAR(50)  NOT NULL,
    deleted_at TIMESTAMP
);


CREATE TABLE users_roles
(
    user_id CHAR(26),
    role_id CHAR(26),
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);


INSERT INTO roles (id,
                   created_at,
                   updated_at,
                   name,
                   code,
                   deleted_at)
VALUES ('01KBY8RFNHJTKTVAMQ9K1WNKNM', -- Unique ID for the role
        CURRENT_TIMESTAMP, -- Created at timestamp
        CURRENT_TIMESTAMP, -- Updated at timestamp
        'Administrator', -- Role name
        'ROLE_ADMIN', -- Role code
        NULL -- Deleted at (not deleted)
       );

INSERT INTO users (id,
                   created_at,
                   updated_at,
                   email,
                   password_hash,
                   full_name,
                   is_active,
                   deleted_at,
                   verified_at)
VALUES ('01KBY8QMDZ4609C11CBNJ694R8', -- Unique ID for the admin user (example ULID)
        CURRENT_TIMESTAMP, -- Created at timestamp
        CURRENT_TIMESTAMP, -- Updated at timestamp
        'admin@example.com', -- Admin email
        '$2a$10$EwX9xJlUgdWaKNbFrz/atOhzUwBtIqFQusuhBoyS/PuMD.HVB4smy', -- Hashed password
        'Admin User', -- Full name
        TRUE, -- Active status
        NULL, -- Deleted at (not deleted)
        CURRENT_TIMESTAMP);


INSERT INTO users_roles(role_id, user_id) VALUE ('01KBY8RFNHJTKTVAMQ9K1WNKNM', '01KBY8QMDZ4609C11CBNJ694R8')

