# --- !Ups
-- --------------------------------------------------------

--
-- 表的结构 classinfo
--

CREATE TABLE classinfo (
  uid        INT(11)          NOT NULL AUTO_INCREMENT,
  school_id    VARCHAR(20)  NOT NULL,
  class_id    INT(11)  NOT NULL,
  class_name     VARCHAR(40) NOT NULL,
  status       INT       NOT NULL DEFAULT 1,
  update_at BIGINT(20)             NOT NULL DEFAULT 0,
  PRIMARY KEY (uid)
);

--
-- 转存表中的数据 classinfo
--

INSERT INTO classinfo (school_id, class_id, class_name) VALUES
  ('93740362', 777888, '苹果班'),
  ('93740362', 777999, '香蕉班'),
  ('93740362', 777666, '梨儿班'),
  ('93740362', 777667, '无人班'),
  ('93741662', 777667, '苹果班'),
  ('93741662', 777668, '梨儿班'),
  ('93741662', 777669, '黄桃班'),
  ('93741662', 777670, '西瓜班'),
  ('93740562', 11, '怪兽班'),
  ('93740562', 22, '奇迹班');

INSERT INTO classinfo (school_id, class_id, class_name, status) VALUES
  ('93740362', 777889, '已删除班', 0);
# --- !Downs

DROP TABLE IF EXISTS classinfo;