# --- !Ups

CREATE TABLE pfopresult (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  code   INT(4),
  process_key   VARCHAR(100) NOT NULL,
  input_bucket VARCHAR(100),
  input_key VARCHAR(255),
  desc VARCHAR(255),
  output_key VARCHAR(255),
  pipeline VARCHAR(100),
  reqid VARCHAR(50),
  updated_at BIGINT,
  PRIMARY KEY (uid),
  UNIQUE (process_key)
);

INSERT INTO pfopresult (code, process_key, input_bucket, input_key, desc, output_key, pipeline, reqid, updated_at) VALUES
(0, '123', 'bucket', 'file', 'The fop was completed successfully', 'file', '0.default', 'ukcAALyx_hlBiOgT', 1393390313123),
(0, '1234', 'bucket2', 'file', 'The fop was completed successfully', 'file', '0.default', 'ukcCALyx_hlBiOgT', 1393390313123);

# --- !Downs

DROP TABLE IF EXISTS pfopresult;