# --- !Ups

CREATE TABLE newsread (
  uid       INT(11)     NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20) NOT NULL,
  parent_id VARCHAR(40) NOT NULL,
  news_id   INT(11)     NOT NULL,
  readTime  BIGINT        NOT NULL DEFAULT 0,
  PRIMARY KEY (uid)
);

INSERT INTO newsread (school_id, parent_id, news_id) VALUES
('93740362', '2_93740362_123', 6),
('93740362', '2_93740362_456', 6),
('93740362', '2_93740362_792', 6);

# --- !Downs

DROP TABLE IF EXISTS newsread;