# --- !Ups

CREATE TABLE tags (
  uid       INT(11)     NOT NULL AUTO_INCREMENT,
  name VARCHAR(40)    NOT NULL,
  `desc` VARCHAR(255)    NOT NULL DEFAULT '',
  status    INT DEFAULT 1,
  PRIMARY KEY (uid),
  UNIQUE (name)
);

INSERT INTO tags (uid, name) VALUES
  (1, '作业'),
  (2, '活动'),
  (3, '备忘');


# --- !Downs

DROP TABLE IF EXISTS tags;