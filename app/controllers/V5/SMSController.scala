package controllers.V5

import controllers.SMSController.sendSMS
import controllers.Secured
import models.V5.{Invitation, InvitationCode, InvitationPhoneKey}
import models._
import play.api.Logger
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

  private val logger: Logger = Logger(classOf[Verification])

  def send(phone: String) = Action.async(parse.json) {
    implicit request =>
      logger.info(request.body.toString())
      implicit val provider: SMSProvider = SMSProvider.create
      request.body.validate[Invitation].map {
        case (invitation) =>
          floodProtect(phone) match {
            case InvitationCode(_, _, time, _) if System.currentTimeMillis - time < 120 * 1000 =>
              Future.successful(BadRequest(Json.toJson(ErrorResponse("请求太频繁，目前限制为2分钟。(too frequently requests, current is 2 minutes)"))))
            case InvitationCode(p, c, t, parent) =>
              logger.info(s"resend invitation : ${InvitationCode(p, c, t, parent)}")
              sendSMS(InvitationCode(p, c, System.currentTimeMillis(), parent).toPayload())
            case null =>
              Parent.phoneSearch(invitation.from.phone) match {
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

  def verify(phone: String) = Action(parse.json) {
    implicit request =>
      request.body.validate[Verification].map {
        case (v) =>
          logger.info(v.toString)
          InvitationCode.isMatched(v) match {
            case true =>
              Cache.remove(phone.iKey)
              Ok(Json.toJson(new SuccessResponse))
            case false =>
              Ok(Json.toJson(ErrorResponse("验证码校验失败。(incorrect code)", 5)))
          }
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

}
