# --- !Ups

-- case class StudentHealthCheck(id: Option[Long], record_id: Option[Long], student_id: Option[Long], temperature: Option[String],
--                               check_status_id: Option[Long], check_status_name: Option[String], relative_words: Option[String],
--                               memo: Option[String])

CREATE TABLE studentmorningcheckrecord (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  record_id INT(11),
  student_id INT(11),
  temperature VARCHAR(20),
  check_status_id INT,
  check_status_name VARCHAR(20),
  relative_words TEXT,
  memo TEXT,
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id, record_id)
);

INSERT INTO studentmorningcheckrecord (school_id, record_id, student_id, temperature, check_status_id, check_status_name, relative_words, memo, updated_at) VALUES
('93740362', 1, 1, '37', 1, '正常', '没事4', '备注1', 1393395313123),
('93740362', 1, 2, '37', 1, '正常', '没事3', '备注2', 1393395313123);

# --- !Downs

DROP TABLE IF EXISTS studentmorningcheckrecord;