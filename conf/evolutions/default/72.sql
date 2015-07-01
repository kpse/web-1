# --- !Ups
-- --------------------------------------------------------

--
-- 表的结构 agentinfo
--

CREATE TABLE agentinfo (
  uid         INT(11)          NOT NULL AUTO_INCREMENT,
  name        VARCHAR(20) NOT NULL,
  phone       VARCHAR(16) NOT NULL,
  logo_url      TEXT,
  login_password    varchar(32) NOT NULL,
  login_name    varchar(32) NOT NULL,
  status      INT          NOT NULL DEFAULT 1,
  updated_at   BIGINT(20) NOT NULL DEFAULT 0,
  created_at   BIGINT     DEFAULT 0,
  PRIMARY KEY (uid),
  UNIQUE KEY (login_name),
  UNIQUE KEY agent_phone (phone)
);

--
-- 转存表中的数据 agentinfo
--

INSERT INTO agentinfo (name, phone, logo_url, login_password, login_name) VALUES
('陕西总代理', '39258249821', 'http://7af4eg.com1.z0.glb.clouddn.com/FhVowrr61elcp_GRuYZYQI_gso0e', '5EBE2294ECD0E0F08EAB7690D2A6EE69', 'a0001');

# --- !Downs

DROP TABLE IF EXISTS agentinfo;