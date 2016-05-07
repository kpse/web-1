# --- !Ups
-- --------------------------------------------------------
--
--
-- 表的结构 im_token
--

CREATE TABLE im_internal_banned_users (
  uid int(11) NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20),
  class_id INT(11),
  user_id VARCHAR(100),
  updated_at bigint(20) NOT NULL DEFAULT 0,
  created_at bigint(20) NOT NULL DEFAULT 0,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id),
  KEY(school_id, class_id),
  KEY(school_id, user_id)
);

insert into im_internal_banned_users (school_id, class_id, user_id) VALUES
 ('93740362', 777888, 'p_93740362_Some(1)_13402815317'),
 ('93740362', 777888, 't_93740362_Some(1)_e0001'),
 ('93740362', 777888, 'p_93740362_Some(5)_13333333333');
# --- !Downs

DROP TABLE IF EXISTS im_internal_banned_users;