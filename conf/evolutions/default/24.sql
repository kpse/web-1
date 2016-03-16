# --- !Ups

CREATE TABLE videomembers (
  uid       INT(11)     NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20) NOT NULL,
  parent_id VARCHAR(40) NOT NULL,
  account   CHAR(32)    NOT NULL,
  status    INT DEFAULT 1,
  update_at BIGINT      NOT NULL DEFAULT 0,
  expire_at BIGINT,
  memo TEXT,
  UNIQUE (account, parent_id),
  PRIMARY KEY (uid)
);

INSERT INTO videomembers (school_id, parent_id, account, expire_at) VALUES
  ('93740362', '2_93740362_123', '699A1A35755AF4940F2726AA291028E5', 1487649057933),
  ('93740362', '2_93740362_456', '799A1A35755AF4940F2726AA291028E5', 1497649057933);


# --- !Downs

DROP TABLE IF EXISTS videomembers;