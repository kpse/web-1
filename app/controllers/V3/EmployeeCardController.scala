package controllers.V3

import controllers.Secured
import controllers.helper.JsonLogger.loggedJson
import models.V3.{CardV3, EmployeeCard}
import models.{ErrorResponse, Relationship, SuccessResponse}
import play.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{SimpleResult, Controller}

object EmployeeCardController extends Controller with Secured {
  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(EmployeeCard.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    EmployeeCard.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的老师卡。(No such employee card)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    Logger.info(s"employee card create: ${request.body}")
    request.body.validate[EmployeeCard].map {
      withId orElse notParentCard orElse noConflicts orElse reuseDeletedResource(kg) orElse toCreate(kg)
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    Logger.info(s"employee update: ${request.body}")
    request.body.validate[EmployeeCard].map {
      idMatched(id) orElse notParentCard orElse noConflicts orElse toUpdate(kg)
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    EmployeeCard.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }

  def cardCheck(kg: Long, cardNum: String, id: Option[Long]) = IsLoggedIn {
    u => _ =>
      cardNum match {
        case (card) if id.isEmpty && Relationship.cardExists(card, None) =>
          Ok(loggedJson(ErrorResponse("该卡片已被家长占用。(Relatives occupied)")))
        case (card) if Relationship.cardExists(card, None) != Relationship.cardExists(card, id) =>
          Ok(loggedJson(ErrorResponse("该卡片已被家长占用。(Relatives occupied)")))
        case (card) if EmployeeCard.cardExists(card, id) =>
          Ok(loggedJson(ErrorResponse("该卡片已被老师占用。(Employees occupied)", 2)))
        case (card) if !CardV3.valid(card) =>
          Ok(loggedJson(ErrorResponse("该卡片未被授权。(Non-authorised card number)", 3)))
        case (card) =>
          Ok(loggedJson(SuccessResponse("该卡片可以使用。(This is a virgin card)")))
      }
  }

  val noConflicts: PartialFunction[EmployeeCard, SimpleResult] = {
    case (s) if s.cardExists =>
      InternalServerError(Json.toJson(ErrorResponse("卡号重复.(Duplicated card number)", 4)))
    case (s) if s.employeeExists =>
      InternalServerError(Json.toJson(ErrorResponse("该教师已有卡片.(Duplicated employee id)", 5)))
  }

  val notParentCard: PartialFunction[EmployeeCard, SimpleResult] = {
    case (s) if Relationship.cardExists(s.card, None) =>
      InternalServerError(Json.toJson(ErrorResponse("此卡号已经绑定给家长.(Card has been occupied by a parent)", 8)))
  }

  val withId: PartialFunction[EmployeeCard, SimpleResult] = {
    case (s) if s.id.isDefined =>
      BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(Please use update interface)", 2)))
  }

  def idMatched(id: Long): PartialFunction[EmployeeCard, SimpleResult] = {
    case (s) if s.id != Some(id) =>
      BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
  }

  def reuseDeletedResource(kg: Long): PartialFunction[EmployeeCard, SimpleResult] = {
    case (s) if s.cardDeleted =>
      Ok(Json.toJson(s.reuseDeleted(kg)))
    case (s) if s.employeeDeleted =>
      Ok(Json.toJson(s.changeOwner(kg)))
  }

  def toCreate(kg: Long): PartialFunction[EmployeeCard, SimpleResult] = {
    case (s) =>
      s.create(kg) match {
        case Some(created) =>
          Ok(Json.toJson(created))
        case None =>
          InternalServerError(Json.toJson(ErrorResponse("创建老师卡失败.(Error in creating employee card)", 6)))
      }

  }

  def toUpdate(kg: Long): PartialFunction[EmployeeCard, SimpleResult] = {
    case (s) =>
      s.update(kg) match {
        case Some(updated) =>
          Ok(Json.toJson(updated))
        case None =>
          InternalServerError(Json.toJson(ErrorResponse("修改老师卡失败.(Error in updating employee card)", 7)))
      }
  }
}