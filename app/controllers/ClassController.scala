package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.{JsError, Json}
import models._
import play.Logger
import models.SchoolClass
import models.ErrorResponse

object ClassController extends Controller {

  implicit val write1 = Json.writes[SchoolClass]
  implicit val write2 = Json.writes[SuccessResponse]
  implicit val write3 = Json.writes[ErrorResponse]
  implicit val read1 = Json.reads[SchoolClass]

  def index(kg: Long) = Action {
    Ok(Json.toJson(School.allClasses(kg)))
  }

  def create(kg: Long) = Action(parse.json) {
    request =>
      Logger.info(request.body.toString())
      request.body.validate[SchoolClass].map {
        case (classInfo) =>
          Ok(Json.toJson(School.updateOrCreate(classInfo)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }

  }

  def update(kg: Long, classId: Long) = Action(parse.json) {
    request =>
      Logger.info(request.body.toString())
      request.body.validate[SchoolClass].map {
        case (classInfo) =>
          Ok(Json.toJson(School.updateOrCreate(classInfo)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }

  }

  def delete(kg: Long, classId: Long) = Action {
    School.hasChildrenInClass(kg, classId) match {
      case (true) =>
        BadRequest(Json.toJson(new ErrorResponse("不能删除还有学生的班级。")))
      case (false) =>
        School.removeClass(kg, classId)
        Ok(Json.toJson(new SuccessResponse))
    }

  }
}
