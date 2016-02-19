# --- !Ups

CREATE TABLE school_weekly_statistics (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20),
  week_start   CHAR(8),
  week_end   CHAR(8),
  child_count   INT(6) DEFAULT 0,
  parent_count   INT(6) DEFAULT 0,
  logged_once   INT(6),
  logged_ever   INT(6),
  created_at BIGINT,
  PRIMARY KEY (uid),
  KEY (school_id, week_start)
);

INSERT INTO school_weekly_statistics (school_id, week_start, week_end, parent_count, child_count, logged_once, logged_ever, created_at) VALUES
('93740362', '20160208', '20160214', 1393, 685, 326, 452, 1393395313123),
('93740362', '20160201', '20160207', 1393, 685, 300, 400, 1393395313123),
('93740362', '20160101', '20160106', 123, 345, 100, 100, 1393395313123),
('93740362', '20151226', '20160101', 1226, 2226, 100, 100, 1393395313123),
('93740362', '20151221', '20151217', 1221, 2221, 100, 100, 1393395313123),
('93740362', '20140101', '20140107', 123, 345, 100, 100, 1393395313123),
('93740362', '20140201', '20160107', 123, 345, 90, 100, 1393395313123),
('93740362', '20140301', '20160107', 123, 345, 91, 100, 1393395313123);


-- delete from school_weekly_statistics;
# --- !Downs

DROP TABLE IF EXISTS school_weekly_statistics;