# --- !Ups

CREATE TABLE workshift (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  name   VARCHAR(20),
  start_time   VARCHAR(10),
  end_time   VARCHAR(10),
  same_day   INT(4),
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id)
);

INSERT INTO workshift (school_id, name, start_time, end_time, updated_at) VALUES
('93740362', '白班', '09:00',  '18:00',  1393390313123),
('93740362', '夜班', '09:10',  '18:10',  1393400313123),
('93740362', '国庆班', '09:30',  '18:30',  1393410313123);

# --- !Downs

DROP TABLE IF EXISTS workshift;