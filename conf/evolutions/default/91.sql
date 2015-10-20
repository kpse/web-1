# --- !Ups
-- --------------------------------------------------------
--
--
-- 表的结构 videomemberdefault
--

CREATE TABLE videomemberdefault (
  uid int(11) NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20),
  account VARCHAR(40),
  password VARCHAR(20),
  created_at bigint(20) NOT NULL DEFAULT 0,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id)
);

insert into videomemberdefault (school_id, account, password) VALUES
 ('0', '8888', '8888');
# --- !Downs

DROP TABLE IF EXISTS videomemberdefault;