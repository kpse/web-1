# --- !Ups
-- --------------------------------------------------------

--
-- 表的结构 assess
--

CREATE TABLE assess (
  uid         INT(11)          NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  child_id varchar(40) NOT NULL,
  emotion TINYINT default 3,
  dining TINYINT default 3,
  rest TINYINT default 3,
  activity TINYINT default 3,
  game TINYINT default 3,
  exercise TINYINT default 3,
  self_care TINYINT default 3,
  manner TINYINT default 3,
  comments   TEXT NOT NULL,
  publisher VARCHAR(20) NOT NULL DEFAULT '',
  publish_at BIGINT(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (uid)
);

INSERT INTO assess (school_id, child_id, emotion, comments, publisher, publish_at)
VALUES
('93740362', '1_93740362_374', 1, '我没说什么。', '马老师', 12312313123),
('93740362', '1_93740362_374', 2,  '总的来说，不认识', '朱老师', 12390313123),
('93740362', '1_93740362_456', 3, '我这周请假了', '杨老师', 12314313123);


# --- !Downs

DROP TABLE IF EXISTS assess;