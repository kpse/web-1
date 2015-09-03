# --- !Ups
-- --------------------------------------------------------
--          case class Vitamin(va: Option[String], vc: Option[String], vd: Option[String], ve: Option[String], vb1: Option[String],
--                    vb2: Option[String], vb3: Option[String], vb5: Option[String], vb6: Option[String], vb9: Option[String], vb12: Option[String], vh: Option[String])
-- case class Metal(ca: Option[String], k: Option[String], na: Option[String], mg: Option[String], fe: Option[String],
--                  zn: Option[String], se: Option[String], cu: Option[String], mn: Option[String], cr: Option[String], mo: Option[String])
--
-- case class DietNutrition(id: Option[Long], food_type_id: Option[Long], name: Option[String], alias: Option[String], weight: Option[String], available_weight: Option[String],
--                          calorie: Option[String], protein: Option[String], fat: Option[String], carbohydrate: Option[String], ash: Option[String],
--                          carotine: Option[String], fibre: Option[String], cholesterol: Option[String], vitamin: Option[Vitamin]
--                          , p: Option[String], metal: Option[Metal], store_type: Int = 1, former_id: Option[Long] = None)
--
-- 表的结构 dietnutrition
--

CREATE TABLE dietnutrition (
  uid int(11) NOT NULL AUTO_INCREMENT,
  school_id varchar(20)   NOT NULL,
  food_type_id INT(11)   NOT NULL,
  name VARCHAR(50),
  alias VARCHAR(30),
  weight VARCHAR(30),
  available_weight VARCHAR(30),
  calorie VARCHAR(30),
  protein VARCHAR(30),
  fat VARCHAR(30),
  carbohydrate VARCHAR(30),
  ash VARCHAR(30),
  carotine VARCHAR(30),
  fibre VARCHAR(30),
  cholesterol VARCHAR(30),
  vitamin_va VARCHAR(30),
  vitamin_vc VARCHAR(30),
  vitamin_vd VARCHAR(30),
  vitamin_ve VARCHAR(30),
  vitamin_vb1 VARCHAR(30),
  vitamin_vb2 VARCHAR(30),
  vitamin_vb3 VARCHAR(30),
  vitamin_vb5 VARCHAR(30),
  vitamin_vb6 VARCHAR(30),
  vitamin_vb9 VARCHAR(30),
  vitamin_vb12 VARCHAR(30),
  vitamin_vh VARCHAR(30),
  p VARCHAR(30),
  metal_ca VARCHAR(30),
  metal_k VARCHAR(30),
  metal_na VARCHAR(30),
  metal_mg VARCHAR(30),
  metal_fe VARCHAR(30),
  metal_zn VARCHAR(30),
  metal_se VARCHAR(30),
  metal_cu VARCHAR(30),
  metal_mn VARCHAR(30),
  metal_cr VARCHAR(30),
  metal_mo VARCHAR(30),
  store_type INT(4),
  former_id INT(11),
  created_at bigint(20) NOT NULL DEFAULT 0,
  status INT(4) DEFAULT 1,
  PRIMARY KEY (uid),
  KEY(school_id)
);

insert into dietnutrition (school_id, food_type_id, name, alias, weight, available_weight, calorie, protein, fat, carbohydrate, ash, carotine, fibre, cholesterol, vitamin_va, vitamin_vc, vitamin_vd, vitamin_ve, vitamin_vb1, vitamin_vb2, vitamin_vb3, vitamin_vb5, vitamin_vb6, vitamin_vb9, vitamin_vb12, vitamin_vh, p, metal_ca, metal_k, metal_na, metal_mg, metal_fe, metal_zn, metal_se, metal_cu, metal_mn, metal_cr, metal_mo, store_type, former_id, created_at) values
('93740362', 1, 'name1', 'alias1', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', 1, 1, 1435767784000),
('93740362', 2, 'name2', 'alias2', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', 0, 2, 1435767784000),
('93740362', 3, 'name4', 'alias3', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', '2mg', '3mg', '1mg', 1, 3, 1435767784000);

# --- !Downs

DROP TABLE IF EXISTS dietnutrition;