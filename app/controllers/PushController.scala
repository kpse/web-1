package controllers

import com.baidu.yun.core.log.{YunLogEvent, YunLogHandler}
import com.baidu.yun.push.auth.PushKeyPair
import com.baidu.yun.push.client.BaiduPushClient
import com.baidu.yun.push.constants.BaiduPushConstants
import com.baidu.yun.push.exception.{PushClientException, PushServerException}
import com.baidu.yun.push.model.{PushMsgToSingleDeviceRequest, PushMsgToSingleDeviceResponse, PushResponse}
import models.V3.Relative
import models._
import models.json_models.{CheckInfo, CheckNotification, EmployeeCheckInfo, IOSField}
import play.Logger
import play.api.Play
import play.api.libs.json.{JsError, Json}
import play.api.mvc._

object PushController extends Controller {
  implicit val write = Json.writes[IOSField]
  implicit val write1 = Json.writes[CheckNotification]
  implicit val write3 = Json.writes[ErrorResponse]
  implicit val write4 = Json.writes[SuccessResponse]
  implicit val readCheckInfo = Json.reads[CheckInfo]
  implicit val readEmployeeCheckInfo = Json.reads[EmployeeCheckInfo]

  def test = Action {
    val msg = CheckNotification(System.currentTimeMillis, 1, "1_93740362_374", "925387477040814447", "123", "", "袋鼠", 3, None)
    Ok(runWithLog(msg, triggerSinglePush))
  }

  type Trigger[A] = (A) => PushResponse

  def runWithLog[A](msg: A, trigger: Trigger[A]) = {
    try {
      trigger(msg)
      Json.toJson(new SuccessResponse)
    }
    catch {
      case e: PushClientException =>
        Json.toJson(ErrorResponse(e.printStackTrace().toString))
      case e: PushServerException =>
        val error = s"request_id: ${e.getRequestId}, error_code: ${e.getErrorCode}, error_message: ${e.getErrorMsg}"
        Logger.info(error)
        Json.toJson(ErrorResponse(error))
    }
  }


  def deployStatus: Integer = {
    // DeployStatus => 1: Developer 2: Production
    Play.current.configuration.getString("ios.deployment") match {
      case Some("prod") => new Integer(2)
      case _ => new Integer(1)
    }
  }

  def messageType(device: Int): Integer = {
    // ios is using notification => 1
    // android is using message => 0
    device match {
      case 3 => new Integer(0)
      case 4 => new Integer(1)
    }
  }


  def triggerSinglePush(check: CheckNotification) = {
    val channelClient = getClient
    val request: PushMsgToSingleDeviceRequest = new PushMsgToSingleDeviceRequest
    //device_type => 1: web 2: pc 3:android 4:ios 5:wp
    request.setDeviceType(new Integer(check.device))
    request.setDeployStatus(deployStatus)
    request.addChannelId(check.channelid)
    request.setMessageType(messageType(check.device))
    Logger.info(Json.toJson(check).toString())
    request.setMessage(Json.toJson(check).toString())
    val response: PushMsgToSingleDeviceResponse = channelClient.pushMsgToSingleDevice(request)
    Logger.info(s"push result msgId: ${response.getMsgId()},sendTime: ${response.getSendTime()}")
    response
  }

  def getClient = {

    val apiKey = Play.current.configuration.getString("push.ak").getOrElse("")
    val secretKey = Play.current.configuration.getString("push.sk").getOrElse("")

    val pair: PushKeyPair = new PushKeyPair(apiKey, secretKey)
    val channelClient = new BaiduPushClient(pair, BaiduPushConstants.CHANNEL_REST_URL)

    channelClient.setChannelLogHandler(new YunLogHandler {
      def onHandle(event: YunLogEvent) {
        Logger.info(event.getMessage)
      }
    })
    channelClient
  }

  def createSwipeMessage(check: CheckNotification) = check.channelid match {
    case "0" =>
      Logger.info("PushController: channel id is a invalid zero")
    case p if p.length > 0 =>
      runWithLog(check, triggerSinglePush)
    case _ =>
      Logger.info("PushController: No channel id available.")
  }

  private def individualSmsEnabled(schoolId: Long, phone: Option[String]): Boolean = phone.isDefined && Relative.findByPhone(schoolId, phone.get).exists(_.ext.exists(_.sms_push == Some(true)))

  private def schoolSmsEnabled(schoolId: Long) = {
    SchoolConfig.valueOfKey(schoolId, "smsPushAccount").exists(_.nonEmpty) &&
      SchoolConfig.valueOfKey(schoolId, "smsPushPassword").exists(_.nonEmpty) &&
      SchoolConfig.valueOfKey(schoolId, "switch_sms_on_card_wiped").exists(_.equalsIgnoreCase("1"))
  }
  def smsPushEnabled(check: CheckInfo, message: CheckNotification) = schoolSmsEnabled(check.school_id) && individualSmsEnabled(check.school_id, message.phone)


  def sendSmsInstead(check: CheckInfo, message: CheckNotification) = {
    val provider: SMSProvider = new Mb365SMS {
      override def url() = "http://mb345.com:999/ws/LinkWS.asmx/Send2"

      override def username() = SchoolConfig.valueOfKey(check.school_id, "smsPushAccount") getOrElse ""

      override def password() = SchoolConfig.valueOfKey(check.school_id, "smsPushPassword") getOrElse ""

      override def template(): String = s"${message.aps.get.alert}【幼乐宝】"
    }
    message.phone map {
      p =>
        val smsReq: String = Verification.generate(p)(provider)
        Logger.info(s"smsReq = $smsReq")
        SMSController.sendSMS(smsReq)(provider)
    }
  }


  def forwardSwipe(kg: Long) = Action(parse.json) {
    request =>
      Logger.info("checking : " + request.body)
      request.body.validate[CheckInfo].map {
        case (check) =>
          val messages = check.create
          Logger.info("messages : " + messages)
          messages map {
            case m if smsPushEnabled(check, m) =>
              sendSmsInstead(check, m)
            case m =>
              createSwipeMessage(m)
          }
          Ok(Json.toJson(new SuccessResponse))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def saveOnly(kg: Long) = Action(parse.json) {
    request =>
      Logger.info("checking history: " + request.body)
      request.body.validate[List[CheckInfo]].map {
        case (all) =>
          all foreach (_.create)
          Ok(Json.toJson(new SuccessResponse))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def employeeCheckInOut(kg: Long) = Action(parse.json) {
    request =>
      Logger.info("checking : " + request.body)
      request.body.validate[EmployeeCheckInfo].map {
        case (check) if check.school_id != kg =>
          BadRequest(Json.toJson(new ErrorResponse("School id is not matching")))
        case (check) =>
          check.create
          Ok(Json.toJson(new SuccessResponse))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}
