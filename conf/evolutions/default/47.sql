# --- !Ups

CREATE TABLE hardware (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  name   VARCHAR(20),
  sn   VARCHAR(50),
  ip   VARCHAR(20),
  port   INT,
  machine_type   INT,
  channel_1_camera   INT(11),
  channel_2_camera   INT(11),
  memo   VARCHAR(100),
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid)
);

INSERT INTO hardware (school_id, name, sn, ip, port, machine_type, updated_at) VALUES
('93740362', '门口刷卡机', '123', '255.255.255.255', 80, 1, 1323390313123),
('93740362', '摄像头1', '123', '255.255.255.255', 80, 2, 1323390313123),
('93740362', '摄像头2', '123', '255.255.255.255', 80, 2, 1323390313123),
('93740362', '摄像头2', '123', '255.255.255.255', 80, 2, 1323390313123);

# --- !Downs

DROP TABLE IF EXISTS hardware;