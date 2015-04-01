package controllers.V2

import controllers.Secured
import models.{Advertisement, ErrorResponse, SuccessResponse, BusLocation}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object BusLocationController extends Controller with Secured  {
  def index(kg: Long, driverId: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(BusLocation.index(kg, driverId)))
  }

  def create(kg: Long, driverId: String) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[BusLocation].map {
        case (location) =>
          BusLocation.create(kg, driverId, location)
          Ok(Json.toJson(new SuccessResponse))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }

  }

  def childOnBus(kg: Long, childId: String) = IsLoggedIn {
    u => _ =>
      BusLocation.child(kg, childId) match {
        case Some(x) if x.status == 0 =>
          NotFound(Json.toJson(ErrorResponse(s"今天${childId}已下车(Got off the school bus already today.)")))
        case Some(x) =>
          Ok(Json.toJson(x))
        case None =>
          NotFound(Json.toJson(ErrorResponse(s"今天没有${childId}的乘车记录(No riding records today.)")))
      }
      
  }

  def checkIn(kg: Long, driverId: String, childId: String) = IsLoggedIn {
    u => _ =>
      BusLocation.checkIn(kg, driverId, childId)
      Ok(Json.toJson(new SuccessResponse))
  }

  def checkOut(kg: Long, driverId: String, childId: String) = IsLoggedIn {
    u => _ =>
      BusLocation.checkOut(kg, driverId, childId)
      Ok(Json.toJson(new SuccessResponse))
  }

  def batchCheckOut(kg: Long, driverId: String) = IsLoggedIn {
    u => _ =>
      BusLocation.BatchCheckOut(kg, driverId, List(""))
      Ok(Json.toJson(new SuccessResponse))
  }
}
