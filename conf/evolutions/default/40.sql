# --- !Ups
--- create table BaseInfo.dbo.CardCollection(
--- SysNO int identity(1,1),系统编号
--- CardNumber varchar(500),加密卡号（加盐后生成的md5）
--- OriginNumber varchar(100)原10位卡号
--- )


CREATE TABLE cardrecord (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  number VARCHAR(50),
  origin VARCHAR(10),
  status INT DEFAULT 1,
  PRIMARY KEY (uid),
  UNIQUE (origin)
);

INSERT INTO cardrecord (school_id, origin, number) VALUES
('93740362', '0002221114', '510122195812310275'),
('93740362', '9991234567', '510122195812310275'),
('93740362', '0001234999', '510122195812310275'),
('93740362', '0001234567', '510122195812310275'),
('93740362', '0001234568', '510122195812310275'),
('93740362', '0001234569', '510122195812310275'),
('93740362', '2323211233', '510122195812310275'),
('93740362', '0001112221', '510122195812310275'),
('93740362', '0002234582', '510122195812310275'),
('93740362', '0001234570', '510122195812310275');

# --- !Downs

DROP TABLE IF EXISTS cardrecord;