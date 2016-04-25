# --- !Ups

CREATE TABLE schoolconfig (
  uid       INT(11)     NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20) NOT NULL,
  name VARCHAR(40) NOT NULL default '',
  value TEXT,
  category VARCHAR(20) DEFAULT 'global',
  update_at BIGINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (uid),
  INDEX (school_id)
);

INSERT INTO schoolconfig (school_id, name, value, update_at) VALUES
  ('93740562', 'backend', 'false', 1401150055960),
  ('93740362', 'video_user_name', 'username', 1401150055960),
  ('93740362', 'video_user_password', 'password', 1401150055960);


# --- !Downs

DROP TABLE IF EXISTS schoolconfig;