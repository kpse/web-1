# --- !Ups

-- case class ArrangementGroup(id: Option[Long], name: Option[String], menus: Option[List[DietArrangement]])

CREATE TABLE dietarrangementgroup (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  name   VARCHAR(50),
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id)
);

INSERT INTO dietarrangementgroup (school_id, name, updated_at) VALUES
('93740362', '一般组', 1393395313123),
('93740362', '高级组', 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS dietarrangementgroup;