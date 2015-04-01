# --- !Ups

CREATE TABLE childrenonbus (
  uid       INT(11)     NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20) NOT NULL,
  employee_id VARCHAR(40) NOT NULL,
  card VARCHAR(20)  NOT NULL DEFAULT '',
  child_id VARCHAR(40) NOT NULL,
  received_at BIGINT      DEFAULT 0,
  PRIMARY KEY (uid),
  INDEX (school_id, card)
);

INSERT INTO childrenonbus (school_id, employee_id, card, child_id, received_at) VALUES
  ('93740362', '1_1391836223533', '0001234567', '2_93740362_123',1401150055960),
  ('93740362', '1_1391836223533', '0001234568', '2_93740362_456',1401150055960),
  ('93740362', '1_1391836223533', '0001234569', '2_93740362_789',1401150055960);


# --- !Downs

DROP TABLE IF EXISTS childrenonbus;