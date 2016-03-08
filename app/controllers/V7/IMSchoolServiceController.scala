package controllers.V7

import controllers.Secured
import models.SuccessResponse
import models.V7.IMSchool
import play.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object IMSchoolServiceController extends Controller with Secured {
  def index(kg: Long) = IsPrincipal {
    u => _ =>
      Ok(Json.toJson(IMSchool.index(kg)))
  }

  def create(kg: Long) = IsPrincipal(parse.json) {
    u => request =>
      Logger.info(request.body.toString())
      request.body.validate[IMSchool].map {
        case (school) =>
          Ok(Json.toJson(school.handle(kg)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def delete(kg: Long) = IsPrincipal {
    u => _ =>
      IMSchool.delete(kg)
      Ok(Json.toJson(new SuccessResponse))
  }
}
