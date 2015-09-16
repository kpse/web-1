package models

import models.CheatCode.validate
import org.joda.time.DateTime
import play.Logger
import play.cache.Cache

import scala.util.Random

case class Verification(phone: String, code: String)

object Verification {
  def isMatched(verification: Verification) = {
    verification.code.equals(Cache.get(verification.phone)) || validate(verification.code)
  }

  def formatMessage(phone: String, code: String)(provider: SMSProvider): String = {
    provider.template().format(code, new DateTime(System.currentTimeMillis).toString("HH:mm:ss"))
  }

  def generatePair(phone: String) = {
    val random = new Random(System.currentTimeMillis)
    val code = "%06d".format(random.nextInt(999999))
    Logger.info(phone + "'s code : " + code)
    Cache.set(phone, code, 120)
    code
  }

  def generate(phone: String)(implicit provider: SMSProvider) = {
    val userName = provider.username()
    val secretKey = provider.password()
    val code = generatePair(phone)
    val pair = formatMessage(phone, code)(provider)
    provider.render(userName, secretKey, phone, pair)
  }
}
