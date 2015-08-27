# --- !Ups
-- --------------------------------------------------------

--
-- 表的结构 datadefinition
--

CREATE TABLE datadefinition (
  uid int(11) NOT NULL AUTO_INCREMENT,
  school_id varchar(20)   NOT NULL,
  name varchar(100) NOT NULL DEFAULT '',
  `type` varchar(100) NOT NULL DEFAULT '',
  created_at bigint(20) NOT NULL DEFAULT 0,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id),
  KEY(school_id, name)
);

insert into datadefinition (school_id, name, `type`, created_at) values
('93740362', 'name1', 'type1', 1435767784000),
('93740362', 'name2', 'type2', 1435767784000),
('93740362', 'name3', 'type3', 1435767784000);

# --- !Downs

DROP TABLE IF EXISTS datadefinition;