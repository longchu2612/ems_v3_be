DROP TABLE IF EXISTS user;
CREATE TABLE user (
    id int NOT NULL AUTO_INCREMENT,
    user_name varchar(255) DEFAULT NULL,
    full_name varchar(255) DEFAULT NULL,
    password varchar(255) DEFAULT NULL,
    phone varchar(50) DEFAULT NULL,
    email varchar(255) DEFAULT NULL,
    avatar varchar(255) DEFAULT NULL,
    user_type int DEFAULT NULL,
    is_active boolean DEFAULT true,
    is_locked boolean DEFAULT false,
    last_login timestamp DEFAULT CURRENT_TIMESTAMP,
    last_failed_attempt timestamp DEFAULT CURRENT_TIMESTAMP,
    failed_attempt_times int DEFAULT 0,
    created_by int DEFAULT NULL,
    updated_by int DEFAULT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP,
    is_deleted boolean NOT NULL DEFAULT false,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;
