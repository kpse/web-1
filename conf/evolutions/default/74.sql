# --- !Ups

-- case class AgentAd(id: Option[Long], agent_id: Long, school_id: Long, title: String, address: Option[String],
--                    contact: String, time_span: Option[String], detail: Option[String], logo: Option[String])

CREATE TABLE agentadvertisement (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  agent_id   INT(11),
  title   VARCHAR(50) NOT NULL,
  address   VARCHAR(250),
  contact   VARCHAR(50) NOT NULL,
  time_span   VARCHAR(100),
  detail   TEXT,
  logo   TEXT,
  publish_status   INT(4) DEFAULT 0,
  published_at BIGINT,
  updated_at BIGINT,
  created_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (agent_id)
);

INSERT INTO agentadvertisement (agent_id, title, address, contact, time_span, detail, logo, updated_at) VALUES
(1, '雅芳', '天津', '13333652147', '7月2日-8月3日', '去广告中心，领免费机票', '', 1393395313123),
(1, '本田', '上海', '13333452147', '12月2日-12月3日', '汽车随便开', '', 1393399313123),
(1, 'T', '四川', '13333653147', null, '特斯拉要不要', '', 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS agentadvertisement;