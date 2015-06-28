# --- !Ups

-- case class DietArrangement(id: Option[Long], menu_id: Option[Long], menu_name: Option[String],
--                            arrange_type: Option[Int], master_id: Option[Long], grade_id: Option[Long], weight: Option[String], updated_at: Option[Long])

CREATE TABLE dietarrangement (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  group_id   INT(11),
  menu_id   INT(11),
  menu_name   VARCHAR(50),
  weight   VARCHAR(10),
  arrange_type INT,
  master_id INT(11),
  grade_id INT(11),
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id, group_id)
);

INSERT INTO dietarrangement (school_id, menu_id, menu_name, group_id, weight, arrange_type, master_id, grade_id, updated_at) VALUES
('93740362', 1, '一顿饭', 1, '100g', 1, 1, 1, 1393395313123),
('93740362', 1, '另一顿饭', 0, '400g', 2, 2, 2, 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS dietarrangement;