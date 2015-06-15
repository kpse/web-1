# --- !Ups

CREATE TABLE parentext (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  base_id   INT(11) NOT NULL,
  display_name VARCHAR(20),
  social_id VARCHAR(20),
  nationality VARCHAR(20),
  fixed_line VARCHAR(20),
  memo VARCHAR(512),
  PRIMARY KEY (uid),
  UNIQUE (base_id)
);

INSERT INTO parentext (base_id, display_name, social_id, nationality, fixed_line, memo) VALUES
(1, '老犁头', '510122196812310275', '中国', '028-88887777', '备注就是懒得很');



# --- !Downs

DROP TABLE IF EXISTS parentext;