# --- !Ups

-- case class Inventory(id: Option[Long], goods_id: Option[Long], goods_name: Option[String], warehouse_id: Option[Long],
-- created_at: Option[Long], updated_at: Option[Long], quality: Option[Int], unit: Option[String])

CREATE TABLE warehouseinventory (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  warehouse_id   INT(11),
  goods_id    INT(11),
  goods_name  VARCHAR(20),
  unit  VARCHAR(20),
  quality  INT,
  updated_at BIGINT,
  created_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id, warehouse_id)
);

INSERT INTO warehouseinventory (school_id, warehouse_id, goods_id, goods_name, unit, quality, updated_at, created_at) VALUES
('93740362', 1, 1, '铅笔', '支', 100, 1393395313123, 1393395313123),
('93740362', 1, 2, '钢笔', '支', 100, 1393399313123, 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS warehouseinventory;