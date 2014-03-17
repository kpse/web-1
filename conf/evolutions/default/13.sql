# --- !Ups
-- --------------------------------------------------------

--
-- 表的结构 employeeinfo
--

CREATE TABLE employeeinfo (
  uid         INT(11)          NOT NULL AUTO_INCREMENT,
  name        VARCHAR(20) NOT NULL,
  employee_id VARCHAR(40) NOT NULL,
  phone       VARCHAR(16) NOT NULL,
  gender      INT       NOT NULL DEFAULT 2,
  workgroup   VARCHAR(40) NOT NULL DEFAULT '',
  workduty    VARCHAR(20) NOT NULL DEFAULT '',
  picurl      VARCHAR(128) DEFAULT '',
  birthday    DATE             NOT NULL DEFAULT '1800-01-01',
  school_id   VARCHAR(20) NOT NULL,
  login_password    varchar(32) NOT NULL,
  login_name    varchar(32) NOT NULL,
  status      TINYINT          NOT NULL DEFAULT 1,
  update_at   BIGINT(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (uid),
  UNIQUE KEY (login_name),
  UNIQUE KEY phone (phone)
);

--
-- 转存表中的数据 employeeinfo
--

INSERT INTO employeeinfo (uid, name, employee_id, phone, gender, workgroup, workduty, picurl, birthday, school_id, login_password, login_name)
VALUES
    (1, '王豫', '3_93740362_1122', '13258249821', 0, '教师组', '教师', '', '1986-06-04', '93740362', '5EBE2294ECD0E0F08EAB7690D2A6EE69', 'e0001'),
  (2, '何忍', '3_93740362_3344', '13708089040', 0, '教师组', '教师', '', '1987-06-04', '93740362', '3FDE6BB0541387E4EBDADF7C2FF31123', 'e0002'),
  (3, '富贵', '3_93740362_9977', '13060003702', 1, '保安组', '员工', '', '1982-06-04', '93740362', '3FDE6BB0541387E4EBDADF7C2FF31123', 'e0003'),
  (5, '蜘蛛侠', '3_93740362_9971', '13060003722', 1, '保安组', '员工', '', '1982-06-04', '93740362', '5EBE2294ECD0E0F08EAB7690D2A6EE69', 'admin'),
  (6, '超人', '3_93740362_9972', '13060003723', 1, '保安组', '员工', '', '1982-06-04', '0', 'CDF3C60380FE6094BBF7F979243B280B', 'operator'),
  (8, '老宋', '0_99999_9998', '13060003724', 1, '保安组', '员工', '', '1982-06-04', '0', 'CDF3C60380FE6094BBF7F979243B280B', 'dataadmin'),
  (7, '西安老师', '3_93740362_9978', '13060003703', 1, '保安组', '员工', '', '1982-06-04', '93740562', '3FDE6BB0541387E4EBDADF7C2FF31123', 'e0004');

# --- !Downs

DROP TABLE IF EXISTS employeeinfo;