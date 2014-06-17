package controllers

import play.api.mvc.{Action, Controller}
import models.{ErrorResponse, Device, SuccessResponse}
import play.api.libs.json.{JsError, Json}

object MacAddressController extends Controller with Secured {

  implicit val write = Json.writes[SuccessResponse]
  implicit val write1 = Json.writes[ErrorResponse]
  implicit val write2 = Json.writes[Device]
  implicit val read = Json.reads[Device]

  def show(device: String) = Action {
    Device.exists(device) match {
      case true => Ok(Json.toJson(new SuccessResponse))
      case false => NotFound("")

    }
  }

  def index = OperatorPage {
    u => request =>
      Ok(Json.toJson(Device.all))
  }

  def create = OperatorPage(parse.json) {
    u => request =>
      request.body.validate[Device].map {
        case (device: Device) if device.exists() =>
          BadRequest(Json.toJson(ErrorResponse("该地址已经存在。")))
        case (device: Device) =>
          Ok(Json.toJson(device.create()))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def update(notUsed: String) = OperatorPage(parse.json) {
    u => request =>
      request.body.validate[Device].map {
        case (device: Device) if device.duplicated =>
          BadRequest(Json.toJson(ErrorResponse("该地址已经存在。")))
        case (device: Device) =>
          Ok(Json.toJson(device.update()))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def delete(id: Long) = OperatorPage {
    u => _ =>
      Device.delete(id) match {
        case true =>
          Ok(Json.toJson(new SuccessResponse))
        case false =>
          Ok(Json.toJson(new ErrorResponse("删除Mac失败")))
      }
  }

}
