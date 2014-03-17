package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.{JsError, Json}
import models.Assess

object AssessController extends Controller with Secured {
  implicit val write = Json.writes[Assess]

  def index(kg: Long, childId: String, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(Assess.all(kg, childId, from, to).take(most.getOrElse(25))))
  }

  implicit val read = Json.reads[Assess]

  def createOrUpdate(kg: Long, childId: String) = IsLoggedIn(parse.json) {
    u =>
      request =>
        request.body.validate[Assess].map {
          case (assess) =>
            Ok(Json.toJson(Assess.createOrUpdate(kg, childId, assess)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  def delete(kg: Long, childId: String, assessId: Long) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(Assess.delete(kg, childId, assessId)))
  }
}
