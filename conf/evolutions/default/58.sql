# --- !Ups

-- case class GoodsOrigin(id: Option[Long], name: Option[String], short_name: Option[String], warehouse_id: Option[Long], memo: Option[String])

CREATE TABLE studentimmunerecord (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  immune_id INT(11),
  student_id INT(11),
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id, immune_id)
);

INSERT INTO studentimmunerecord (school_id, immune_id, student_id, updated_at) VALUES
('93740362', 1, 1, 1393395313123),
('93740362', 1, 2, 1393395313123);

# --- !Downs

DROP TABLE IF EXISTS studentimmunerecord;