package controllers

import models.{ErrorResponse, PowerStatus, Location}
import models.Location._
import play.api.libs.json.Json
import play.api.mvc._

object LocationController extends Controller {
  def show(kg: Long, childId: String) = Action {
       Ok(Json.toJson(Location.find(kg, childId)))
  }

  def history(deviceId: String, from: Option[String], to: Option[String], most: Option[Int]) = Action {
    Ok(Json.toJson(Location.history(deviceId, from, to, most)))
  }

  def status(deviceId: String) = Action {
    val status1: Option[PowerStatus] = Location.powerStatus(deviceId)
    status1 match {
      case Some(p) => Ok(Json.toJson(p))
      case None => Ok(Json.toJson(ErrorResponse("no such device registered.")))
    }

  }
}
