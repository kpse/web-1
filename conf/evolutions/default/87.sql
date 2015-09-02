# --- !Ups
-- --------------------------------------------------------
--
--
-- 表的结构 logstorage
--

CREATE TABLE logstorage (
  uid int(11) NOT NULL AUTO_INCREMENT,
  school_id varchar(20)   NOT NULL,
  url TEXT NOT NULL,
  logged_day bigint(20) NOT NULL DEFAULT 0,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id)
);

insert into logstorage (school_id, url, logged_day) values
('93740362', 'http://7d9m4e.com1.z0.glb.clouddn.com/fn2_prepaidplan_recharge.log', 1435767784000),
('93740362', 'http://7d9m4e.com1.z0.glb.clouddn.com/fn2_prepaidplan_recharge.log', 1435767784000),
('93740362', 'http://7d9m4e.com1.z0.glb.clouddn.com/fn2_prepaidplan_recharge.log', 1435767784000);

# --- !Downs

DROP TABLE IF EXISTS logstorage;