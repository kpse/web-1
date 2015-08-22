package controllers.V3

import controllers.Secured
import models.V3.MedicineRecord
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class SchoolInternalNotification(id: Option[Long], class_id: Int, title: String, content: String, created_at: Option[Long]) {
  def update(kg: Long, id: Long): Option[SchoolInternalNotification] = Some(this.copy(id = Some(id)))

  def create(kg: Long): Option[SchoolInternalNotification] = Some(this.copy(id = Some(1)))
}

object SchoolInternalNotification {
  implicit val readSchoolInternalNotification = Json.reads[SchoolInternalNotification]
  implicit val writeSchoolInternalNotification = Json.writes[SchoolInternalNotification]

  def show(kg: Long, id: Long): Option[SchoolInternalNotification] = Some(SchoolInternalNotification(Some(id), 1, "给一班的通知", "明天放假", Some(System.currentTimeMillis())))

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = List(SchoolInternalNotification(Some(1), 1, "给一班的通知", "明天放假", Some(System.currentTimeMillis())))

  def deleteById(kg: Long, id: Long) = None
}

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
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[SchoolInternalNotification].map {
      case (s) =>
        Ok(Json.toJson(s.update(kg, id)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    SchoolInternalNotification.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}