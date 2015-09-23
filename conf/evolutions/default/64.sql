# --- !Ups

-- Menu(id: Option[Long], name: Option[String], weight: Option[String], arrange_type: Option[Int])

CREATE TABLE dietmenu (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  food_type_id   INT(11),
  former_id   INT(11),
  name   VARCHAR(50),
  weight   VARCHAR(10),
  recipe   TEXT,
  property   VARCHAR(100),
  tips   VARCHAR(100),
  arrange_type INT,
  updated_at BIGINT,
  store_type INT(4) NOT NULL DEFAULT 1,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id),
  KEY (school_id, food_type_id),
  KEY (school_id, former_id)
);

INSERT INTO dietmenu (uid, school_id, name, weight, arrange_type, recipe, property, tips, updated_at, food_type_id, former_id) VALUES
(6001, '93740362', '减肥菜单', '100g', 1, 'recipe1', 'property1', 'tips', 1393395313123, 1, 2),
(6002, '93740362', '海鲜', '200g', 2, 'recipe2', 'property2', 'tips2', 1393399313123, 3, 4);

# --- !Downs

DROP TABLE IF EXISTS dietmenu;