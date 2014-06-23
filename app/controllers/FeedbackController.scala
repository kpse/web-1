package controllers

import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import models.{ErrorResponse, JsonResponse, SuccessResponse, Feedback}

object FeedbackController extends Controller with Secured {

  implicit val write1 = Json.writes[SuccessResponse]
  implicit val write2 = Json.writes[ErrorResponse]
  implicit val write3 = Json.writes[Feedback]
  implicit val read1 = Json.reads[Feedback]

  def create = Action(parse.json) {
    implicit request =>
      request.body.validate[Feedback].map {
        case (info) =>
          info.create
          Ok(Json.toJson(new SuccessResponse))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def index(source: Option[String]) = Action {
    Ok(Json.toJson(Feedback.index(source)))

  }

  def update(id: Long) = OperatorPage(parse.json) {
    u => request =>
      request.body.validate[Feedback].map {
        case (info) if info.id.getOrElse(-1) != id =>
          BadRequest(Json.toJson(ErrorResponse("id不一致")))
        case (info) =>
          Feedback.update(info)
          Ok(Json.toJson(new SuccessResponse))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}
