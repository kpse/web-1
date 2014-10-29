package controllers

import models.Advertisement
import play.api.libs.json.Json
import play.api.mvc.{Controller, Action}

object ADController extends Controller with Secured {

  def index(kg: Long) = Action {
    Ok(Json.toJson(Advertisement.find(kg)))
  }

  def show(kg: Long, id: Long) = Action {
    Ok(Json.toJson(Advertisement.show(kg, id)))
  }
}
