package models.V5

import models.CheatCode.validate
import models.{Parent, SMSProvider, Verification}
import play.api.Logger
import play.api.cache.Cache
import play.api.libs.json.Json

import scala.util.Random
import play.api.Play.current

case class InvitationPhonePair(host: String, invitee: String)

case class InvitationPhoneKey(phone: String) {
  val iKey = s"${phone}_invitation_code"
}

case class InvitationCode(phone: String, code: String, created_at: Long, parent: Option[Parent] = None) {
  def isMatch(another: Option[InvitationCode]): Boolean = another.fold(false) { input => phone == input.phone && code == input.code }
  def toPayload()(implicit provider: SMSProvider) = {
    val userName = provider.username()
    val secretKey = provider.password()
    val pair = formatMessage()(provider)
    provider.render(userName, secretKey, phone, pair)
  }

  def formatMessage()(implicit provider: SMSProvider): String = {
    s"验证码：${code}，${parent.get.name}请您加入幼乐宝，共同关注孩子的精彩瞬间。请将此验证码20分钟内转告给${parent.get.name}，欢迎搜索微信公众号“幼乐宝”。点击“软件下载”安装使用。"
  }
}


object InvitationCode {
  private val logger: Logger = Logger(classOf[InvitationCode])

  implicit val readInvitationPhonePair = Json.reads[InvitationPhonePair]
  implicit val writeInvitationPhonePair = Json.writes[InvitationPhonePair]

  implicit def stringToInvitationPhoneKey(phone: String): InvitationPhoneKey = InvitationPhoneKey(phone)

  def isMatched(verification: Verification) = {
    verification.toCodeVerification.isMatch(Cache.getAs[InvitationCode](verification.phone.iKey)) || validate(verification.code)
  }

  def newCodeFromPhoneNumber(phone: String, parent: Parent) = {
    val random = new Random(System.currentTimeMillis)
    val code = "%06d".format(random.nextInt(999999))
    logger.info(phone + "'s code : " + code)
    val invitationCode: InvitationCode = InvitationCode(phone, code, System.currentTimeMillis, Some(parent))
    Cache.set(phone.iKey, invitationCode, 12 * 60)
    invitationCode
  }

  def generate(phone: String, parent: Parent)(implicit provider: SMSProvider) = {
    newCodeFromPhoneNumber(phone, parent).toPayload()(provider)
  }
}
