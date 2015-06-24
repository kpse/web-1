# --- !Ups

-- case class FinancialReceipt(id: Option[Long], serial_number: Option[String], payment_type: Option[String],
-- sn_base: Option[Int], creator: Option[String], created_at: Option[Long], receipts: List[FinancialReceiptItem])

CREATE TABLE financialreceipt (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  serial_number VARCHAR(20),
  student_id INT(11),
  payment_type   INT(4),
  sn_base   INT(11),
  creator   VARCHAR(20),
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id)
);

INSERT INTO financialreceipt (school_id, serial_number, student_id, payment_type, sn_base, creator, updated_at) VALUES
('93740362', '1234321', 1, 1, 100, '创建人', 1393395313123),
('93740362', '1234322', 2, 2, 111, '创建人2', 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS financialreceipt;