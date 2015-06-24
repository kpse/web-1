# --- !Ups

-- case class FinancialProject(id: Option[Long], parent_id: Option[Long], name: Option[String], short_name: Option[String], project_id: Option[String], total: Option[String], memo: Option[String])

CREATE TABLE financialproject (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  project_id VARCHAR(20),
  group_id INT(11),
  parent_id INT(11),
  name   VARCHAR(50),
  short_name   VARCHAR(20),
  total VARCHAR(20),
  memo TEXT,
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id, group_id)
);

INSERT INTO financialproject (school_id, parent_id, name, short_name, project_id, group_id, total, memo, updated_at) VALUES
('93740362', 1, '打过没打过没打过没', '短打', 'aaa_1', 1, '99232', '这是memo', 1393395313123),
('93740362', 2, '没打过没打过没打过', '长打', 'aaa_2', 1, '99333', '这是memo', 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS financialproject;