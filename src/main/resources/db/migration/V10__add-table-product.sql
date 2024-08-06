DROP TABLE IF EXISTS product;
CREATE TABLE product (
  id int NOT NULL AUTO_INCREMENT,
  name varchar(255) DEFAULT NULL,
  price double DEFAULT NULL,
  category_id int DEFAULT NULL,
  status boolean DEFAULT NULL,
  created_at timestamp NULL DEFAULT NULL,
  created_by int DEFAULT NULL,
  updated_by int DEFAULT NULL,
  updated_at timestamp NULL DEFAULT NULL,
  is_deleted boolean DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


