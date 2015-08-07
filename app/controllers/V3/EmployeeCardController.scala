package controllers.V3

import controllers.Secured
import controllers.helper.JsonLogger.loggedJson
import models.V3.{CardV3, EmployeeCard}
import models.{ErrorResponse, Relationship, SuccessResponse}
import play.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

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
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) if s.cardExists =>
        InternalServerError(Json.toJson(ErrorResponse("卡号重复(duplicated card number)", 4)))
      case (s) if s.cardDeleted =>
        Ok(Json.toJson(s.reuseDeleted(kg)))
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    Logger.info(s"employee update: ${request.body}")
    request.body.validate[EmployeeCard].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
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
          Ok(loggedJson(ErrorResponse("该卡片未被授权。(Non-autherised card number)", 3)))
        case (card) =>
          Ok(loggedJson(SuccessResponse("该卡片可以使用。(this is a virgin card)")))
      }
  }
}