# --- !Ups

-- case class Stocking(id: Option[Long], `type`: Option[Int], invoice_type: Option[Int], invoice_name: Option[String], serial_number: Option[String], sn_base: Option[Long],
--                     creator: Option[String], updated_at: Option[Long], employee_id: Option[String], warehouse_id: Option[Long], memo: Option[String], origin_id: Option[Long], items: Option[List[StockingDetail]])

CREATE TABLE warehousestocking (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  warehouse_id   INT(11),
  type    INT,
  invoice_type  INT,
  invoice_name  VARCHAR(20),
  serial_number  VARCHAR(20),
  sn_base  INT,
  creator  VARCHAR(20),
  employee_id  INT(11),
  origin_id  INT(11),
  memo  TEXT,
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id, warehouse_id)
);

INSERT INTO warehousestocking (school_id, warehouse_id, type, invoice_type, invoice_name, serial_number, sn_base, creator, employee_id, origin_id, memo, updated_at) VALUES
('93740362', 1, 1, 1, '发票', '1231231', 3232, '', 1, 1, '备注1', 1393395313123),
('93740362', 1, 2, 3, '发票2', '1232322', 34234, 'B', 1, 1, '备注2', 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS warehousestocking;