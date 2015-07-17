# --- !Ups

-- case class AgentActivity(id: Option[Long], agent_id: Long, contractor_id: Long, title: String, address: Option[String], contact: String, time_span: Option[String],
--                    detail: Option[String], logo: Option[String], updated_at: Option[Long], publishing: Option[AdPublishing] = None)

CREATE TABLE agentactivity (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  agent_id   INT(11),
  contractor_id   INT(11),
  title   VARCHAR(50) NOT NULL,
  address   VARCHAR(250),
  contact   VARCHAR(50) NOT NULL,
  time_span   VARCHAR(100),
  detail   TEXT,
  logo   TEXT,
  publish_status   INT(4) DEFAULT 0,
  published_at BIGINT,
  reject_reason TEXT,
  updated_at BIGINT,
  created_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (agent_id, contractor_id)
);

INSERT INTO agentactivity (agent_id, contractor_id, title, address, contact, time_span, detail, logo, updated_at, publish_status, published_at, reject_reason) VALUES
(1, 1, '雅芳', '天津', '13333652147', '7月2日-8月3日', '去广告中心，领免费机票', '', 1393395313123, 2, 1393395313123, null),
(1, 1, '本田', '上海', '13333452147', '12月2日-12月3日', '汽车随便开', '', 1393399313123, 99, 0, null),
(1, 2, '悲剧', '上海', '13333452147', '12月2日-12月3日', '要悲剧', '', 1393399313123, 3, 0, '名字太长'),
(1, 2, 'T', '四川', '13333653147', null, '特斯拉要不要', '', 1393399313123, 0, 0, null);

# --- !Downs

DROP TABLE IF EXISTS agentactivity;