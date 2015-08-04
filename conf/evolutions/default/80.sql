# --- !Ups

CREATE TABLE employeecard (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  employee_id   INT(11),
  card   CHAR(10),
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  UNIQUE (employee_id),
  UNIQUE (card),
  KEY (school_id)
);

INSERT INTO employeecard (school_id, employee_id, card, updated_at) VALUES
('93740362', 1, '0001112221', 1393395313123),
('93740362', 2, '0001112222', 1393395313123),
('93740362', 3, '0001112223', 1393395313123),
('93740362', 4, '0001112224', 1393395313123),
('93740562', 5, '0001112225', 1393395313123),
('93740562', 6, '0001112226', 1393395313123);

# --- !Downs

DROP TABLE IF EXISTS employeecard;