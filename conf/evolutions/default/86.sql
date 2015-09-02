# --- !Ups
-- --------------------------------------------------------
-- SchoolInternalFeedback(id: Option[Long], notification_id: Long, class_id: Int, created_at: Option[Long])
--
-- 表的结构 internalfeedback
--

CREATE TABLE internalfeedback (
  uid int(11) NOT NULL AUTO_INCREMENT,
  school_id varchar(20)   NOT NULL,
  notification_id int(11),
  class_id INT   NOT NULL DEFAULT 0,
  created_at bigint(20) NOT NULL DEFAULT 0,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id),
  KEY(school_id, notification_id),
  KEY(school_id, notification_id, class_id)
);

insert into internalfeedback (school_id, notification_id, class_id, created_at) values
('93740362', 1, 999666, 1435767784000),
('93740362', 2, 999777, 1435767784000),
('93740362', 3, 999888, 1435767784000);

# --- !Downs

DROP TABLE IF EXISTS internalfeedback;