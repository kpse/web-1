package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json
import models.Assignment

object AssignmentController extends Controller {

  implicit val write = Json.writes[Assignment]

  def index(kg: Long, classId: Option[String], from: Option[Long], to: Option[Long], most: Option[Int]) = Action {
    Ok(Json.toJson(Assignment.index(kg, classId, from, to, most).take(most.getOrElse(25))))
  }
}
