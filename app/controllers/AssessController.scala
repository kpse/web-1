package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import models.Assess

object AssessController extends Controller {
  implicit val write = Json.writes[Assess]

  def index(kg: Long, childId: String, from: Option[Long], to: Option[Long], most: Option[Int]) = Action {
    Ok(Json.toJson(Assess.all(kg, childId, from, to).take(most.getOrElse(25))))
  }
}
