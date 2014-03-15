package controllers

import play.api.mvc.{SimpleResult, Action, Controller}
import models._
import play.api.libs.ws.WS
import play.Logger
import play.api.libs.json.{JsError, Json}
import play.cache.Cache
import scala.concurrent.{Future, Promise}

object SMSController extends Controller {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global
  implicit val write1 = Json.writes[SuccessResponse]
  implicit val write2 = Json.writes[ErrorResponse]
  implicit val read = Json.reads[Verification]

  def floodProtect(phone: String) = Cache.get(phone)

  def sendVerificationCode(phone: String) = Action.async {
    implicit request =>
      floodProtect(phone) match {
        case (code: String) =>
          Future.successful(BadRequest("请求太频繁。"))
        case null =>
          sendSMS(phone)
        case _ =>
          Future.successful(BadRequest("短信发送出错。"))
      }

  }


  def sendSMS(phone: String): Future[SimpleResult] = {

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
