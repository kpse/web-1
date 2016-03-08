# --- !Ups
-- --------------------------------------------------------
--
--
-- 表的结构 im_school_group
--

CREATE TABLE im_school_group (
  uid int(11) NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20),
  updated_at bigint(20) NOT NULL DEFAULT 0,
  created_at bigint(20) NOT NULL DEFAULT 0,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id)
);

insert into im_school_group (school_id, status) VALUES
 ('93740362', 1),
 ('93740562', 0);
# --- !Downs

DROP TABLE IF EXISTS im_school_group;