# --- !Ups
-- --------------------------------------------------------

--
-- 表的结构 dailylog
--

CREATE TABLE dailylog (
  uid         INT(11)          NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  child_id varchar(40) NOT NULL,
  parent_name VARCHAR(20) NOT NULL,
  record_url TEXT DEFAULT '',
  card_no varchar(20) NOT NULL,
  card_type INT DEFAULT 0,
  notice_type INT DEFAULT 0,
  check_at   BIGINT(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (uid)
);

insert into dailylog (school_id, child_id, parent_name, card_no, check_at) values
('93740362', '1_1391836223533', '袋鼠', '0001234567', 1424448000001),
('93740362', '1_1391836223533', '袋鼠', '0001234567', 1405180800002),
('93740362', '1_1391836223533', '袋鼠', '0001234567', 1405267200003),
('93740362', '1_1391836223533', '袋鼠', '0001234567', 1405353600004),
('93740362', '1_1391836223533', '袋鼠', '0001234567', 1405440000005),
('93740362', '1_1391836223533', '袋鼠', '0001234567', 1405526400006),
('93740362', '1_1391836223533', '袋鼠', '0001234567', 1405612800007),
('93740362', '1_1391836223533', '袋鼠', '0001234567', 1405699200008),
('93740362', '1_1391836223533', '袋鼠', '0001234567', 1405785600009),
('93740362', '1_1391836223533', '袋鼠', '0001234567', 1405866500001),
('93740362', '1_1391836223533', '袋鼠', '0001234567', 1405981000002),
('93740362', '1_93740362_456', '林玄', '0001234568', 1424448000001),
('93740362', '1_93740362_456', '林玄', '0001234568', 1405180800001),
('93740362', '1_93740362_456', '林玄', '0001234568', 1405267200001),
('93740362', '1_93740362_456', '林玄', '0001234568', 1405353600001),
('93740362', '1_93740362_456', '林玄', '0001234568', 1405440000001),
('93740362', '1_93740362_456', '林玄', '0001234568', 1405526400001),
('93740362', '1_93740362_456', '林玄', '0001234568', 1405612800001),
('93740362', '1_93740362_456', '林玄', '0001234568', 1405699200001),
('93740362', '1_93740362_456', '林玄', '0001234568', 1405781000001),
('93740362', '1_93740362_9982', '大象', '2323211233', 1424563200000),
('93740362', '1_93740362_9982', '大象', '2323211233', 1405184900002),
('93740362', '1_93740362_9982', '大象', '2323211233', 1405298500002),
('93740362', '1_93740362_9982', '大象', '2323211233', 1405312100002),
('93740362', '1_93740362_9982', '大象', '2323211233', 1405425700002),

('93740362', '1_93740362_778', '大象', '0001234570', 1424448000001),
('93740362', '1_93740362_778', '大象', '0001234570', 1405180800003),
('93740362', '1_93740362_778', '大象', '0001234570', 1405267200003),
('93740362', '1_93740362_778', '大象', '0001234570', 1405353600003),
('93740362', '1_93740362_778', '大象', '0001234570', 1405440000003);

# --- !Downs

DROP TABLE IF EXISTS dailylog;