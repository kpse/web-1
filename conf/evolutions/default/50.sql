# --- !Ups

CREATE TABLE workshiftdate (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  base_id INT(11),
  date VARCHAR(20),
  updated_at BIGINT,
  shift_status INT(4),
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id),
  KEY (base_id)
);

INSERT INTO workshiftdate (school_id, base_id, date, updated_at) VALUES
('93740362', 1, '5-18', 1393390313123),
('93740362', 1, '5-28', 1393390313123);

# --- !Downs

DROP TABLE IF EXISTS workshiftdate;