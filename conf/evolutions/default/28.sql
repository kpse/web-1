# --- !Ups

CREATE TABLE membershiprecords (
  uid       INT(11)     NOT NULL AUTO_INCREMENT,
  phone VARCHAR(20) NOT NULL,
  sms_enabled_at BIGINT      NOT NULL DEFAULT 0,
  received_at BIGINT      DEFAULT 0,
  PRIMARY KEY (uid),
  UNIQUE (phone)
);

INSERT INTO membershiprecords (phone) VALUES
  ('13993740361'),
  ('13993740362'),
  ('13999374033');


# --- !Downs

DROP TABLE IF EXISTS membershiprecords;