package models.V3

case class Vitamin(va: Option[String], vc: Option[String], vd: Option[String], ve: Option[String], vb1: Option[String],
                   vb2: Option[String], vb3: Option[String], vb5: Option[String], vb6: Option[String], vb9: Option[String], vb12: Option[String], vh: Option[String])
case class Metal(ca: Option[String], k: Option[String], na: Option[String], mg: Option[String], fe: Option[String],
                 zn: Option[String], se: Option[String], cu: Option[String], mn: Option[String], cr: Option[String], mo: Option[String])

case class DietNutrition(id: Option[Long], food_type_id: Option[Long], name: Option[String], alias: Option[String], weight: Option[String], available_weight: Option[String],
                         calorie: Option[String], protein: Option[String], fat: Option[String], carbohydrate: Option[String], ash: Option[String],
                         carotine: Option[String], fibre: Option[String], cholesterol: Option[String], vitamin: Option[Vitamin]
                         , p: Option[String], metal: Option[Metal], store_type: Int = 1, former_id: Option[Long] = None)
