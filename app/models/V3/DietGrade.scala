package models.V3

import play.api.libs.json.Json

case class DietGrade(id: Option[Long], name: Option[String], age_name: Option[String], age_min: Option[String], age_max: Option[String], gender: Option[Int],
                     Labor: Option[String], Joule: Option[String],
                     calorie: Option[String], protein: Option[String], fat: Option[String],
                         carotine: Option[String], choline: Option[String], cholesterol: Option[String], vitamin: Option[Vitamin]
                         , p: Option[String], i: Option[String], f: Option[String], metal: Option[Metal])

object DietGrade {
  implicit val writeVitamin = Json.writes[Vitamin]
  implicit val readVitamin = Json.reads[Vitamin]
  implicit val writeMetal = Json.writes[Metal]
  implicit val readMetal = Json.reads[Metal]
  implicit val writeDietGrade = Json.writes[DietGrade]
  implicit val readDietGrade = Json.reads[DietGrade]
}