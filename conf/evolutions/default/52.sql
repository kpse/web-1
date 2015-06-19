# --- !Ups

-- case class Immune(id: Option[Long], name: Option[String], memo: Option[String])
--
-- case class ImmuneRecord(id: Option[Long], immune: Option[Immune], name: Option[String], description: Option[String], sub_id: Option[Long], sub_name: Option[String], memo: Option[String], created_at: Option[Long])

CREATE TABLE immunerecord (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  immune_name VARCHAR(20),
  immune_memo TEXT,
  name   VARCHAR(20),
  description TEXT,
  sub_id INT(11),
  sub_name VARCHAR(20),
  memo TEXT,
  created_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid)
);

INSERT INTO immunerecord (school_id, immune_name, immune_memo, name, description, sub_id, sub_name, memo, created_at) VALUES
('93740362', '乙肝', '打过没', '乙肝 二月二', '天气不错', 1, '一号针', '针形是什么', 1393395313123),
('93740362', '狂犬', '没打过', '狂犬第三次', '没有下雨', 2, '二号针', '针形是什么', 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS immunerecord;