DROP TABLE IF EXISTS tables;
CREATE TABLE tables (
  id int NOT NULL AUTO_INCREMENT,
  name varchar(255) DEFAULT NULL,
  status boolean DEFAULT NULL,
  created_at timestamp NULL DEFAULT NULL,
  created_by int DEFAULT NULL,
  updated_by int DEFAULT NULL,
  updated_at timestamp NULL DEFAULT NULL,
  is_deleted boolean DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;


