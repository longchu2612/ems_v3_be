DROP TABLE IF EXISTS category;
CREATE TABLE category (
  id int NOT NULL AUTO_INCREMENT,
  category_name varchar(45) DEFAULT NULL,
  status boolean DEFAULT NULL,
  created_at timestamp NULL DEFAULT NULL,
  created_by int DEFAULT NULL,
  updated_by int DEFAULT NULL,
  updated_at timestamp NULL DEFAULT NULL,
  is_deleted boolean DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


