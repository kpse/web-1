# --- !Ups

-- case class MorningHealthCheck(id: Option[Long], recorder_id: Option[Long], writer_id: Option[Long], checked_at: Option[Long], memo: Option[String])

CREATE TABLE morningcheckrecord (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  recorder_id INT(11),
  writer_id INT(11),
  memo TEXT,
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id)
);

INSERT INTO morningcheckrecord (school_id, recorder_id, writer_id, memo, updated_at) VALUES
('93740362', 99, 101, '备注', 1393395313123),
('93740362', 100, 102, '备注2', 1393395313123);

# --- !Downs

DROP TABLE IF EXISTS morningcheckrecord;