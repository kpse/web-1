package controllers.V3

import controllers.Secured
import models.V3.{SchoolInternalFeedback, MedicineRecord}
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object NotificationFeedbackController extends Controller with Secured {
  def index(kg: Long, notificationId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(SchoolInternalFeedback.index(kg, notificationId, from, to, most)))
  }

  def show(kg: Long, notificationId: Long, id: Long) = IsLoggedIn { u => _ =>
    SchoolInternalFeedback.show(kg, notificationId, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的通知反馈。(No such school internal feedback)")))
    }
  }

  def create(kg: Long, notificationId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[SchoolInternalFeedback].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) if s.notification_id != notificationId =>
        BadRequest(Json.toJson(ErrorResponse("内部通知ID不匹配(notification is not match)", 4)))
      case (s) =>
        Ok(Json.toJson(s.create(kg, notificationId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, notificationId: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[SchoolInternalFeedback].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg, notificationId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, notificationId: Long, id: Long) = IsLoggedIn { u => _ =>
    SchoolInternalFeedback.deleteById(kg, notificationId, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}