# --- !Ups

CREATE TABLE schoolbus (
  uid         INT(11)     NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  employee_id VARCHAR(40) NOT NULL,
  name VARCHAR(20),
  morning_path    TEXT,
  evening_path    TEXT,
  morning_start    VARCHAR(20),
  morning_end    VARCHAR(20),
  evening_start    VARCHAR(20),
  evening_end    VARCHAR(20),
  updated_at BIGINT               DEFAULT 0,
  status    INT DEFAULT 1,
  PRIMARY KEY (uid),
  UNIQUE (school_id, employee_id),
);

INSERT INTO schoolbus (school_id, employee_id, name, morning_path, evening_path, morning_start, morning_end, evening_start, evening_end, updated_at, status) VALUES
  ('93740362', '3_93740362_1022', '国内班车', '北京-上海-成都-广州', '广州-武汉-石家庄-北京', '8:00', '9:00', '15:40', '16:20', 1427817610000, 1),
  ('93740362', '3_93740362_9977', '国际班车', '北京-纽约-伦敦-上海', '上海-洛杉矶-里约热内卢-北京', '8:00', '9:00', '15:40', '16:20', 1427817610000, 1);


# --- !Downs

DROP TABLE IF EXISTS schoolbus;