# --- !Ups
-- --------------------------------------------------------
--
--
-- 表的结构 im_keywords
--

CREATE TABLE im_keywords (
  uid int(11) NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20),
  word VARCHAR(100),
  updated_at bigint(20) NOT NULL DEFAULT 0,
  created_at bigint(20) NOT NULL DEFAULT 0,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id)
);

insert into im_keywords (school_id, word) VALUES
 ('93740362', '不准'),
 ('93740362', '说'),
 ('93740362', '你老母');
# --- !Downs

DROP TABLE IF EXISTS im_keywords;