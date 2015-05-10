# --- !Ups

CREATE TABLE bindinghistory (
  uid         INT(11)     NOT NULL AUTO_INCREMENT,
  phone   VARCHAR(20) NOT NULL,
  device_type VARCHAR(10) NOT NULL,
  access_token VARCHAR(40) NOT NULL,
  version_code VARCHAR(10) NOT NULL,
  updated_at BIGINT               DEFAULT 0,
  PRIMARY KEY (uid),
  INDEX (phone)
);

# --- !Downs

DROP TABLE IF EXISTS bindinghistory;