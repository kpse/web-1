# --- !Ups

-- case class AgentAd(id: Option[Long], agent_id: Long, school_id: Long, title: String, address: Option[String],
--                    contact: String, time_span: Option[String], detail: Option[String], logo: Option[String])

CREATE TABLE agentcontractor (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  agent_id   INT(11),
  title   VARCHAR(50) NOT NULL,
  category INT,
  address   VARCHAR(250),
  contact   VARCHAR(50) NOT NULL,
  time_span   VARCHAR(100),
  detail   TEXT,
  logo   TEXT,
  latitude   decimal,
  longitude   decimal,
  geo_address   TEXT,
  priority   INT(4) NOT NULL DEFAULT 0,
  publish_status   INT(4) DEFAULT 0,
  published_at BIGINT,
  reject_reason TEXT,
  updated_at BIGINT,
  created_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (agent_id),
  KEY (category)
);

INSERT INTO agentcontractor (agent_id, title, address, contact, time_span, detail, logo, updated_at, publish_status, published_at, reject_reason, category) VALUES
(1, '雅芳', '天津', '13333652147', '2015-07-02~2015-08-03', '去广告中心，领免费机票', '', 1393395313123, 2, 1393395313123, null, 0),
(1, '本田', '上海', '13333452147', '2015-07-12~2015-08-03', '汽车随便开', '', 1393399313123, 99, 0, null, 1),
(1, '悲剧', '上海', '13333452147', '2015-12-02~2016-12-03', '要悲剧', '', 1393399313123, 3, 0, '名字太长', 2),
(1, 'T', '四川', '13333653147', null, '特斯拉要不要', '', 1393399313123, 0, 0, null, 4),
(1, 'Coca cola', '四川', '13333653147', null, '喝可乐挑战赛', '', 1393399313123, 0, 0, null, 8),
(1, '摄影在线', '四川', '13333653147', null, '喝可乐挑战赛', '', 1393399313123, 4, 1393395313123, null, 1),
(1, '培训在线', '四川', '13333653147', null, '喝可乐挑战赛', '', 1393399313123, 4, 1393395313123, null, 2),
(1, '游乐在线', '四川', '13333653147', null, '喝可乐挑战赛', '', 1393399313123, 4, 1393395313123, null, 4),
(1, '购物在线', '四川', '13333653147', null, '喝可乐挑战赛', '', 1393399313123, 4, 1393395313123, null, 8),
(1, '其他在线', '四川', '13333653147', null, '喝可乐挑战赛', '', 1393399313123, 4, 1393395313123, null, 0),
(1, '去年下线', '四川', '13333653147', null, '喝可乐挑战赛', '', 1393399313123, 5, 1393395313123, null, 8);

update agentcontractor set latitude=123.231, longitude=321.123 where uid in (1, 3);

# --- !Downs

DROP TABLE IF EXISTS agentcontractor;