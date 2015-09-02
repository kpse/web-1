package controllers.V3

import anorm.SqlParser._
import anorm.~
import controllers.Secured
import models.V3.SchoolInternalNotification
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object NotificationController extends Controller with Secured {
  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(SchoolInternalNotification.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    SchoolInternalNotification.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的校内通知。(No such school internal notification)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[SchoolInternalNotification].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[SchoolInternalNotification].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    SchoolInternalNotification.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}