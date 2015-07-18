# --- !Ups

-- case class AgentAd(id: Option[Long], agent_id: Long, school_id: Long, title: String, address: Option[String],
--                    contact: String, time_span: Option[String], detail: Option[String], logo: Option[String])

CREATE TABLE agentadinschool (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  agent_id   INT(11) NOT NULL,
  school_id   VARCHAR(20) NOT NULL,
  ad_id   INT(11),
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  UNIQUE (agent_id, school_id, ad_id),
  KEY (agent_id),
  KEY (school_id)
);

INSERT INTO agentadinschool (agent_id, school_id, ad_id, updated_at) VALUES
(1, '93740362', 1, 1393395313123),
(1, '93740362', 2, 1393395313123),
(1, '93740562', 1, 1393395313123),
(1, '93740562', 2, 1393395313123);

# --- !Downs

DROP TABLE IF EXISTS agentadinschool;