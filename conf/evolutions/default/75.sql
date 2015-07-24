# --- !Ups

-- case class AgentContractorInSchool(id: Option[Long], agent_id: Long,
-- contractor_id: Long, school_id: Long, updated_at: Option[Long])

CREATE TABLE agentcontractorinschool (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  agent_id   INT(11) NOT NULL,
  school_id   VARCHAR(20) NOT NULL,
  contractor_id   INT(11),
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  UNIQUE (agent_id, school_id, contractor_id),
  KEY (agent_id),
  KEY (school_id)
);

INSERT INTO agentcontractorinschool (agent_id, school_id, contractor_id, updated_at) VALUES
(1, '93740362', 1, 1393395313123),
(1, '93740362', 2, 1393395313123),
(1, '93740362', 6, 1393395313123),
(1, '93740362', 7, 1393395313123),
(1, '93740362', 8, 1393395313123),
(1, '93740362', 9, 1393395313123),
(1, '93740362', 10, 1393395313123),
(1, '93740362', 11, 1393395313123),
(1, '93740562', 1, 1393395313123),
(1, '93740562', 2, 1393395313123);

# --- !Downs

DROP TABLE IF EXISTS agentcontractorinschool;