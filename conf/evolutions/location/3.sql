# --- !Ups

CREATE TABLE link_records (
  uid       INT(11)      NOT NULL AUTO_INCREMENT,
  device_id CHAR(10)  NOT NULL,
  `time`     CHAR(6) NOT NULL,
  gsm VARCHAR(10)  NOT NULL,
  gps VARCHAR(10)  NOT NULL,
  bat VARCHAR(10)  NOT NULL,
  step VARCHAR(10)  NOT NULL,
  turnover VARCHAR(10)  NOT NULL,
  ex1 VARCHAR(10)  NOT NULL,
  ex2 VARCHAR(10)  NOT NULL,
  `date` CHAR(6),
  tracker_status CHAR(10),
  PRIMARY KEY (uid)
);

INSERT INTO link_records (device_id, `time`, gsm, gps,
                     bat, step, turnover,
                     ex1, ex2, `date`, tracker_status)
VALUES
  ('1451351909', '143716', '60', '88', '97', '0', '0', '3','60','111214','FFFFFFFF');

# --- !Downs

DROP TABLE IF EXISTS link_records;