package controllers

import play.api.mvc._
import helper.JsonLogger._
import models.{SuccessResponse, ErrorResponse, CheatCode}
import play.cache.Cache
import play.api.libs.json.Json

object CheatingController extends Controller with Secured {
  implicit val reads = Json.reads[CheatCode]
  implicit val writes = Json.writes[CheatCode]
  implicit val writes2 = Json.writes[ErrorResponse]
  implicit val writes3 = Json.writes[SuccessResponse]

  val cheatCodeKey = "verification"

  def show = OperatorPage {
    username =>
      _ =>
        Cache.get(cheatCodeKey) match {
          case code: String =>
            Ok(loggedJson(new CheatCode(code)))
          case _ =>
            Ok(loggedJson(new CheatCode("")))
        }

  }

  def delete = OperatorPage {
    username =>
      _ =>
        Cache.remove(cheatCodeKey)
        Ok(loggedJson(new SuccessResponse))

  }

  def create = OperatorPage(parse.json) {
    username =>
      request =>
        request.body.validate[CheatCode].map {
          case (code) if code.code.length == 6 =>
            Cache.set(cheatCodeKey, code.code)
            Ok(loggedJson(code))
          case _ =>
            BadRequest(loggedJson(ErrorResponse("验证码必须是6位数字。")))
        }.recoverTotal {
          e => BadRequest("Detected error:" + loggedErrorJson(e))
        }
  }


}