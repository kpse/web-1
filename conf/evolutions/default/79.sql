# --- !Ups

-- case class AgentActivityInSchool(id: Option[Long], agent_id: Long, activity_id: Long,
-- school_id: Long, updated_at: Option[Long])

CREATE TABLE agentactivityinschool (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  agent_id   INT(11) NOT NULL,
  school_id   VARCHAR(20) NOT NULL,
  activity_id   INT(11),
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  UNIQUE (agent_id, school_id, activity_id),
  KEY (agent_id),
  KEY (school_id)
);

INSERT INTO agentactivityinschool (agent_id, school_id, activity_id, updated_at) VALUES
(1, '93740362', 1, 1393395313123),
(1, '93740362', 2, 1393395313123),
(1, '93740362', 5, 1393395313123),
(1, '93740362', 6, 1393395313123),
(1, '93740562', 1, 1393395313123),
(1, '93740562', 2, 1393395313123);

# --- !Downs

DROP TABLE IF EXISTS agentactivityinschool;