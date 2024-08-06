DROP TABLE IF EXISTS user_role;
CREATE TABLE user_role (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id int NOT NULL,
    role_id int NOT NULL,
    created_by int DEFAULT NULL,
    updated_by int DEFAULT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP,
    is_deleted boolean NOT NULL DEFAULT false
) ENGINE = InnoDB DEFAULT CHARSET = utf8;
