# --- !Ups
-- --------------------------------------------------------

--
-- 表的结构 assignment
--

CREATE TABLE assignment (
  uid         INT(11)          NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  class_id int(11) NOT NULL,
  title   VARCHAR(20) NOT NULL DEFAULT '',
  content   TEXT NOT NULL,
  image TEXT DEFAULT '',
  publisher VARCHAR(20) NOT NULL DEFAULT '',
  update_at BIGINT(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (uid)
);

INSERT INTO assignment (school_id, class_id, title, content, image, publisher, update_at)
VALUES
('93740362', 777888, '老师你好。', '今天的作业是写生。', 'http://suoqin-test.u.qiniudn.com/FgPmIcRG6BGocpV1B9QMCaaBQ9LK', '马老师', 12312313123),
('93740362', 777888, '家长你好。', '今天的作业是吃饭。',  'http://suoqin-test.u.qiniudn.com/Fget0Tx492DJofAy-ZeUg1SANJ4X', '朱老师', 12313313123),
('93740362', 777666, '老师再见。', '今天请带一只小兔子回家。', 'http://suoqin-test.u.qiniudn.com/FgPmIcRG6BGocpV1B9QMCaaBQ9LK', '杨老师', 12314313123);


# --- !Downs

DROP TABLE IF EXISTS assignment;