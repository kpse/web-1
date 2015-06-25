# --- !Ups

-- case class FinancialProject(id: Option[Long], parent_id: Option[Long], name: Option[String], short_name: Option[String], project_id: Option[String], total: Option[String], memo: Option[String])

CREATE TABLE groupedfinancialproject (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  group_id INT(11),
  project_id INT(11),
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id, group_id)
);

INSERT INTO groupedfinancialproject (school_id, project_id, group_id, updated_at) VALUES
('93740362', 1, 1, 1393395313123),
('93740362', 2, 1, 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS groupedfinancialproject;