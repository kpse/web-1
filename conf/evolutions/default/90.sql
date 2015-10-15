# --- !Ups
-- --------------------------------------------------------
--
--
-- 表的结构 paymenthistory
--

CREATE TABLE paymenthistory (
  uid int(11) NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20),
  parent_id VARCHAR(40),
  phone VARCHAR(20),
  transaction_id VARCHAR(50) NOT NULL,
  transaction_type VARCHAR(20) NOT NULL,
  channel_type VARCHAR(20) NOT NULL,
  transaction_fee int NOT NULL,
  buyer_email VARCHAR(256),
  content TEXT,
  created_at bigint(20) NOT NULL DEFAULT 0,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id),
  KEY(school_id, parent_id),
  KEY(school_id, phone),
  UNIQUE (transaction_id)
);

# --- !Downs

DROP TABLE IF EXISTS paymenthistory;