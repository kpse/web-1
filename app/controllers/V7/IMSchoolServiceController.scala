package controllers.V7

import controllers.Secured
import models.{ConfigItem, ErrorResponse, SchoolConfig, SuccessResponse}
import models.SchoolConfig._
import play.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object IMSchoolServiceController extends Controller with Secured {
  private val configKey = "schoolGroupChat"
  def status(kg: Long) = IsPrincipal {
    u => _ =>
      val option: Option[ConfigItem] = SchoolConfig.config(kg).config.find(_.name == configKey)
      option match {
        case Some(o) =>
          Ok(Json.toJson(List(o)))
        case None =>
          Ok(Json.toJson(ErrorResponse("后台配置查询错误。(No such configuration)")))
      }

  }

  def turnOn(kg: Long) = IsPrincipal(parse.json) {
    u => request =>
      Logger.info(request.body.toString())
      request.body.validate[ConfigItem].map {
        case (config) if config.name == configKey =>
          SchoolConfig.addConfig(kg, config)
          Ok(Json.toJson(new SuccessResponse))
        case _ =>
          Ok(Json.toJson(ErrorResponse("没有这个配置项。(No such configuration)", 10)))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def turnOff(kg: Long) = IsPrincipal {
    u => _ =>
      SchoolConfig.addConfig(kg, ConfigItem(configKey, "false"))
      Ok(Json.toJson(new SuccessResponse))
  }
}
