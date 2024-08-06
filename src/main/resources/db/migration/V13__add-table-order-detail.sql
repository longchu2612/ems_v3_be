DROP TABLE IF EXISTS order_detail;
CREATE TABLE order_detail (
  id int NOT NULL AUTO_INCREMENT,
  orders_id int DEFAULT NULL,
  note varchar(255) DEFAULT NULL,
  product_id int DEFAULT NULL,
  quantity int DEFAULT NULL,
  price double DEFAULT NULL,
  status int DEFAULT NULL,
  created_at timestamp NULL DEFAULT NULL,
  created_by int DEFAULT NULL,
  updated_by int DEFAULT NULL,
  updated_at timestamp NULL DEFAULT NULL,
  is_deleted boolean DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


