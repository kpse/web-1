# --- !Ups

-- case class WarehouseKeeper(id: Option[Long], employee_id: Option[Long])

CREATE TABLE warehousekeeper (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  warehouse_id INT(11),
  employee_id INT(11),
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id, warehouse_id)
);

INSERT INTO warehousekeeper (school_id, warehouse_id, employee_id, updated_at) VALUES
('93740362', 1, 1, 1393395313123),
('93740362', 1, 2, 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS warehousekeeper;