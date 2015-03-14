# --- !Ups
-- --------------------------------------------------------

--
-- 表的结构 feedback
--

CREATE TABLE feedback (
  uid         INT(11)          NOT NULL AUTO_INCREMENT,
  phone       VARCHAR(16) NOT NULL,
  content     TEXT,
  comment     TEXT,
  image      VARCHAR(512) DEFAULT '',
  status     TINYINT          NOT NULL DEFAULT 1,
  source     varchar(20)          NOT NULL DEFAULT 'android_parent',
  update_at   BIGINT(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (uid)
);

# --- !Downs

DROP TABLE IF EXISTS feedback;