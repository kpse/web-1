# --- !Ups
-- --------------------------------------------------------

--
-- 表的结构 camerarecord
--

CREATE TABLE camerarecord (
  uid int(11) NOT NULL AUTO_INCREMENT,
  school_id varchar(20)   NOT NULL,
  hardware_id int(11) NOT NULL,
  account varchar(100) NOT NULL DEFAULT '',
  password varchar(100) NOT NULL DEFAULT '',
  updated_at bigint(20) NOT NULL DEFAULT '0',
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id),
  KEY(school_id, hardware_id)
);

insert into camerarecord (school_id, hardware_id, account, password, updated_at) values
('93740362', 2, 'account1', 'password1', 1435767784000),
('93740362', 3, 'account2', 'password2', 1435767784000),
('93740362', 4, 'account2', 'password3', 1435767784000),

# --- !Downs

DROP TABLE IF EXISTS camerarecord;