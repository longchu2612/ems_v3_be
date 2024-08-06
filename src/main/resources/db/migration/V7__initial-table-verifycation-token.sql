DROP TABLE IF EXISTS verification_token;
CREATE TABLE verification_token (
    id int NOT NULL AUTO_INCREMENT,
    token varchar(255) DEFAULT NULL,
    otp varchar(10) DEFAULT NULL,
    expired_time timestamp DEFAULT NULL,
    created_by int DEFAULT NULL,
    updated_by int DEFAULT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP,
    is_deleted boolean NOT NULL DEFAULT false,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
