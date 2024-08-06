DROP TABLE IF EXISTS notification;
CREATE TABLE notification (
    notification_id int NOT NULL AUTO_INCREMENT,
    user_id int DEFAULT NULL,
    title varchar(200) DEFAULT NULL,
    content_template varchar(200) DEFAULT NULL,
    content_value varchar(200) DEFAULT NULL,
    notification_type int DEFAULT NULL,
    is_read int DEFAULT NULL,
    created_by int DEFAULT NULL,
    updated_by int DEFAULT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp DEFAULT CURRENT_TIMESTAMP,
    is_deleted boolean NOT NULL DEFAULT false,
    PRIMARY KEY (notification_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

