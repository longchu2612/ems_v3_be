DROP TABLE IF EXISTS eatery;

CREATE TABLE
    eatery (
        id int NOT NULL AUTO_INCREMENT,
        name VARCHAR(255) DEFAULT NULL,
        phone VARCHAR(255) DEFAULT NULL,
        tax_no VARCHAR(255) DEFAULT NULL,
        address VARCHAR(255) DEFAULT NULL,
        created_at timestamp NULL DEFAULT NULL,
        created_by int DEFAULT NULL,
        updated_by int DEFAULT NULL,
        updated_at timestamp NULL DEFAULT NULL,
        is_deleted boolean DEFAULT NULL,
        PRIMARY KEY (id)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8;