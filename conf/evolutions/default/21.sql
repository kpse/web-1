# --- !Ups
-- --------------------------------------------------------

--
-- 表的结构 sessionlog
--

CREATE TABLE sessionlog (
  uid         INT(11)          NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  session_id varchar(40) NOT NULL,
  content   TEXT NOT NULL,
  media_url TEXT DEFAULT '',
  media_type VARCHAR(10) DEFAULT 'image',
  sender VARCHAR(40) NOT NULL DEFAULT '',
  sender_type CHAR(1) NOT NULL DEFAULT 't',
  update_at BIGINT(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (uid)
);

INSERT INTO sessionlog (school_id, session_id, content, media_url, sender, update_at, sender_type)
VALUES
('93740362', '1_93740362_9982', '老师你好，我们家王大侠怎么样。', 'http://suoqin-test.u.qiniudn.com/FgPmIcRG6BGocpV1B9QMCaaBQ9LK', '2_93740362_792', 112312313123, 'p'),
('93740362', '1_93740362_9982', '家长你好，你家王大侠最近没有来。', 'http://suoqin-test.u.qiniudn.com/Fget0Tx492DJofAy-ZeUg1SANJ4X', '3_93740362_1122', 212313313123, 't'),
('93740362', '1_93740362_9982', '娃他妈，怎么回事。', '', '2_93740362_790', 312314313123, 'p');


# --- !Downs

DROP TABLE IF EXISTS sessionlog;