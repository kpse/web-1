# --- !Ups

CREATE TABLE nbr_records (
  uid       INT(11)      NOT NULL AUTO_INCREMENT,
  device_id CHAR(10)  NOT NULL,
  `time`     CHAR(6) NOT NULL,
  mcc VARCHAR(3)  NOT NULL,
  mnc VARCHAR(3)  NOT NULL,
  ta VARCHAR(10) NOT NULL,
  num CHAR(1)  NOT NULL,
  lac VARCHAR(5) NOT NULL,
  cid VARCHAR(5) NOT NULL,
  rxlev VARCHAR(5) NOT NULL,
  `date` CHAR(6),
  tracker_status CHAR(10),
  PRIMARY KEY (uid)
);

# --- !Downs

DROP TABLE IF EXISTS nbr_records;