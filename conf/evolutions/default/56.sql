# --- !Ups

-- case class FinancialReceiptItem(id: Option[Long], project_id: Option[String], sum_value: Option[String], reason: Option[String], count: Option[Int], created_at: Option[Long])

CREATE TABLE financialreceiptitem (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  receipt_id INT(11),
  project_id INT(11),
  sum_value VARCHAR(20),
  reason   TEXT,
  count   INT,
  updated_at BIGINT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY (school_id, project_id),
  KEY (school_id, receipt_id)
);

INSERT INTO financialreceiptitem (school_id, receipt_id, project_id, sum_value, reason, count, updated_at) VALUES
('93740362', 1, 1, '111', '没啥理由', 1, 1393395313123),
('93740362', 1, 1, '222', '有很多理由', 1, 1393399313123);

# --- !Downs

DROP TABLE IF EXISTS financialreceiptitem;