# --- !Ups

-- Menu(id: Option[Long], name: Option[String], weight: Option[String], arrange_type: Option[Int])

CREATE TABLE dietmenu (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  name   VARCHAR(50),
  weight   VARCHAR(10),
  arrange_type INT,
  updated_at BIGINT,
  store_type INT(4) NOT NULL DEFAULT 1,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id)
);

INSERT INTO dietmenu (uid, school_id, name, weight, arrange_type, updated_at) VALUES
(6001, '93740362', '减肥菜单', '100g', 1, 1393395313123),
(6002, '93740362', '海鲜', '200g', 2, 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS dietmenu;