package controllers

import controllers.V3.StudentController
import controllers.helper.JsonLogger._
import play.api.mvc.{SimpleResult, Action, Controller}
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

  def create(kg: Long) = IsPrincipal(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString())
        request.body.validate[SchoolClass].map(
          noZeroID orElse noDuplicateName orElse createOrUpdate
        ).recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }

  }

  def update(kg: Long, classId: Long) = IsLoggedIn(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString())
        request.body.validate[SchoolClass].map(
          idNotMatched(classId) orElse
            noZeroID orElse
            noDuplicateName orElse
            createOrUpdate
        ).recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  val noZeroID: PartialFunction[SchoolClass, SimpleResult] = {
    case (classInfo) if classInfo.class_id equals Some(0) =>
      Ok(loggedJson(ErrorResponse("不允许创建ID为0的班级.", 1)))
  }

  val noDuplicateName: PartialFunction[SchoolClass, SimpleResult] = {
    case (classInfo) if School.classNameExists(classInfo) =>
      Ok(loggedJson(ErrorResponse("已有ID不相同的同名班级,请确认信息正确性", 2)))
  }

  val createOrUpdate: PartialFunction[SchoolClass, SimpleResult] = {
    case (classInfo) =>
      Logger.info(s"clearCurrentCache in updating ${classInfo}")
      clearCurrentCache(classInfo.school_id)
      Ok(loggedJson(School.updateOrCreate(classInfo)))
  }

  def idNotMatched(classId: Long): PartialFunction[SchoolClass, SimpleResult] = {
    case (classInfo) if classInfo.class_id.getOrElse(0) != classId =>
      Ok(loggedJson(ErrorResponse("different id from url.")))
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
      School.cleanSchoolClassesData(kg)
      Ok(Json.toJson(new SuccessResponse))
  }

  def clearCurrentCache(kg: Long) = {
    StudentController.clearCurrentCache(kg)
  }
}
