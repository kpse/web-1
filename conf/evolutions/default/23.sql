# --- !Ups

CREATE TABLE macwhitelist (
  uid       INT(11)     NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20) NOT NULL,
  mac CHAR(20) NOT NULL,
  encoded_mac CHAR(32) NOT NULL,
  status INT default 1,
  update_at  BIGINT        NOT NULL DEFAULT 0,
  UNIQUE (mac, encoded_mac),
  PRIMARY KEY (uid)
);

# --- !Downs

DROP TABLE IF EXISTS macwhitelist;