package models

import play.api.Play

import scala.xml.Elem

trait SMSProvider {
  def url(): String

  def username(): String

  def password(): String

  def result(response: Elem): Boolean

  def template(): String

  def render(userName: String, password: String, mobile: String, content: String): Map[String, Seq[String]]
}

class HuyiSMS extends SMSProvider {
  def url() = Play.current.configuration.getString("sms2.ws.url").getOrElse("")

  def username() = Play.current.configuration.getString("sms2.username").getOrElse("")

  def password() = Play.current.configuration.getString("sms2.password").getOrElse("")

  def result(response: Elem) = (response \ "code" map (_.text.toLong)) == List(2)

  def template(): String = "验证码：(%s)，切勿泄露给他人，如非本人操作，建议修改账号密码。自%s起，有效期2分钟。"

  def render(userName: String, password: String, mobile: String, content: String): Map[String, Seq[String]] = {
    Map("account" -> Seq(userName), "password" -> Seq(password), "mobile" -> Seq(mobile),
      "content" -> Seq(content))
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

  def render(userName: String, password: String, mobile: String, content: String): Map[String, Seq[String]] = {
    Map("CorpID" -> Seq(userName), "Pwd" -> Seq(password), "Mobile" -> Seq(mobile),
      "Content" -> Seq(content), "Cell" -> Seq(""), "SendTime" -> Seq(""))
  }
}

object SMSProvider {
  def create : SMSProvider = Play.current.configuration.getString("sms.channel") match {
    case Some("2") => new HuyiSMS
    case _ => new Mb365SMS

  }
}
