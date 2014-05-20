# --- !Ups

CREATE TABLE sessionread (
  uid       INT(11)     NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20) NOT NULL,
  reader_id VARCHAR(40) NOT NULL,
  topic varchar(40) NOT NULL,
  last_read_session_id   INT(11)     NOT NULL,
  read_at  BIGINT        NOT NULL DEFAULT 0,
  PRIMARY KEY (uid)
);

# --- !Downs

DROP TABLE IF EXISTS sessionread;