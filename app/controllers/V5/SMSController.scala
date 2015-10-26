package controllers.V5

import controllers.SMSController.sendSMS
import controllers.Secured
import models.V5.{InvitationCode, InvitationPhoneKey}
import models._
import play.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}
import play.cache.Cache

import scala.concurrent.Future

object SMSController extends Controller with Secured {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global
  implicit val write1 = Json.writes[SuccessResponse]
  implicit val write2 = Json.writes[ErrorResponse]
  implicit val read = Json.reads[Verification]

  implicit def stringToInvitationPhoneKey(phone: String): InvitationPhoneKey = InvitationPhoneKey(phone)

  def floodProtect(phone: String) = Cache.get(phone.iKey)

  def send(phone: String) = Action.async {
    implicit request =>
      implicit val provider: SMSProvider = SMSProvider.create
      floodProtect(phone) match {
        case InvitationCode(_, _, time) if System.currentTimeMillis - time < 120*1000  =>
          Future.successful(BadRequest(Json.toJson(ErrorResponse("请求太频繁，目前限制为2分钟。(too frequently requests, current is 2 minutes)"))))
        case InvitationCode(p, c, t) =>
          Logger.info(s"resend : ${InvitationCode(p, c, t)}")
          sendSMS(InvitationCode(p, c, System.currentTimeMillis()).toPayload(phone))
        case null =>
          val smsReq = InvitationCode.generate(phone)
          Logger.info(smsReq.toString())
          sendSMS(smsReq)
        case _ =>
          Future.successful(BadRequest(Json.toJson(ErrorResponse("短信发送出错。(other errors in sending SMS, contact admin please)", 3))))
      }

  }

  def verify(phone: String) = Action(parse.json) {
    implicit request =>
      request.body.validate[Verification].map {
        case (v) =>
          Logger.info(v.toString)
          InvitationCode.isMatched(v) match {
            case true =>
              Cache.remove(phone)
              Ok(Json.toJson(new SuccessResponse))
            case false =>
              Ok(Json.toJson(ErrorResponse("验证码校验失败。(incorrect code)", 5)))
          }
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

}
