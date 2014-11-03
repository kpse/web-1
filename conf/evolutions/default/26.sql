# --- !Ups

CREATE TABLE advertisement (
  uid       INT(11)     NOT NULL AUTO_INCREMENT,
  school_id VARCHAR(20) NOT NULL,
  position_id INT(11) default 0,
  link text NOT NULL,
  image text NOT NULL,
  name VARCHAR(20) NOT NULL default '',
  status    INT DEFAULT 1,
  update_at BIGINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (uid)
);

INSERT INTO advertisement (school_id, position_id, link, image, name, update_at) VALUES
  ('93740362', 1, 'https://www.cocobabys.com/', 'https://www.cocobabys.com/assets/images/hero-1.jpg', '库乐宝', 1401140055960),
  ('93740362', 2, 'https://stage2.cocobabys.com/', 'https://www.cocobabys.com/assets/images/hero-2.jpg', '库乐宝', 1401150055960);


# --- !Downs

DROP TABLE IF EXISTS advertisement;