# --- !Ups

CREATE TABLE childrenbusplan (
  uid         INT(11)     NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  employee_id VARCHAR(40) NOT NULL,
  child_id    VARCHAR(40) NOT NULL,
  updated_at BIGINT               DEFAULT 0,
  status    INT DEFAULT 1,
  PRIMARY KEY (uid),
  UNIQUE (school_id, child_id),
  INDEX (school_id, employee_id)
);

INSERT INTO childrenbusplan (school_id, employee_id, child_id, updated_at, status) VALUES
  ('93740362', '3_93740362_1022', '1_1391836223533', 1427817610000, 1),
  ('93740362', '3_93740362_9977', '1_93740362_456', 1427817610000, 1),
  ('93740362', '3_93740362_9977', '1_93740362_9982', 1427817610000, 1);


# --- !Downs

DROP TABLE IF EXISTS childrenbusplan;