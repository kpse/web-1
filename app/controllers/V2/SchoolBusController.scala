package controllers.V2

import controllers.ClassController._
import controllers.Secured
import models.V2.SchoolBus
import models.V2.SchoolBus._
import models._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Controller, SimpleResult}

object SchoolBusController extends Controller with Secured {
  def index(kg: Long) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(SchoolBus.index(kg)))
  }

  def show(kg: Long, busId: Long) = IsLoggedIn {
    u => _ =>
      SchoolBus.show(kg, busId) match {
        case Some(p) =>
          Ok(Json.toJson(p))
        case None =>
          NotFound(Json.toJson(ErrorResponse("没有对应的校车${busId}。(No such school bus)")))
      }
  }

  def delete(kg: Long, busId: Long) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(SchoolBus.delete(kg, busId)))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[SchoolBus].map {
        handleCreation(kg)
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }


  def handleCreation(kg: Long): PartialFunction[SchoolBus, SimpleResult] = {
    guardSchoolId(kg) orElse
    guardEmployee(kg) orElse
      updateBus orElse
      createBus
  }

  def guardSchoolId(kg: Long): PartialFunction[SchoolBus, SimpleResult] = {
    case (plan) if plan.school_id != kg =>
      BadRequest(Json.toJson(ErrorResponse("错误的学校id.(Wrong school id in creating/updating school bus)")))
  }

  def guardEmployee(kg: Long): PartialFunction[SchoolBus, SimpleResult] = {
    case (plan) if plan.driver.isEmpty =>
      BadRequest(Json.toJson(ErrorResponse("请提供的驾驶员信息.(no teacher in creating/updating school bus)")))
    case (plan) if Employee.phoneSearch(plan.driver.get.phone).isEmpty =>
      BadRequest(Json.toJson(ErrorResponse("该学校不存在的驾驶员.(Wrong teacher in creating/updating school bus)")))
  }

  val createBus: PartialFunction[SchoolBus, SimpleResult] = {
    case (bus) =>
      bus.create match {
        case Some(p) =>
          Ok(Json.toJson(p))
        case None =>
          InternalServerError(Json.toJson(ErrorResponse("创建校车失败, 请联系管理员.(Error in creating school bus)")))
      }
  }

  val updateBus: PartialFunction[SchoolBus, SimpleResult] = {
    case (bus) if bus.exists =>
      bus.update match {
        case Some(p) =>
          Ok(Json.toJson(p))
        case None =>
          InternalServerError(Json.toJson(ErrorResponse("更新校车失败, 请联系管理员.(Error in creating school bus)")))
      }
  }
}
