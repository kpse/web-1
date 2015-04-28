# --- !Ups

CREATE TABLE sessionread (
  uid       INT(11)     NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20) NOT NULL,
  reader_id VARCHAR(40) NOT NULL,
  topic varchar(40) NOT NULL,
  last_read_session_id   INT(11)     NOT NULL,
  read_at  BIGINT        NOT NULL DEFAULT 0,
  PRIMARY KEY (uid)
);


INSERT INTO sessionread (school_id, reader_id, topic, last_read_session_id, read_at) VALUES
('93740362', '3_93740362_1122', '1_93740362_9982', 5, 1401150055997),
('93740362', '3_93740362_1122', 'h_1_93740362_9982', 2, 1401150055997);

# --- !Downs

DROP TABLE IF EXISTS sessionread;