package models

import play.api.Play
import org.joda.time.DateTime
import java.net.URLEncoder

case class Verification(phone: String, code: String)

object Verification {

  def makeCodePair(phone: String): String = {
    URLEncoder.encode("验证码： %d，切勿泄露给他人，如非本人操作，建议修改账号密码。%s【幼乐宝】".format(9902, new DateTime(System.currentTimeMillis).toString("yyyy-MM-dd")), "gb2312")
  }

  def generate(phone: String) = {
    val userName = Play.current.configuration.getString("sms.username").getOrElse("")
    val secretKey = Play.current.configuration.getString("sms.password").getOrElse("")
    Map("CorpID" -> Seq(userName), "Pwd" -> Seq(secretKey), "Mobile" -> Seq(phone),
      "Content" -> Seq(makeCodePair(phone)), "Cell" -> Seq(""), "SendTime" -> Seq(""))
  }

}
