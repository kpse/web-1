# --- !Ups
--- (id: Option[Long], record_name: Option[String], recorded_at: Option[Long], student_id: Option[Long], basic_height: Option[String],
--- basic_weight: Option[String], head: Option[HeadCheckResult], surgery: Option[SurgeryCheckResult], medicine: Option[MedicineCheckResult],
--- other: Option[OtherCheckResult], memo: Option[String])

--- (head_eye_left: Option[String], head_eye_right: Option[String], head_eye_trachoma: Option[String],
---                            head_eye_conjunctivitis: Option[String], head_ear_leftear: Option[String], head_ear_rightear: Option[String], head_tooth_qty: Option[String],
---                            head_tooth_caries: Option[String], head_tooth_periodontal: Option[String])
---
--- (surgery_headcircumference: Option[String], surgery_chestcircumference: Option[String],
---                               surgery_limbs: Option[String], surgery_spine: Option[String], surgery_skin: Option[String], surgery_lymphaden: Option[String])
---
--- (medicine_lefttonsil: Option[String], medicine_righttonsil: Option[String], medicine_bloodpressure: Option[String], medicine_bloodtypesysno: Option[String],
---                                medicine_bloodtypename: Option[String], medicine_hemoglobin: Option[String], medicine_heart: Option[String], medicine_liver: Option[String],
---                                medicine_spleen: Option[String], medicine_lung: Option[String])
---
--- (other_rickets_chongmen: Option[String], other_rickets_fangtou: Option[String],
---                             other_rickets_pingpongtou: Option[String], other_rickets_zhentu: Option[String], other_rickets_jixiong: Option[String], other_rickets_lechuanzhu: Option[String],
---                             other_rickets_haoshigou: Option[String], other_rickets_yijingduohan: Option[String], other_rickets_xo: Option[String], other_infantileparalysis: Option[String],
---                             other_ascarid: Option[String], other_skins: Option[String], other_deformity: Option[String], other_psychosis: Option[String],
---                             other_hernia: Option[String], other_others: Option[String])

CREATE TABLE healthcheckdetail (
  uid         INT(11) NOT NULL AUTO_INCREMENT,
  school_id   VARCHAR(20) NOT NULL,
  record_name   VARCHAR(20),
  recorded_at BIGINT,
  student_id INT(11),
  basic_height VARCHAR(10),
  basic_weight VARCHAR(10),
  head_eye_left VARCHAR(10),
  head_eye_right VARCHAR(10),
  head_eye_trachoma VARCHAR(10),
  head_eye_conjunctivitis VARCHAR(10),
  head_ear_leftear VARCHAR(10),
  head_ear_rightear VARCHAR(10),
  head_tooth_qty VARCHAR(10),
  head_tooth_caries VARCHAR(10),
  head_tooth_periodontal VARCHAR(10),
  surgery_headcircumference VARCHAR(10),
  surgery_chestcircumference VARCHAR(10),
  surgery_limbs VARCHAR(10),
  surgery_spine VARCHAR(10),
  surgery_skin VARCHAR(10),
  surgery_lymphaden VARCHAR(10),
  medicine_lefttonsil VARCHAR(10),
  medicine_righttonsil VARCHAR(10),
  medicine_bloodpressure VARCHAR(10),
  medicine_bloodtype VARCHAR(10),
  medicine_bloodtypename VARCHAR(10),
  medicine_hemoglobin VARCHAR(10),
  medicine_heart VARCHAR(10),
  medicine_liver VARCHAR(10),
  medicine_spleen VARCHAR(10),
  medicine_lung VARCHAR(10),
  other_rickets_chongmen VARCHAR(10),
  other_rickets_fangtou VARCHAR(10),
  other_rickets_pingpongtou VARCHAR(10),
  other_rickets_zhentu VARCHAR(10),
  other_rickets_jixiong VARCHAR(10),
  other_rickets_lechuanzhu VARCHAR(10),
  other_rickets_haoshigou VARCHAR(10),
  other_rickets_yijingduohan VARCHAR(10),
  other_rickets_xo VARCHAR(10),
  other_infantileparalysis VARCHAR(10),
  other_ascarid VARCHAR(10),
  other_skins VARCHAR(10),
  other_deformity VARCHAR(10),
  other_psychosis VARCHAR(10),
  other_hernia VARCHAR(10),
  other_others VARCHAR(10),
  memo TEXT,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid)
);

INSERT INTO healthcheckdetail (school_id, record_name, recorded_at, student_id, basic_height, basic_weight, head_eye_left, head_eye_right, head_eye_trachoma, head_eye_conjunctivitis,
head_ear_leftear, head_ear_rightear, head_tooth_caries, head_tooth_qty, head_tooth_periodontal, surgery_headcircumference, surgery_chestcircumference,
surgery_limbs, surgery_spine, surgery_skin, surgery_lymphaden, medicine_lefttonsil, medicine_righttonsil, medicine_bloodpressure, medicine_bloodtype,
medicine_bloodtypename, medicine_hemoglobin, medicine_heart, medicine_liver, medicine_spleen, medicine_lung, other_rickets_chongmen, other_rickets_fangtou,
other_rickets_pingpongtou, other_rickets_zhentu, other_rickets_jixiong, other_rickets_lechuanzhu, other_rickets_haoshigou,other_rickets_yijingduohan,
other_rickets_xo, other_infantileparalysis, other_ascarid, other_skins, other_deformity, other_psychosis, other_hernia, other_others, memo) VALUES
('93740362', '老赵', 1323390313123, 1, '1.78m', '180kg', '5.0', '4.9', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', 'B', 'B型血',
'无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '无', '没有备注');

# --- !Downs

DROP TABLE IF EXISTS healthcheckdetail;