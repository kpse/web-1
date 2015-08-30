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

insert into feedback (phone, content, comment) VALUES
('123456789321', '一个安卓反馈', '不想处理');

insert into feedback (phone, content, comment, source) VALUES
('123456789321', '一个安卓教师反馈', '不想处理1', 'android_teacher');

insert into feedback (phone, content, comment, source) VALUES
('123456789321', '一个iOS反馈', '不想处理2', 'ios_parent');

insert into feedback (phone, content, comment, source) VALUES
('123456789321', '一个iOS老师反馈', '不想处理3', 'ios_teacher');

insert into feedback (phone, content, comment, source) VALUES
('123456789321', '一个网页反馈', '不想处理4', 'web');

insert into feedback (phone, content, comment, source) VALUES
('123456789321', '处理完毕', 'done', 'done');

# --- !Downs

DROP TABLE IF EXISTS feedback;