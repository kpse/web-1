package controllers

import play.api.mvc._
import play.api.libs.json.{JsError, Json}
import models._
import models.Schedule._

object ScheduleController extends Controller with Secured {

  case class EmptyResult(error_code: Int)

  implicit val write5 = Json.writes[EmptyResult]

  def preview(kg: Long, classId: Long) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(Schedule.preview(kg, classId)))
  }

  def show(kg: Long, classId: Long, scheduleId: Long) = IsLoggedIn {
    u => _ =>
      Schedule.show(kg, classId, scheduleId) match {
        case Some(r) => Ok(Json.toJson(r))
        case None => Ok(Json.toJson(EmptyResult(1)))
      }

  }

  def update(kg: Long, classId: Long, scheduleId: Long) = IsLoggedIn(parse.json) {
    u =>
      implicit request =>
        request.body.validate[ScheduleDetail].map {
          case (detail) =>
            Schedule.insertNew(detail.copy(school_id = kg, class_id = classId, schedule_id = scheduleId)) match {
              case Some(data) => Ok(Json.toJson(data))
              case None => InternalServerError(Json.toJson(ErrorResponse("创建课程表失败。(failed updating class schedule)")))
            }

        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  def index(kg: Long, classId: Long) = Action {
    Ok(Json.toJson(Schedule.all(kg, classId)))
  }

  def create(kg: Long, classId: Long) = IsLoggedIn(parse.json) {
    u =>
      implicit request =>
        request.body.validate[Schedule].map {
          case (s) =>
            Ok(Json.toJson(Schedule.create(kg, classId, s)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }
}
