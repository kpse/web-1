# --- !Ups
--- create table BaseInfo.dbo.CardCollection(
--- SysNO int identity(1,1),系统编号
--- CardNumber varchar(500),加密卡号（加盐后生成的md5）
--- OriginNumber varchar(100)原10位卡号
--- )


CREATE TABLE checkingrecord (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  base_id   INT(11) NOT NULL,
  create_user INT(11) NOT NULL,
  memo TEXT,
  error_status INT DEFAULT 1,
  PRIMARY KEY (uid)
);

INSERT INTO checkingrecord (school_id, base_id, create_user, memo) VALUES
('93740362', 1, 1, '某些卡是对的');

INSERT INTO checkingrecord (school_id, base_id, create_user, memo, error_status) VALUES
('93740362', 2, 1, '某些卡是错的', 0);

# --- !Downs

DROP TABLE IF EXISTS checkingrecord;