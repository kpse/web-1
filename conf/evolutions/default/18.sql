# --- !Ups
-- --------------------------------------------------------

--
-- 表的结构 assess
--

CREATE TABLE assess (
  uid         INT(11)          NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  child_id varchar(40) NOT NULL,
  emotion Int default 3,
  dining Int default 3,
  rest Int default 3,
  activity Int default 3,
  game Int default 3,
  exercise Int default 3,
  self_care Int default 3,
  manner Int default 3,
  comments   TEXT NOT NULL,
  publisher VARCHAR(20) NOT NULL DEFAULT '',
  publisher_id VARCHAR(40),
  update_at BIGINT(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (uid)
);

INSERT INTO assess (school_id, child_id, emotion, comments, publisher, update_at, publisher_id)
VALUES
('93740362', '1_93740362_374', 1, '我没说什么。', '马老师', 1333390313123, '3_93740362_1122'),
('93740362', '1_93740362_374', 2,  '总的来说，不认识', '朱老师', 1323390313123, '3_93740362_9977'),
('93740362', '1_93740362_456', 3, '我这周请假了', '杨老师', 12314313123, '3_93740362_9971');


# --- !Downs

DROP TABLE IF EXISTS assess;