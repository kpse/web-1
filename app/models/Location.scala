package models

import anorm.SqlParser._
import anorm.{~, _}
import org.joda.time.{DateTimeZone, DateTime}
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}
import org.joda.time.tz.DateTimeZoneBuilder
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class Location(kg: Long, child_id: String, latitude: String, latitude_direction: String, longitude: String, longitude_direction: String, speed: String, direction: String, timestamp: Long)
case class LocationRecord(deviceId: String, latitude: String, latitude_direction: String, longitude: String, longitude_direction: String, speed: String, direction: String, timestamp: Long)

object Location {
  implicit val locationWrite = Json.writes[Location]
  implicit val locationWrite2 = Json.writes[LocationRecord]
  implicit val locationRead = Json.reads[Location]
  implicit val locationRead2 = Json.reads[LocationRecord]

  def history(deviceId: String, from: Option[String], to: Option[String], most: Option[Int]) = DB.withConnection("location") {
    implicit c =>
      SQL("select * from records where device_id={device} limit 1")
        .on('device -> deviceId)
        .as(pureLocation *)
  }

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

  val pureLocation = {
    get[String]("device_id") ~
    get[String]("latitude") ~
    get[String]("latitude_direction") ~
    get[String]("longitude") ~
    get[String]("longitude_direction") ~
    get[String]("speed") ~
    get[String]("direction") ~
    get[String]("time") ~
      get[String]("date") map {
      case deviceId ~ latitude ~ latitudeD ~ longitude ~ longitudeD ~ speed ~ direction ~ time ~ date =>
        val pattern: DateTimeFormatter = DateTimeFormat.forPattern("ddMMyy-HHmmss")
        val parseDateTime: DateTime = pattern.withZone(DateTimeZone.UTC).parseDateTime(s"$date-$time")
        LocationRecord(deviceId, latitude, latitudeD, longitude, longitudeD, speed, direction, parseDateTime.getMillis)
    }
  }

}
