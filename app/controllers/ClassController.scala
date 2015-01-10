package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.{JsError, Json}
import models._
import play.Logger
import models.SchoolClass
import models.ErrorResponse

object ClassController extends Controller with Secured {

  implicit val write1 = Json.writes[SchoolClass]
  implicit val write2 = Json.writes[SuccessResponse]
  implicit val write3 = Json.writes[ErrorResponse]
  implicit val write4 = Json.writes[Employee]
  implicit val read1 = Json.reads[SchoolClass]
  implicit val read2 = Json.reads[Employee]

  def index(kg: Long) = IsLoggedIn {
    u => _ =>
      Logger.info(u)
      Ok(Json.toJson(UserAccess.filter(UserAccess.queryByUsername(u, kg))(School.allClasses(kg))))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString())
        request.body.validate[SchoolClass].map {
          case (classInfo) if School.classNameExists(classInfo) =>
            Ok(Json.toJson(ErrorResponse("已有ID不相同的同名班级,请确认信息正确性")))
          case (classInfo) =>
            Ok(Json.toJson(School.updateOrCreate(classInfo)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }

  }

  def update(kg: Long, classId: Long) = IsLoggedIn(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString())
        request.body.validate[SchoolClass].map {
          case (classInfo) =>
            Ok(Json.toJson(School.updateOrCreate(classInfo)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }

  }

  def delete(kg: Long, classId: Long) = IsLoggedIn {
    u => _ =>
      School.hasChildrenInClass(kg, classId) match {
        case (true) =>
          BadRequest(Json.toJson(ErrorResponse("不能删除还有学生的班级。")))
        case (false) =>
          School.removeClass(kg, classId)
          Ok(Json.toJson(new SuccessResponse))
      }

  }

  def managers(kg: Long, classId: Long) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(School.getClassManagers(kg, classId)))
  }

  def createManager(kg: Long, classId: Long) = IsLoggedIn(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString())
        request.body.validate[Employee].map {
          case (noUpdate) if School.managerExists(kg, classId, noUpdate) =>
            Ok(Json.toJson(new SuccessResponse))
          case (employee) =>
            Ok(Json.toJson(School.createClassManagers(kg, classId, employee)))
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  def removeAll(kg: Long) = IsPrincipal {
    u => _ =>
      School.cleanSchoolData(kg)
      Ok(Json.toJson(new SuccessResponse))
  }
}
