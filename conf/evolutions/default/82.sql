# --- !Ups
-- --------------------------------------------------------

--
-- 表的结构 medicinerecord
--

CREATE TABLE medicinerecord (
  uid int(11) NOT NULL AUTO_INCREMENT,
  school_id varchar(20)   NOT NULL,
  student_id int(11) NOT NULL,
  title varchar(50) NOT NULL DEFAULT '',
  content varchar(255) NOT NULL DEFAULT '',
  clocks TEXT,
  updated_at bigint(20) NOT NULL DEFAULT 0,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id),
  KEY(school_id, student_id)
);

insert into medicinerecord (school_id, student_id, title, content, updated_at) values
('93740362', 1, '吃了', 'content1', 1435767784000),
('93740362', 2, '没吃', 'content2', 1435767784000),
('93740362', 3, '不想吃', 'content3', 1435767784000),

# --- !Downs

DROP TABLE IF EXISTS medicinerecord;