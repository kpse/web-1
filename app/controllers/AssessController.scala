package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.{JsError, Json}
import models.Assess

object AssessController extends Controller {
  implicit val write = Json.writes[Assess]

  def index(kg: Long, childId: String, from: Option[Long], to: Option[Long], most: Option[Int]) = Action {
    Ok(Json.toJson(Assess.all(kg, childId, from, to).take(most.getOrElse(25))))
  }

  implicit val read = Json.reads[Assess]

  def createOrUpdate(kg: Long, childId: String) = Action(parse.json) {
    request =>
      request.body.validate[Assess].map {
        case (assess) =>
          Ok(Json.toJson(Assess.createOrUpdate(kg, childId, assess)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def delete(kg: Long, childId: String, assessId: Long) = Action {
    Ok(Json.toJson(Assess.delete(kg, childId, assessId)))
  }
}
