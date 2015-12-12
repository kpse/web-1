package controllers

import models._
import org.joda.time.DateTime
import play.Logger
import play.api.libs.json.{JsError, Json}
import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller, SimpleResult}
import play.cache.Cache
import models.helper.MD5Helper.{md5, urlEncode}

import scala.concurrent.Future

object SMSController extends Controller with Secured {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global
  implicit val write1 = Json.writes[SuccessResponse]
  implicit val write2 = Json.writes[ErrorResponse]
  implicit val read = Json.reads[Verification]

  //special cellphone number without verification
  Cache.set("12212345678", "968483")

  def floodProtect(phone: String) = Cache.get(phone)

  def sendVerificationCode(phone: String) = Action.async {
    implicit request =>
      floodProtect(phone) match {
        case code if phone.equals("12212345678") =>
          Future.successful(Ok(Json.toJson(new SuccessResponse)))
        case (code: String) =>
          Future.successful(BadRequest(Json.toJson(ErrorResponse("请求太频繁，目前限制为2分钟。(too frequently requests, current is 2 minutes)"))))
        case null =>
          val provider: SMSProvider = new AliDayu
          val smsReq: String = Verification.generate(phone)(provider)
          Logger.info(s"smsReq = $smsReq")
          sendSMS(smsReq)(provider)
        case _ =>
          Future.successful(BadRequest(Json.toJson(ErrorResponse("短信发送出错。(other errors in sending SMS, contact admin please)", 3))))
      }

  }

  def sendSMS(generate: String)(implicit provider: SMSProvider): Future[SimpleResult] = {
    WS.url(provider.url()).withHeaders("Content-Type" -> "application/x-www-form-urlencoded;charset=UTF-8")
      .post(generate).map {
      response =>
        Logger.info("server returns: " + response.xml.toString)
        provider.result(response.xml) match {
          case true =>
            Ok(Json.toJson(new SuccessResponse))
          case false =>
            Ok(Json.toJson(ErrorResponse("验证码发送失败。(sending error from sms server side)", 4)))
        }
    }
  }

  def verify(phone: String) = Action(parse.json) {
    implicit request =>
      request.body.validate[Verification].map {
        case (v) =>
          Logger.info(v.toString)
          Verification.isMatched(v) match {
            case verify if verify && v.phone.equals("12212345678") =>
              Ok(Json.toJson(new SuccessResponse))
            case true =>
              Cache.remove(phone)
              Ok(Json.toJson(new SuccessResponse))
            case false => Ok(Json.toJson(ErrorResponse("验证码校验失败。(incorrect code)", 5)))
          }
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }

  }

  def checkVerificationCode = IsOperator {
    u => _ =>
      Ok(Json.toJson(List[String]()))
  }
}
