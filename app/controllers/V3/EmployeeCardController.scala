package controllers.V3

import controllers.Secured
import models.V3.EmployeeCard
import models.{SuccessResponse, ErrorResponse}
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
}