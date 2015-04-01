# --- !Ups

CREATE TABLE buslocation (
  uid       INT(11)     NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20) NOT NULL,
  employee_id VARCHAR(40) NOT NULL,
  latitude DOUBLE NOT NULL,
  longitude DOUBLE NOT NULL,
  direction FLOAT,
  radius FLOAT,
  address VARCHAR(255),
  received_at BIGINT      DEFAULT 0,
  PRIMARY KEY (uid),
  INDEX (school_id, employee_id)
);

INSERT INTO buslocation (uid, school_id, employee_id, latitude, longitude, direction, radius, address, received_at) VALUES
  (1, '93740362', '3_93740362_9977', 108.883425, 34.253351, 10.1, 0.1, '西安', 1401150055960),
  (2, '93740362', '3_93740362_9977', 108.883425, 34.253351, 90.3, 0.5, '中国', 1401150055960),
  (3, '93740362', '3_93740362_9977', 108.883425, 34.253351, 180.9, 0.9, '地球', 1401150055960);


# --- !Downs

DROP TABLE IF EXISTS buslocation;