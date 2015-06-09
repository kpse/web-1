# --- !Ups
--- SysNO int identity(1,1),系统编号
--- MobilePhoneList varchar(max),手机号集合
--- MessageContent nvarchar(max),短信内容
--- CreateTime datetime创建时间


CREATE TABLE smsrecord (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  phone_list   MEDIUMTEXT,
  content TEXT,
  status INT(4) DEFAULT 1,
  created_at BIGINT,
  PRIMARY KEY (uid)
);

INSERT INTO smsrecord (school_id, phone_list, content, created_at) VALUES
('93740362', '13402815317,14880498549,13408654680', '出来混迟早要还的', 1323390313123);

# --- !Downs

DROP TABLE IF EXISTS smsrecord;