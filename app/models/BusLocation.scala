package models

import play.api.libs.json.Json

case class BusLocation(school_id: Long, driver_id: String, latitude: Double, longitude: Double, direction: Float, radius: Float, address: String, child_id: Option[String] = None, timestamp: Option[Long] = None)

object BusLocation {
  implicit val busLocationReads = Json.reads[BusLocation]
  implicit val busLocationWrites = Json.writes[BusLocation]

  def index(kg: Long, driverId: String) = List(BusLocation(kg, driverId, 108.883425, 34.253351, 0, 0, "", None, Some(System.currentTimeMillis)))

  def child(kg: Long, childId: String) = List(BusLocation(kg, "", 108.883425, 34.253351, 0, 0, "", Some(childId), Some(System.currentTimeMillis)))
}
