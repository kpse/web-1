# --- !Ups
-- --------------------------------------------------------

--
-- 表的结构 agentinfo
--

CREATE TABLE agentinfo (
  uid         INT(11)          NOT NULL AUTO_INCREMENT,
  area        VARCHAR(20) NOT NULL,
  name        VARCHAR(20) NOT NULL,
  phone       VARCHAR(16) NOT NULL,
  logo_url      TEXT,
  login_password    varchar(32) NOT NULL,
  login_name    varchar(32) NOT NULL,
  status      INT          NOT NULL DEFAULT 1,
  expire_at   BIGINT(20) NOT NULL DEFAULT 0,
  updated_at   BIGINT(20) NOT NULL DEFAULT 0,
  created_at   BIGINT     DEFAULT 0,
  PRIMARY KEY (uid),
  UNIQUE KEY (login_name),
  UNIQUE KEY agent_phone (phone)
);

--
-- 转存表中的数据 agentinfo
--

INSERT INTO agentinfo (area, name, phone, logo_url, login_password, login_name, expire_at) VALUES
('陕西', '肉夹馍', '39258249821', 'http://7af4eg.com1.z0.glb.clouddn.com/FhVowrr61elcp_GRuYZYQI_gso0e', '5EBE2294ECD0E0F08EAB7690D2A6EE69', 'a0001', 1463379200000),
('北京', '天安门', '39258249822', 'http://7af4eg.com1.z0.glb.clouddn.com/FhVowrr61elcp_GRuYZYQI_gso0e', '5EBE2294ECD0E0F08EAB7690D2A6EE69', 'a0002', 1473379200000),
('台湾', '网星', '39258249823', 'http://7af4eg.com1.z0.glb.clouddn.com/FhVowrr61elcp_GRuYZYQI_gso0e', '5EBE2294ECD0E0F08EAB7690D2A6EE69', 'a0003', 1483379200000);

# --- !Downs

DROP TABLE IF EXISTS agentinfo;