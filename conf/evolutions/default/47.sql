# --- !Ups

CREATE TABLE hardware (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  name   VARCHAR(20),
  sn   VARCHAR(50),
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid)
);

INSERT INTO hardware (school_id, name, sn, updated_at) VALUES
('93740362', '门口刷卡机', '123',  1323390313123);

# --- !Downs

DROP TABLE IF EXISTS hardware;