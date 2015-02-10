package controllers

import models.{Assess, ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object AssessController extends Controller with Secured {
  implicit val write = Json.writes[Assess]
  implicit val write1 = Json.writes[SuccessResponse]
  implicit val write2 = Json.writes[ErrorResponse]

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

  def batchCreate(kg: Long) = IsAuthenticated(parse.json) {
    u =>
      request =>
        request.body.validate[List[Assess]].map {
          case (assessments) =>
            Assess.batchCreate(kg, assessments).isEmpty match {
              case true =>
                Ok(Json.toJson(ErrorResponse("批量创建评价失败。(Batch createing assessment failed.)")))
              case false =>
                Ok(Json.toJson(new SuccessResponse))
            }

        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }
}
