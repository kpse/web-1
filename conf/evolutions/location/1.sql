# --- !Ups

CREATE TABLE records (
  uid                 INT(11)     NOT NULL AUTO_INCREMENT,
  device_id           CHAR(10)    NOT NULL,
  cmd                 VARCHAR(10) NOT NULL,
  `time`              CHAR(6)     NOT NULL,
  valid               CHAR(1)     NOT NULL,
  latitude            VARCHAR(10) NOT NULL,
  latitude_direction  CHAR(1)     NOT NULL,
  longitude           VARCHAR(10) NOT NULL,
  longitude_direction CHAR(1)     NOT NULL,
  speed               VARCHAR(6)  NOT NULL,
  direction           VARCHAR(3),
  `date`              CHAR(6),
  tracker_status      CHAR(4),
  PRIMARY KEY (uid)
);

INSERT INTO records (device_id, cmd, `time`, valid, latitude,
                     latitude_direction, longitude, longitude_direction,
                     speed, direction, `date`, tracker_status)
VALUES 
  ('1451351909','V1','143716','V','3415.0645','N','10852.3265','E','0.0','000','251114','FFFF'),
  ('1451351909','V1','143715','V','3415.0645','N','10852.3265','E','0.0','000','251114','FFFF'),
  ('1451351909','V1','143714','V','3415.0645','N','10852.3265','E','0.0','000','251114','FFFF'),
  ('1451351909','V1','143713','V','3415.0645','N','10852.3265','E','0.0','000','251114','FFFF'),
  ('1451351909','V1','143712','V','3415.0645','N','10852.3265','E','0.0','000','251114','FFFF'),
  ('1451351909','V1','143712','V','3415.0645','N','10852.3265','E','0.0','000','251114','FFFF'),
  ('1451351909','V1','143711','V','3415.0645','N','10852.3265','E','0.0','000','251114','FFFF'),
  ('1451351909','V1','143710','V','3415.0645','N','10852.3265','E','0.0','000','251114','FFFF'),
  ('1451351909','V1','143709','V','3415.0645','N','10852.3265','E','0.0','000','251114','FFFF'),
  ('1451351909','V1','143709','V','3415.0645','N','10852.3265','E','0.0','000','251114','FFFF'),
  ('1451351909','V1','143708','V','3415.0645','N','10852.3265','E','0.0','000','251114','FFFF');

# --- !Downs

DROP TABLE IF EXISTS records;