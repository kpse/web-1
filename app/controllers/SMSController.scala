package controllers

import play.api.mvc.{Action, Controller}
import models._
import play.api.libs.ws.WS
import play.Logger
import play.api.libs.json.{JsError, Json}
import play.cache.Cache

object SMSController extends Controller {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def sendVerificationCode(phone: String) = Action.async {
    val url = "http://mb345.com:999/ws/LinkWS.asmx/Send2"
    val generate = Verification.generate(phone)
    Logger.info(generate.toString)
    WS.url(url).withHeaders("Content-Type" -> "application/x-www-form-urlencoded;charset=UTF-8")
      .post(generate).map {
      response =>
        Logger.info("server returns: " + response.xml.toString)
        response.xml.map(_.text.toLong) match {
          case List(num) if num > 0 =>
            Ok(Json.toJson(new SuccessResponse))
          case _ =>
            Ok(Json.toJson(new ErrorResponse("failed")))
        }
    }
  }

  implicit val write1 = Json.writes[JsonResponse]
  implicit val read = Json.reads[Verification]

  def verify(phone: String) = Action(parse.json) {
    implicit request =>
      request.body.validate[Verification].map {
        case (v) =>
          Verification.isMatched(v) match {
            case true =>
              Cache.set(phone, "", 0)
              Ok(Json.toJson(new SuccessResponse))
            case false => Ok(Json.toJson(new ErrorResponse("failed")))
          }
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }

  }
}
