package controllers.V3

import controllers.ClassController._
import controllers.Secured
import controllers.helper.JsonLogger._
import models.ErrorResponse
import models.V3.SchoolClassV3
import play.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Controller, SimpleResult}

object ClassController extends Controller with Secured {

  def index(kg: Long) = IsPrincipal {
    u => _ =>
      Ok(Json.toJson(SchoolClassV3.index(kg)))
  }

  def create(kg: Long) = IsPrincipal(parse.json) { u => request =>
    request.body.validate[SchoolClassV3].map {
      noZeroID orElse idExists orElse noDuplicateName orElse createClass(kg)
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsPrincipal(parse.json) { u => request =>
    request.body.validate[SchoolClassV3].map {
      noZeroID orElse idExists orElse noDuplicateName orElse updateClass(kg)
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def createClass(kg: Long): PartialFunction[SchoolClassV3, SimpleResult] = {
    case (classInfo) =>
      Ok(Json.toJson(classInfo.copy(school_id = kg).create))
  }

  def updateClass(kg: Long): PartialFunction[SchoolClassV3, SimpleResult] = {
    case (classInfo) =>
      Logger.info(s"clearCurrentCache in V3 updating ${classInfo}")
      clearCurrentCache(kg)
      Ok(Json.toJson(classInfo.copy(school_id = kg).update))
  }

  val noZeroID: PartialFunction[SchoolClassV3, SimpleResult] = {
    case (classInfo) if classInfo.class_id equals Some(0) =>
      Ok(loggedJson(ErrorResponse("不允许创建ID为0的班级。(class id cannot be set to 0)", 1)))
  }

  val idExists: PartialFunction[SchoolClassV3, SimpleResult] = {
    case (classInfo) if classInfo.idExists =>
      Ok(loggedJson(ErrorResponse("班级id已经占用。(class id is occupied)", 3)))
  }

  val noDuplicateName: PartialFunction[SchoolClassV3, SimpleResult] = {
    case (classInfo) if classInfo.classNameExists() =>
      Ok(loggedJson(ErrorResponse("已有ID不相同的同名班级,请确认信息正确性。(class id and name is conflicting)", 2)))
  }

  def clearCurrentCache(kg: Long) = {
    StudentController.clearCurrentCache(kg)
  }
}
