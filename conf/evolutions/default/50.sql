# --- !Ups

CREATE TABLE workshiftdate (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  base_id INT(11),
  date VARCHAR(20),
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid)
);

INSERT INTO workshiftdate (school_id, base_id, date, updated_at) VALUES
('93740362', 1, '5-18', 1323390313123),
('93740362', 1, '5-28', 1323390313123);

# --- !Downs

DROP TABLE IF EXISTS workshiftdate;