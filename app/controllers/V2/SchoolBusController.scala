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
      SchoolBus.delete(kg, busId)
      Ok(Json.toJson(new SuccessResponse()))
  }

  def create(kg: Long) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[SchoolBus].map {
        handleCreation(kg)
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[SchoolBus].map {
        handleUpdate(kg, id)
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }


  def handleCreation(kg: Long): PartialFunction[SchoolBus, SimpleResult] = {
    guardSchoolId(kg) orElse
      guardBus(kg) orElse
      guardEmployee(kg) orElse
      reviveBus orElse
      updateBus orElse
      createBus
  }

  def handleUpdate(kg: Long, id: Long): PartialFunction[SchoolBus, SimpleResult] = {
    guardSchoolId(kg) orElse
      guardBus(kg) orElse
      guardBusId(id) orElse
      guardEmployee(kg) orElse
      reviveBus orElse
      updateBus
  }

  def guardSchoolId(kg: Long): PartialFunction[SchoolBus, SimpleResult] = {
    case (plan) if plan.school_id != kg =>
      BadRequest(Json.toJson(ErrorResponse("错误的学校id.(Wrong school id in creating/updating school bus)")))
  }

  def driverNotMatchBus(bus: SchoolBus) = bus.id.nonEmpty && bus.driver.nonEmpty && bus.driver.get.id.nonEmpty && SchoolBus.driverDoesNotMatch(bus.driver.get.id.get, bus.id.get)

  def driverIsExisting(bus: SchoolBus) = bus.driver.nonEmpty && bus.driver.get.id.nonEmpty && SchoolBus.driverIsExisting(bus.driver.get.id.getOrElse(""))

  def guardBus(kg: Long): PartialFunction[SchoolBus, SimpleResult] = {
    case (bus) if bus.id.nonEmpty && !bus.exists =>
      BadRequest(Json.toJson(ErrorResponse("班车不存在.(no such school bus)")))
    case (bus) if bus.nameDuplicated =>
      BadRequest(Json.toJson(ErrorResponse(s"班车名${bus.name}已经存在.(Duplicated school bus name)")))
  }

  def guardBusId(id: Long): PartialFunction[SchoolBus, SimpleResult] = {
    case (bus) if bus.id.getOrElse(-1) != id =>
      BadRequest(Json.toJson(ErrorResponse("错误的班车ID.(no such school bus id)")))
  }

  def guardEmployee(kg: Long): PartialFunction[SchoolBus, SimpleResult] = {
    case (bus) if bus.driver.isEmpty =>
      BadRequest(Json.toJson(ErrorResponse("请提供的驾驶员信息.(no teacher in creating/updating school bus)")))
    case (bus) if Employee.phoneSearch(bus.driver.get.phone).isEmpty =>
      BadRequest(Json.toJson(ErrorResponse("该学校不存在的驾驶员.(Wrong teacher in creating/updating school bus)")))
    case (bus) if driverIsExisting(bus) && bus.id.isEmpty =>
      BadRequest(Json.toJson(ErrorResponse(s"该驾驶员${bus.driver.get.id.get}正在驾驶其他车辆.(current driver is existing)")))
    case (bus) if driverIsExisting(bus) && driverNotMatchBus(bus) =>
      BadRequest(Json.toJson(ErrorResponse(s"该驾驶员${bus.driver.get.id.get}正在驾驶其他车辆.(current driver is existing)")))
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

  val reviveBus: PartialFunction[SchoolBus, SimpleResult] = {
    case (bus) if bus.deleted =>
      bus.revive match {
        case Some(p) =>
          Ok(Json.toJson(p))
        case None =>
          InternalServerError(Json.toJson(ErrorResponse("重新创建校车失败, 请联系管理员.(Error in re-creating school bus)")))
      }
  }
}
