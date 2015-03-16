package controllers

import models.PushableNumber._
import play.api.libs.json.Json
import play.api.mvc.Controller

object LoginTokenController extends Controller with Secured {
  def tokenOf(phone: String) = IsOperator {
    u => _ =>
      Ok(Json.toJson(phone.token))
  }
}
