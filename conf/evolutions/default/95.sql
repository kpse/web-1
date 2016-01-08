# --- !Ups

CREATE TABLE school_weekly_statistics (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20),
  week_start   CHAR(8),
  child_count   INT(6) DEFAULT 0,
  parent_count   INT(6) DEFAULT 0,
  logged_once   INT(6),
  logged_ever   INT(6),
  created_at BIGINT,
  PRIMARY KEY (uid),
  KEY (school_id, week_start)
);

INSERT INTO school_weekly_statistics (school_id, week_start, parent_count, child_count, logged_once, logged_ever, created_at) VALUES
('93740362', '20160107', 123, 345, 100, 100, 1393395313123),
('93740362', '20160101', 123, 345, 100, 100, 1393395313123),
('93740362', '20151226', 1226, 2226, 100, 100, 1393395313123),
('93740362', '20151221', 1221, 2221, 100, 100, 1393395313123),
('93740362', '20140101', 123, 345, 100, 100, 1393395313123),
('93740362', '20140201', 123, 345, 90, 100, 1393395313123),
('93740362', '20140301', 123, 345, 91, 100, 1393395313123);

# --- !Downs

DROP TABLE IF EXISTS school_weekly_statistics;