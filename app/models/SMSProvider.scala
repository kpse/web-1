package models

import models.helper.MD5Helper._
import org.joda.time.DateTime
import play.Logger
import play.api.Play

import scala.xml.Elem

trait SMSProvider {
  def url(): String

  def username(): String

  def password(): String

  def result(response: Elem): Boolean

  def template(): String

  def render(userName: String, password: String, mobile: String, content: String): String
}

class HuyiSMS extends SMSProvider {
  def url() = Play.current.configuration.getString("sms2.ws.url").getOrElse("")

  def username() = Play.current.configuration.getString("sms2.username").getOrElse("")

  def password() = Play.current.configuration.getString("sms2.password").getOrElse("")

  def result(response: Elem) = (response \ "code" map (_.text.toLong)) == List(2)

  def template(): String = "验证码：(%s)，切勿泄露给他人，如非本人操作，建议修改账号密码。自%s起，有效期2分钟。"

  def render(userName: String, password: String, mobile: String, content: String): String = {
    Map("account" -> Seq(userName), "password" -> Seq(password), "mobile" -> Seq(mobile),
      "content" -> Seq(content)).toList.map(p => s"${p._1}=${urlEncode(p._2.mkString(","))}").mkString("&")
  }
}

class AliDayu extends SMSProvider {
  def url() = Play.current.configuration.getString("sms3.ws.url").getOrElse("")

  def username() = Play.current.configuration.getString("sms3.username").getOrElse("")

  def password() = Play.current.configuration.getString("sms3.password").getOrElse("")

  def result(response: Elem) = {
    val code = (response \ "result" \ "err_code").text
    code.nonEmpty && code.toLong == 0
  }

  def template(): String = "%s"

  def render(ak: String, sk: String, mobile: String, content: String): String = {
    val timestamp = DateTime.now.toString("yyyy-MM-dd HH:mm:ss")
    val payload = List("app_key" -> ak, "format" -> "xml", "method" -> "alibaba.aliqin.fc.sms.num.send", "partner_id" -> "apidoc",
      "sign_method" -> "md5", "timestamp" -> timestamp, "v" -> "2.0", "rec_num" -> mobile, "sms_free_sign_name" -> "幼乐宝",
      "sms_param" -> s"""{"code":"$content", "time":"$timestamp"}""", "sms_type" -> "normal", "sms_template_code" -> "SMS_3105364")
    val sign = md5(s"$sk${payload.sortWith(_._1 < _._1).map(p => s"${p._1}${p._2}").mkString}$sk")
    val debug = payload ++ List("sign" -> sign)
    Logger.debug(s"payload = $debug")
    debug.map(p => s"${p._1}=${urlEncode(p._2)}").mkString("&")
  }

}

class Mb365SMS extends SMSProvider {
  def url() = Play.current.configuration.getString("sms.ws.url").getOrElse("")

  def username() = Play.current.configuration.getString("sms.username").getOrElse("")

  def password() = Play.current.configuration.getString("sms.password").getOrElse("")

  def result(response: Elem) = response map (_.text.toLong) match {
    case List(num) if num > 0 => true
    case _ => false
  }

  def template(): String = "验证码：(%s)，切勿泄露给他人，如非本人操作，建议修改账号密码。自%s起，有效期2分钟。【幼乐宝】"

  def render(userName: String, password: String, mobile: String, content: String): String = {
    Map("CorpID" -> Seq(userName), "Pwd" -> Seq(password), "Mobile" -> Seq(mobile),
      "Content" -> Seq(content), "Cell" -> Seq(""), "SendTime" -> Seq("")).toList.map(p => s"${p._1}=${urlEncode(p._2.mkString(","))}").mkString("&")
  }
}

object SMSProvider {
  def create : SMSProvider = Play.current.configuration.getString("sms.channel") match {
    case Some("2") => new HuyiSMS
    case Some("3") => new AliDayu
    case _ => new Mb365SMS

  }
}
