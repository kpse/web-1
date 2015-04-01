package controllers.V2

import controllers.Secured
import models.{SuccessResponse, BusLocation}
import play.api.libs.json.Json
import play.api.mvc.Controller

object BusLocationController extends Controller with Secured  {
  def index(kg: Long, driverId: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(BusLocation.index(kg, driverId)))
  }

  def create(kg: Long, driverId: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(new SuccessResponse))
  }

  def childOnBus(kg: Long, childId: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(BusLocation.child(kg, childId)))
  }

  def checkIn(kg: Long, driverId: String, childId: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(new SuccessResponse))
  }

  def checkOut(kg: Long, driverId: String, childId: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(new SuccessResponse))
  }

  def batchCheckOut(kg: Long, driverId: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(new SuccessResponse))
  }
}
