# --- !Ups
-- --------------------------------------------------------
--
--
-- 表的结构 dietstructure
--

CREATE TABLE dietstructure (
  uid int(11) NOT NULL AUTO_INCREMENT,
  school_id varchar(20)   NOT NULL,
  menu_id int(11) NOT NULL,
  nutrition_id int(11) NOT NULL,
  weight VARCHAR(50),
  created_at bigint(20) NOT NULL DEFAULT 0,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id),
  KEY(school_id, menu_id)
);

insert into dietstructure (school_id, menu_id, nutrition_id, created_at) values
('93740362', 6001, 1, 1435767784000),
('93740362', 6001, 2, 1435767784000),
('93740362', 6001, 3, 1435767784000);

# --- !Downs

DROP TABLE IF EXISTS dietstructure;