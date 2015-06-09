# --- !Ups
--- SysNO int identity(1,1),系统编号
--- GroupSysNO int,分组编号
--- UserSysNO int,人员编号
--- UserType int,人员类别
--- MobilePhone varchar(20),手机号


CREATE TABLE smsgroupmember (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  group_id   INT(11) NOT NULL,
  name  VARCHAR(20),
  phone VARCHAR(20),
  PRIMARY KEY (uid),
  INDEX (group_id)
);

INSERT INTO smsgroupmember (school_id, group_id, name, phone) VALUES
('93740362', 1, '老赵', '13408654680');

# --- !Downs

DROP TABLE IF EXISTS smsgroupmember;