package controllers

import controllers.AssessController._
import models.{ErrorResponse, Advertisement, SuccessResponse}
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

  def update(kg: Long, id: Long) = IsOperator(parse.json) { u =>
    request =>
      request.body.validate[Advertisement].map {
        case (ad) if ad.id.getOrElse(0) != id =>
          Ok(Json.toJson(new ErrorResponse("广告id不匹配。")))
        case (ad) =>
          Advertisement.update(kg, ad)
          Ok(Json.toJson(new SuccessResponse))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}
