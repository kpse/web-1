package models

import models.CheatCode.validate
import models.V5.InvitationCode
import org.joda.time.DateTime
import play.api.Logger
import play.cache.Cache

import scala.util.Random

case class Verification(phone: String, code: String) {
  def toCodeVerification = InvitationCode(phone, code, System.currentTimeMillis)
}

object Verification {
  private val logger: Logger = Logger(classOf[Verification])
  def isMatched(verification: Verification) = {
    verification.code.equals(Cache.get(verification.phone)) || validate(verification.code)
  }

  def formatMessage(phone: String, code: String)(provider: SMSProvider): String = {
    provider.template().format(code, new DateTime(System.currentTimeMillis).toString("HH:mm:ss"))
  }

  def generatePair(phone: String) = {
    val random = new Random(System.currentTimeMillis)
    val code = "%06d".format(random.nextInt(999999))
    logger.info(phone + "'s code : " + code)
    Cache.set(phone, code, 120)
    code
  }

  def generate(phone: String)(implicit provider: SMSProvider): String = {
    val userName = provider.username()
    val secretKey = provider.password()
    val code = generatePair(phone)
    val pair = formatMessage(phone, code)(provider)
    provider.render(userName, secretKey, phone, pair)
  }
}
