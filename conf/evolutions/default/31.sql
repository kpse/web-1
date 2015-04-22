# --- !Ups

CREATE TABLE buslocation (
  uid       INT(11)     NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20) NOT NULL,
  employee_id VARCHAR(40) NOT NULL,
  latitude DOUBLE NOT NULL,
  longitude DOUBLE NOT NULL,
  direction DOUBLE,
  radius DOUBLE,
  address VARCHAR(255),
  received_at BIGINT      DEFAULT 0,
  status    INT DEFAULT 1,
  PRIMARY KEY (uid),
  INDEX (school_id, employee_id)
);

INSERT INTO buslocation (uid, school_id, employee_id, latitude, longitude, direction, radius, address, received_at) VALUES
  (1, '93740362', '3_93740362_9977', 30.739469, 104.179257, 10.1, 0.1, '西安', 1483232461001),
  (2, '93740362', '3_93740362_1022', 30.239469, 104.179257, 90.3, 0.5, '中国', 1483232461002),
  (3, '93740362', '3_93740362_9977', 30.739469, 104.179257, 180.9, 0.9, '地球', 1483232461003);


# --- !Downs

DROP TABLE IF EXISTS buslocation;