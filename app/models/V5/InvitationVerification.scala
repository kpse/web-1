package models.V5

import models.CheatCode.validate
import models.{SMSProvider, Verification}
import play.api.Logger
import play.api.cache.Cache

import scala.util.Random
import play.api.Play.current

case class InvitationPhoneKey(phone: String) {
  val iKey = s"${phone}_invitation_code"
}

case class InvitationCode(phone: String, code: String, created_at: Long) {
  def isMatch(another: Option[InvitationCode]): Boolean = another.fold(false) { input => phone == input.phone && code == input.code }
  def toPayload(phone: String)(implicit provider: SMSProvider) = {
    val userName = provider.username()
    val secretKey = provider.password()
    val pair = formatMessage(phone)(provider)
    provider.render(userName, secretKey, phone, pair)
  }

  def formatMessage(phone: String)(provider: SMSProvider): String = {
    "验证码：%s，%s请您加入幼乐宝，共同关注%s的精彩瞬间。请将此验证码20分钟内转告给%s，欢迎搜索微信公众号“幼乐宝”。点击“软件下载”安装使用。".format(code, "老王", "小王", "老王")
  }
}


object InvitationCode {
  private val logger: Logger = Logger(classOf[InvitationCode])

  implicit def stringToInvitationPhoneKey(phone: String): InvitationPhoneKey = InvitationPhoneKey(phone)

  def isMatched(verification: Verification) = {
    verification.toCodeVerification.isMatch(Cache.getAs[InvitationCode](verification.phone.iKey)) || validate(verification.code)
  }

  def newCodeFromPhoneNumber(phone: String) = {
    val random = new Random(System.currentTimeMillis)
    val code = "%06d".format(random.nextInt(999999))
    logger.info(phone + "'s code : " + code)
    val invitationCode: InvitationCode = InvitationCode(phone, code, System.currentTimeMillis)
    Cache.set(phone.iKey, invitationCode, 12 * 60)
    invitationCode
  }

  def generate(phone: String)(implicit provider: SMSProvider) = {
    val code = newCodeFromPhoneNumber(phone)
    code.toPayload(phone)(provider)
  }
}
