# --- !Ups

-- case class AgentActivity(id: Option[Long], agent_id: Long, contractor_id: Long, title: String, address: Option[String], contact: String, time_span: Option[String],
--                    detail: Option[String], logo: Option[String], updated_at: Option[Long], publishing: Option[AdPublishing] = None)

CREATE TABLE agentactivityfeedback (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  agent_id   INT(11),
  activity_id   INT(11),
  school_id   VARCHAR(20),
  parent_id   VARCHAR(40),
  name   VARCHAR(50) NOT NULL,
  contact   VARCHAR(50) NOT NULL,
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (agent_id, activity_id),
  KEY (school_id),
  KEY (parent_id)
);

INSERT INTO agentactivityfeedback (agent_id, activity_id, school_id, parent_id, contact, name, updated_at) VALUES
(1, 1, '93740362', '2_93740362_123', '13333652147', '老张', 1393395313123),
(1, 1, '93740362', '2_93740362_456', '13333452148', '老王', 1393399313123),
(1, 1, '93740362', '2_93740362_789', '13333452149', '奥旭', 1393399313123),
(1, 1, '93740362', '2_93740362_790', '13333653110', '成功', 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS agentactivityfeedback;