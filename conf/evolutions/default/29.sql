# --- !Ups

CREATE TABLE newstags (
  uid       INT(11)     NOT NULL AUTO_INCREMENT,
  news_id       INT(11)     NOT NULL,
  tag_id INT(11)     NOT NULL,
  status    INT DEFAULT 1,
  PRIMARY KEY (uid),
  UNIQUE (news_id, tag_id)
);

INSERT INTO newstags (news_id, tag_id) VALUES
  (1, 1),
  (1, 2),
  (6, 1),
  (7, 1);


# --- !Downs

DROP TABLE IF EXISTS newstags;