# --- !Ups

CREATE TABLE sharedpages (
  uid         INT(11)     NOT NULL AUTO_INCREMENT,
  token   VARCHAR(40) NOT NULL,
  original_id INT(11) NOT NULL,
  comment text,
  created_at BIGINT               DEFAULT 0,
  status    INT DEFAULT 1,
  PRIMARY KEY (uid),
  UNIQUE (token)
);

# --- !Downs

DROP TABLE IF EXISTS sharedpages;