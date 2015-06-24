# --- !Ups

-- case class FinancialProjectGroup(id: Option[Long], name: Option[String], short_name: Option[String], group_id: Option[String], projects: Option[List[FinancialProject]])

CREATE TABLE financialprojectgroup (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  name   VARCHAR(50),
  short_name   VARCHAR(20),
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id)
);

INSERT INTO financialprojectgroup (school_id, name, short_name, updated_at) VALUES
('93740362', '自言自语组', '一个组', 1393395313123),
('93740362', '诶么么组', '另一个组', 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS financialprojectgroup;