package controllers.V3

import controllers.Secured
import models.V3.Hardware
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class Camera(id: Option[Long], account: String, password: String, hardware: Hardware) {
  def update(kg: Long) = Camera(id, account, password, hardware)

  def create(kg: Long) = Camera(Some(1), account, password, hardware)
}

object Camera {
  implicit val readCamera = Json.reads[Camera]
  implicit val writeCamera = Json.writes[Camera]

  def deleteById(kg: Long, id: Long) = None

  def show(kg: Long, id: Long): Option[Camera] = Some(Camera(Some(id), "account", "password", Hardware(Some(1), Some("cam1"), Some("sn"), Some("8.8.8.8"), Some(80), Some(1), Some(System.currentTimeMillis()))))

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = List(Camera(Some(1), "account", "password", Hardware(Some(1), Some("cam1"), Some("sn"), Some("8.8.8.8"), Some(80), Some(1), Some(System.currentTimeMillis()))))

}

object CameraController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(Camera.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Camera.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的摄像头。(No such camera)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Camera].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[Camera].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    Camera.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}
