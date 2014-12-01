package models

import anorm.SqlParser._
import anorm.{~, _}
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class Location(kg: Long, child_id: String, latitude: String, latitude_direction: String, longitude: String, longitude_direction: String, speed: String, direction: String, timestamp: Long)

object Location {
  implicit val locationWrite = Json.writes[Location]
  implicit val locationRead = Json.reads[Location]
  def find(kg: Long, childId: String) = DB.withConnection("location") {
    implicit c =>
      SQL("select * from records limit 1").as(simple(kg, childId) *)
  }

  def simple(kg: Long, childId: String) = {
    get[String]("latitude") ~
    get[String]("latitude_direction") ~
    get[String]("longitude") ~
    get[String]("longitude_direction") ~
    get[String]("speed") ~
    get[String]("direction") ~
    get[String]("time") ~
      get[String]("date") map {
      case latitude ~ latitudeD ~ longitude ~ longitudeD ~ speed ~ direction ~ time ~ date =>
        Location(kg, childId, latitude, latitudeD, longitude, longitudeD, speed, direction, System.currentTimeMillis())
    }
  }

}
