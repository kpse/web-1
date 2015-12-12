# --- !Ups

-- case class StockingDetail(id: Option[Long], stocking_id: Option[Long], goods_id: Option[Long], goods_name: Option[String],
--                           origin_id: Option[Long], origin_name: Option[String], price: Option[String], quality: Option[Int], unit: Option[String], memo: Option[String], subtotal: Option[String])

CREATE TABLE warehousestockingdetail (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  warehouse_id   INT(11),
  stocking_id    INT(11),
  goods_id  INT(11),
  goods_name  VARCHAR(50),
  employee_id  INT(11),
  employee_name  VARCHAR(50),
  origin_id INT(11),
  origin_name VARCHAR(50),
  price VARCHAR(50),
  unit VARCHAR(50),
  subtotal VARCHAR(50),
  quality INT,
  memo TEXT,
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id, warehouse_id, stocking_id)
);

INSERT INTO warehousestockingdetail (school_id, warehouse_id, stocking_id, origin_id, origin_name, goods_id, goods_name, price, unit, quality, subtotal, updated_at) VALUES
('93740362', 1, 1, 1, '上海', 1, '蜡烛', '100', '只', 100, '￥100',  1393395313123),
('93740362', 1, 2, 2, '西安', 2, '书签', '100', '打', 20, '￥120', 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS warehousestockingdetail;