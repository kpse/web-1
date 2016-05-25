# --- !Ups
--- SysNO int identity(1,1),系统编号
--- Name nvarchar(20),访客姓名
--- CertificateType nvarchar(10),证件类型
--- CertificateNumber nvarchar(30),证件编号
--- VisitReason nvarchar(30),访问理由
--- VisitTime datetime,访问时间
--- VisitQTY int,访客人数
--- VisitUserSysNO int,被访问者系统编号
--- VisitUserType int,被访问者类型
--- Memo nvarchar(500),备注
--- PicPath varchar(max),访客照片
--- SGIDPicture varchar(max)身份证照片


CREATE TABLE visitor (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  name   VARCHAR(20),
  certificate_type VARCHAR(10),
  certificate_number VARCHAR(30),
  reason VARCHAR(30),
  phone VARCHAR(20),
  visited_at BIGINT DEFAULT 0,
  quantity INT,
  visitor_user_id   INT(11),
  visitor_user_type INT,
  memo TEXT,
  photo_record TEXT,
  sgid_picture TEXT,
  status INT DEFAULT 1,
  PRIMARY KEY (uid)
);

INSERT INTO visitor (school_id, name, certificate_number, certificate_type, reason, visited_at, quantity, visitor_user_id, visitor_user_type, memo, photo_record, sgid_picture) VALUES
('93740362', '老王', '510122198012342145', '身份证', '要债', 1323390313123, 17, 1, 0, '出来混迟早要还的', 'url1', 'url2');

# --- !Downs

DROP TABLE IF EXISTS visitor;