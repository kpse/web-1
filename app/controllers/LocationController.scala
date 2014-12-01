package controllers

import models.Location
import play.api.libs.json.Json
import play.api.mvc._

object LocationController extends Controller {
  def show(kg: Long, childId: String) = Action {
       Ok(Json.toJson(Location.find(kg, childId)))
  }
}
