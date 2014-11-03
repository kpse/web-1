package controllers

import controllers.AssessController._
import models.{Advertisement, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}

object ADController extends Controller with Secured {

  def index(kg: Long) = Action {
    Ok(Json.toJson(Advertisement.index(kg)))
  }

  def show(kg: Long, id: Long) = Action {
    Ok(Json.toJson(Advertisement.show(kg, id)))
  }

  def create(kg: Long) = IsOperator(parse.json) { u =>
    request =>
      request.body.validate[Advertisement].map {
        case (ad) =>
          Advertisement.create(kg, ad)
          Ok(Json.toJson(new SuccessResponse))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}
