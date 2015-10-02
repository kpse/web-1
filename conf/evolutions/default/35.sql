# --- !Ups

CREATE TABLE bindinghistory (
  uid         INT(11)     NOT NULL AUTO_INCREMENT,
  phone   VARCHAR(20) NOT NULL,
  device_type VARCHAR(10) NOT NULL,
  access_token VARCHAR(40) NOT NULL,
  version_code VARCHAR(10) NOT NULL,
  updated_at BIGINT               DEFAULT 0,
  PRIMARY KEY (uid),
  INDEX (phone),
  INDEX (phone, updated_at)
);

INSERT INTO bindinghistory (phone, device_type, access_token, version_code, updated_at) VALUES
('13402815317', 'ios', '0', '99', 1441036610001),
('13408654680', 'ios', '0', '99', 1441036610001),
('13402815317', 'ios', '0', '99', 1441036810001);

# --- !Downs

DROP TABLE IF EXISTS bindinghistory;