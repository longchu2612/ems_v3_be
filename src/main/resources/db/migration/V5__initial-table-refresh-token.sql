DROP TABLE IF EXISTS refresh_token;
CREATE TABLE refresh_token (
    id int NOT NULL AUTO_INCREMENT,
    user_id int DEFAULT NULL,
    refresh_token varchar(255) DEFAULT NULL,
    created_by int DEFAULT NULL,
    updated_by int DEFAULT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP,
    is_deleted boolean NOT NULL DEFAULT false,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;