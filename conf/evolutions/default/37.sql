# --- !Ups
---case class StudentExt(display_name: Option[String], former_name: Option[String], student_id: Option[String], social_id: Option[String], residence_place: Option[String],
---                      residence_type: Option[String], nationality: Option[String], original_place: Option[String], ethnos: Option[String],
---                      student_type: Option[Int], inDate: Option[String], interest: Option[String], bed_number: Option[String], memo: Option[String],
---                      bus_status: Option[Int], medical_history: Option[String])

CREATE TABLE studentext (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  base_id   INT(11) NOT NULL,
  display_name VARCHAR(20),
  former_name VARCHAR(20),
  student_id VARCHAR(50),
  social_id VARCHAR(20),
  residence_place VARCHAR(50),
  residence_type VARCHAR(10),
  nationality VARCHAR(20),
  original_place VARCHAR(20),
  ethnos VARCHAR(20),
  student_type INT,
  in_date BIGINT,
  interest VARCHAR(512),
  bed_number VARCHAR(20),
  memo TEXT,
  bus_status INT,
  medical_history VARCHAR(2048),
  PRIMARY KEY (uid),
  UNIQUE (base_id)
);

INSERT INTO studentext (base_id, display_name, former_name, student_id, social_id, residence_place, residence_type,
nationality, original_place, ethnos, student_type, in_date, interest, bed_number, memo, bus_status, medical_history) VALUES
(1, '李毅', '小李', '223112', '510122198812310275', '四川成都', '城镇户口', '中国', '杭州西湖', '汉族', 0, 1387649057933, '吃喝睡', '1-2-3', '备注就是懒得很', 0, '青霉素过敏');



# --- !Downs

DROP TABLE IF EXISTS studentext;