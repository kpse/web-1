package controllers

import models._
import play.api.libs.json.Json
import play.api.mvc.Controller

object VideoProviderController extends Controller with Secured {

  def index = OperatorPage {
    u => implicit request =>
      Ok(Json.toJson(VideoProvider.index))
  }

  def create(kg: Long) = OperatorPage {
    u => implicit request =>
      VideoProvider.create(kg)
      Ok(Json.toJson(new SuccessResponse))
  }
}
