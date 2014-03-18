# --- !Ups
-- --------------------------------------------------------

--
-- 表的结构 chargeinfo
--

CREATE TABLE chargeinfo (
  uid        INT(11)          NOT NULL AUTO_INCREMENT,
  school_id    VARCHAR(20)  NOT NULL,
  total_phone_number     INT(11) NOT NULL default 0,
  expire_date DATE             NOT NULL DEFAULT '2200-01-01',
  status INT default 1,
  update_at BIGINT(20)             NOT NULL DEFAULT 0,
  PRIMARY KEY (uid)
);

INSERT INTO chargeinfo (school_id, total_phone_number, expire_date, update_at)
VALUES
('93740362', 10, '2014-03-01', 1323390313123),
('93740562', 3, '2014-03-01', 1323390313123);

# --- !Downs

DROP TABLE IF EXISTS chargeinfo;