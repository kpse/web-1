package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.{JsError, Json}
import models.Assignment

object AssignmentController extends Controller {

  implicit val write = Json.writes[Assignment]
  implicit val read1 = Json.reads[Assignment]

  def index(kg: Long, classId: Option[String], from: Option[Long], to: Option[Long], most: Option[Int]) = Action {
    Ok(Json.toJson(Assignment.index(kg, classId, from, to, most).take(most.getOrElse(25))))
  }

  def update(kg: Long, assignmentId: Long) = Action(parse.json) {
    request =>
      request.body.validate[Assignment].map {
        case (assignment) =>
          Ok(Json.toJson(Assignment.update(kg, assignmentId, assignment)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def create(kg: Long) = Action(parse.json) {
    request =>
      request.body.validate[Assignment].map {
        case (assignment) =>
          Ok(Json.toJson(Assignment.create(kg, assignment)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}
