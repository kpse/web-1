# --- !Ups

-- case class Goods(id: Option[Long], name: Option[String], short_name: Option[String], unit: Option[String], max_warning: Option[String],
--                  min_warning: Option[String], warehouse_id: Option[Long], stock_place: Option[String], memo: Option[String])

CREATE TABLE goods (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  warehouse_id   INT(11),
  name    VARCHAR(50),
  short_name  VARCHAR(20),
  unit  VARCHAR(20),
  max_warning  VARCHAR(20),
  min_warning  VARCHAR(20),
  stock_place  VARCHAR(50),
  memo   TEXT,
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id, warehouse_id)
);

INSERT INTO goods (school_id, warehouse_id, name, short_name, unit, max_warning, min_warning, stock_place, memo, updated_at) VALUES
('93740362', 1, '铅笔', '小', '支', '100', '20', '2楼',  '高级备注', 1393395313123),
('93740362', 1, '钢笔', '大', '支', '100', '20', '3楼', '备注', 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS goods;