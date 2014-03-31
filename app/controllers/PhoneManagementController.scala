package controllers

import play.api.mvc.Controller
import models.{SuccessResponse, ErrorResponse, Parent}
import helper.JsonLogger.loggedJson
import play.api.libs.json.Json

object PhoneManagementController extends Controller with Secured {
  implicit val write = Json.writes[Parent]
  implicit val write2 = Json.writes[ErrorResponse]
  implicit val write3 = Json.writes[SuccessResponse]
  implicit val read = Json.reads[Parent]

  def show(phone: String) = IsOperator {
    u => _ =>
      Parent.phoneSearch(phone).map(p => Ok(loggedJson(p))).getOrElse(NotFound)

  }

  def delete(phone: String) = IsOperator {
    u => _ =>
      Parent.permanentRemove(phone)
      Ok(Json.toJson(new SuccessResponse))
  }
}
