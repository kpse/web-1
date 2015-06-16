# --- !Ups

CREATE TABLE workerinshift (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  base_id INT(11),
  user_id   INT(11),
  user_type   INT,
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid)
);

INSERT INTO workerinshift (school_id, base_id, user_id, user_type, updated_at) VALUES
('93740362', 1, 1, 1, 1323390313123),
('93740362', 1, 1, 2, 1323390313123);

# --- !Downs

DROP TABLE IF EXISTS workerinshift;