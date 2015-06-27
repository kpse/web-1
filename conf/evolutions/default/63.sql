# --- !Ups

-- case class Warehouse(id: Option[Long], employees: Option[List[WarehouseKeeper]], name: Option[String], memo: Option[String])

CREATE TABLE warehouse (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  name   VARCHAR(50),
  memo   TEXT,
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id)
);

INSERT INTO warehouse (school_id, name, memo, updated_at) VALUES
('93740362', '加利福尼亚仓库', '里面没东西', 1393395313123),
('93740362', '阳光海岸仓库', '上周着火了', 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS warehouse;