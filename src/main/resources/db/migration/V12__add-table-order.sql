DROP TABLE IF EXISTS orders;

CREATE TABLE
    orders (
        id int NOT NULL AUTO_INCREMENT,
        table_id int DEFAULT NULL,
        note varchar(255) DEFAULT NULL,
        customer_name varchar(255) DEFAULT NULL,
        total_quantity int DEFAULT NULL,
        total_price double DEFAULT NULL,
        status int DEFAULT NULL,
        created_at timestamp NULL DEFAULT NULL,
        created_by int DEFAULT NULL,
        updated_by int DEFAULT NULL,
        updated_at timestamp NULL DEFAULT NULL,
        is_deleted boolean DEFAULT NULL,
        PRIMARY KEY (id)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8;