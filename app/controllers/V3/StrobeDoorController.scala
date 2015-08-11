package controllers.V3

import controllers.Secured
import models.V3.Hardware
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

case class StrobeDoor(id: Option[Long], channel_1_camera: Option[Long], channel_2_camera: Option[Long], hardware: Hardware) {
  def update(kg: Long) = StrobeDoor(id, channel_1_camera, channel_2_camera, hardware)

  def create(kg: Long) = StrobeDoor(Some(1), channel_1_camera, channel_1_camera, hardware)
}

object StrobeDoor {
  implicit val readStrobeDoor = Json.reads[StrobeDoor]
  implicit val writeStrobeDoor = Json.writes[StrobeDoor]

  def deleteById(kg: Long, id: Long) = None

  def show(kg: Long, id: Long): Option[StrobeDoor] = Some(StrobeDoor(Some(id), Some(1), Some(2), Hardware(Some(1), Some("cam1"), Some("sn"), Some("8.8.8.8"), Some(80), Some(1), Some(System.currentTimeMillis()))))

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = List(StrobeDoor(Some(1), Some(1), Some(2), Hardware(Some(1), Some("cam1"), Some("sn"), Some("8.8.8.8"), Some(80), Some(1), Some(System.currentTimeMillis()))))

}

object StrobeDoorController extends Controller with Secured {

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(StrobeDoor.index(kg, from, to, most)))
  }

  def show(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    StrobeDoor.show(kg, id) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"没有ID为${id}的门闸设备。(No such strobe door)")))
    }
  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[StrobeDoor].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(kg: Long, id: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[StrobeDoor].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(kg: Long, id: Long) = IsLoggedIn { u => _ =>
    StrobeDoor.deleteById(kg, id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}
