# --- !Ups

-- case class GoodsOrigin(id: Option[Long], name: Option[String], short_name: Option[String], warehouse_id: Option[Long], memo: Option[String])

CREATE TABLE goodsorigin (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  warehouse_id INT(11),
  name VARCHAR(50),
  short_name VARCHAR(20),
  memo   TEXT,
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id, warehouse_id)
);

INSERT INTO goodsorigin (school_id, warehouse_id, name, short_name, memo, updated_at) VALUES
('93740362', 1, '美国', 'A', '没啥理由', 1393395313123),
('93740362', 1, '日本', 'J', '有很多理由', 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS goodsorigin;