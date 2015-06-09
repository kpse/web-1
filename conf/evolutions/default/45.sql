# --- !Ups
--- SysNO int identity(1,1),系统编号
--- GroupName nvarchar(20)分组名称


CREATE TABLE smsgroup (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  name   VARCHAR(20),
  created_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid)
);

INSERT INTO smsgroup (school_id, name, created_at) VALUES
('93740362', '老赵组', 1323390313123);

# --- !Downs

DROP TABLE IF EXISTS smsgroup;