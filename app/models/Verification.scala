package models

import play.api.Play
import org.joda.time.DateTime
import scala.util.Random
import play.cache.Cache
import play.Logger

case class Verification(phone: String, code: String)

object Verification {
  def isMatched(verification: Verification) = {
    val code = Cache.get(verification.phone)
    if (code != null) {
      code.equals(verification.code)
    }
    else false;

  }


  def formatMessage(phone: String, code: String): String = {
    "验证码：【%s】，切勿泄露给他人，如非本人操作，建议修改账号密码。自%s起，有效期10分钟。【幼乐宝】".format(code, new DateTime(System.currentTimeMillis).toString("HH:mm:ss"))
  }

  def generatePair(phone: String) = {
    val random = new Random(System.currentTimeMillis)
    val code = "%06d".format(random.nextInt(999999))
    Logger.info(phone + "'s code : " + code)
    Cache.set(phone, code, 600)
    code
  }

  def generate(phone: String) = {
    val userName = Play.current.configuration.getString("sms.username").getOrElse("")
    val secretKey = Play.current.configuration.getString("sms.password").getOrElse("")
    val code = generatePair(phone)
    val pair = formatMessage(phone, code)
    Map("CorpID" -> Seq(userName), "Pwd" -> Seq(secretKey), "Mobile" -> Seq(phone),
      "Content" -> Seq(pair), "Cell" -> Seq(""), "SendTime" -> Seq(""))
  }
}
