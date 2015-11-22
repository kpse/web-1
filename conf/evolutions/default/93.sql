# --- !Ups
-- --------------------------------------------------------
--
--
-- 表的结构 im_class_group
--

CREATE TABLE im_class_group (
  uid int(11) NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20),
  class_id INT(11),
  group_id VARCHAR(50),
  group_name VARCHAR(100),
  updated_at bigint(20) NOT NULL DEFAULT 0,
  created_at bigint(20) NOT NULL DEFAULT 0,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id),
  KEY(school_id, class_id)
);

insert into im_class_group (school_id, class_id, group_id, group_name) VALUES
 ('93740362', 777888, '93740362_777888', '苹果班');
# --- !Downs

DROP TABLE IF EXISTS im_class_group;