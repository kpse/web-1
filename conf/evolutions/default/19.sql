# --- !Ups
-- --------------------------------------------------------

--
-- 表的结构 privilege
--

CREATE TABLE privilege (
  uid         INT(11)          NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  employee_id VARCHAR(40) NOT NULL,
  `group` VARCHAR(10) NOT NULL,
  subordinate VARCHAR(128) NOT NULL,
  promoter VARCHAR(40) NOT NULL,
  status      TINYINT          NOT NULL DEFAULT 1,
  update_at BIGINT(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (uid)
);

INSERT INTO privilege (school_id, employee_id, `group`, subordinate, promoter, update_at)
VALUES
('0', '3_93740362_9972', 'operator','', '', 1333390313123),
('93740362', '3_93740362_9971', 'principal', '', '', 1323390313123),
('93740362', '3_93740362_3344', 'teacher', '777888', '3_93740362_9971', 12314313123);


# --- !Downs

DROP TABLE IF EXISTS privilege;