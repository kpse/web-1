package controllers.V3

import controllers.Secured
import models.V3.MedicineRecord
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class SchoolInternalFeedback(id: Option[Long], notification_id: Long, class_id: Int, created_at: Option[Long]) {
  def update(kg: Long, notificationId: Long, id: Long): Option[SchoolInternalFeedback] = Some(this.copy(id = Some(id), notification_id = notificationId))

  def create(kg: Long, notificationId: Long): Option[SchoolInternalFeedback] = Some(this.copy(id = Some(1), notification_id = notificationId))
}

object SchoolInternalFeedback {
  implicit val readSchoolInternalFeedback = Json.reads[SchoolInternalFeedback]
  implicit val writeSchoolInternalFeedback = Json.writes[SchoolInternalFeedback]

  def show(kg: Long, notificationId: Long, id: Long): Option[SchoolInternalFeedback] = Some(SchoolInternalFeedback(Some(id), notificationId, 1, Some(System.currentTimeMillis())))

  def index(kg: Long, notificationId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = List(SchoolInternalFeedback(Some(1), notificationId, 1, Some(System.currentTimeMillis())))

  def deleteById(kg: Long, notificationId: Long, id: Long) = None
}

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
      case (s) =>
        Ok(Json.toJson(s.create(kg, notificationId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, notificationId: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[SchoolInternalFeedback].map {
      case (s) =>
        Ok(Json.toJson(s.update(kg, notificationId, id)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, notificationId: Long, id: Long) = IsLoggedIn { u => _ =>
    SchoolInternalFeedback.deleteById(kg, notificationId, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}