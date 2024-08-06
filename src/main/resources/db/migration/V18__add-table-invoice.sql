DROP TABLE IF EXISTS invoice;

CREATE TABLE
    invoice (
        id int NOT NULL AUTO_INCREMENT,
        orders_id int DEFAULT NULL,
        total_quantity int DEFAULT NULL,
        total_price double DEFAULT NULL,
        created_at timestamp NULL DEFAULT NULL,
        created_by int DEFAULT NULL,
        updated_by int DEFAULT NULL,
        updated_at timestamp NULL DEFAULT NULL,
        is_deleted boolean DEFAULT NULL,
        PRIMARY KEY (id)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8;