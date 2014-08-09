# --- !Ups

CREATE TABLE videomembers (
  uid       INT(11)     NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20) NOT NULL,
  parent_id VARCHAR(40) NOT NULL,
  account   CHAR(32)    NOT NULL,
  status    INT DEFAULT 1,
  update_at BIGINT      NOT NULL DEFAULT 0,
  UNIQUE (account, parent_id),
  PRIMARY KEY (uid)
);

INSERT INTO videomembers (school_id, parent_id, account) VALUES
  ('93740362', '13', '132');


# --- !Downs

DROP TABLE IF EXISTS videomembers;