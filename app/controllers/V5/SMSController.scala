package controllers.V5

import controllers.SMSController.sendSMS
import controllers.Secured
import models.V5.{InvitationCode, InvitationPhoneKey, InvitationPhonePair}
import models._
import models.V5.InvitationCode.readInvitationPhonePair
import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}
import play.cache.Cache

import scala.concurrent.Future

object SMSController extends Controller with Secured {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global
  implicit val write1 = Json.writes[SuccessResponse]
  implicit val write2 = Json.writes[ErrorResponse]

  implicit def stringToInvitationPhoneKey(phone: String): InvitationPhoneKey = InvitationPhoneKey(phone)

  def floodProtect(phone: String) = Cache.get(phone.iKey)

  private val logger: Logger = Logger(classOf[InvitationPhonePair])

  def send(phone: String) = Action.async(parse.json) {
    implicit request =>
      logger.info(request.body.toString())
      implicit val provider: SMSProvider = new HuyiSMS
      request.body.validate[InvitationPhonePair].map {
        case (invitation) if invitation.invitee != phone =>
          Future.successful(BadRequest(Json.toJson(ErrorResponse("请将短信发送给被邀请人。(invitee phone number is not matched to uri)", 7))))
        case (invitation) if Parent.phoneSearch(phone).exists(p => p.status == Some(1)) =>
          Future.successful(BadRequest(Json.toJson(ErrorResponse("被邀请人已经在幼乐宝注册。(invitee phone number has already registered)", 8))))
        case (invitation) =>
          floodProtect(phone) match {
            case InvitationCode(_, _, time, _) if System.currentTimeMillis - time < 120 * 1000 =>
              Future.successful(BadRequest(Json.toJson(ErrorResponse("请求太频繁，目前限制为2分钟。(too frequently requests, current is 2 minutes)"))))
            case InvitationCode(p, c, t, parent) =>
              logger.info(s"resend invitation : ${InvitationCode(p, c, t, parent)}")
              sendSMS(InvitationCode(p, c, System.currentTimeMillis(), parent).toPayload())
            case null =>
              Parent.phoneSearch(invitation.host.phone) match {
                case Some(parent) if parent.status == Some(1) =>
                  val smsReq = InvitationCode.generate(phone, parent)
                  logger.info(s"smsReq = $smsReq")
                  sendSMS(smsReq)
                case _ =>
                  Future.successful(BadRequest(Json.toJson(ErrorResponse(s"未找到此手机号${phone}的注册信息。(unregistered phone number)", 6))))
              }
            case _ =>
              Future.successful(BadRequest(Json.toJson(ErrorResponse("短信发送出错。(other errors in sending SMS, contact admin please)", 3))))
          }
      }.recoverTotal {
        e => Future.successful(BadRequest("Detected error:" + JsError.toFlatJson(e)))
      }

  }
}
