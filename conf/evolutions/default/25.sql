# --- !Ups

CREATE TABLE videoprovidertoken (
  uid       INT(11)     NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20) NOT NULL,
  token CHAR(32) NOT NULL,
  status    INT DEFAULT 1,
  update_at BIGINT      NOT NULL DEFAULT 0,
  UNIQUE (school_id, token),
  PRIMARY KEY (uid)
);

INSERT INTO videoprovidertoken (school_id, token) VALUES
  ('93740362', '3FDE6BB0541387E4EBDADF7C2FF31123');


# --- !Downs

DROP TABLE IF EXISTS videoprovidertoken;