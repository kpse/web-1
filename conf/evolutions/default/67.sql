# --- !Ups

-- UserPrivilege(id: Option[Long], user_id: Option[Long], privilege_id: Option[Long], memo: Option[String])

CREATE TABLE localuserprivilege (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  user_id   INT(11),
  privilege_id   INT(11),
  memo   TEXT,
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id)
);

INSERT INTO localuserprivilege (school_id, user_id, privilege_id, memo, updated_at) VALUES
('93740362', 1, 1, '高级权限', 1393395313123),
('93740362', 1, 2, '超级权限', 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS localuserprivilege;