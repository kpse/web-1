# --- !Ups
-- --------------------------------------------------------
-- (id: Option[Long], class_id: Int, title: String, content: String, created_at: Option[Long])
--
-- 表的结构 internalnotification
--

CREATE TABLE internalnotification (
  uid int(11) NOT NULL AUTO_INCREMENT,
  school_id varchar(20)   NOT NULL,
  class_id INT   NOT NULL DEFAULT 0,
  title varchar(100) NOT NULL DEFAULT '',
  content TEXT NOT NULL DEFAULT '',
  updated_at bigint(20) NOT NULL DEFAULT 0,
  created_at bigint(20) NOT NULL DEFAULT 0,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id),
  KEY(school_id, class_id)
);

insert into internalnotification (school_id, class_id, title, content, created_at) values
('93740362', 999666, 'title1', 'content1', 1435767784000),
('93740362', 999777, 'title2', 'content2', 1435767784000),
('93740362', 999888, 'title3', 'content3', 1435767784000);

# --- !Downs

DROP TABLE IF EXISTS internalnotification;