# --- !Ups
-- --------------------------------------------------------

--
-- 表的结构 employeedailylog
--

CREATE TABLE employeedailylog (
  uid int(11) NOT NULL AUTO_INCREMENT,
  school_id varchar(20)   NOT NULL,
  employee_id int(11) NOT NULL,
  record_url text,
  card varchar(20)   NOT NULL,
  card_type int(11) DEFAULT '0',
  notice_type int(4) NOT NULL DEFAULT '0',
  checked_at bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (uid),
  KEY(checked_at),
  KEY(school_id),
  KEY(employee_id)
);

insert into employeedailylog (school_id, employee_id, card, checked_at) values
('93740362', 1, '0001234567', 1435767784000),
('93740362', 1, '0001234567', 1434685724002),
('93740362', 1, '0001234567', 1434267200003),
('93740362', 1, '0001234567', 1434353600004),
('93740362', 1, '0001234567', 1434440000005),
('93740362', 1, '0001234567', 1434526400006),
('93740362', 1, '0001234567', 1434612800007),
('93740362', 1, '0001234567', 1434699200008),
('93740362', 1, '0001234567', 1434785600009),
('93740362', 1, '0001234567', 1434866500001),
('93740362', 1, '0001234567', 1435767784000),

# --- !Downs

DROP TABLE IF EXISTS employeedailylog;