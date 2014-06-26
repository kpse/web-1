package models

import play.api.Play.current
import java.util.Properties
import net.tanesha.recaptcha.{ReCaptchaImpl, ReCaptchaFactory}

object ReCaptcha {
  def publicKey(): String = {
    current.configuration.getString("recaptcha.publickey").getOrElse("")
  }

  def privateKey(): String = {
    current.configuration.getString("recaptcha.privatekey").getOrElse("")
  }

  def render(): String = {
    ReCaptchaFactory.newReCaptcha(publicKey(), privateKey(), false).createRecaptchaHtml(null, new Properties)
  }

  def check(addr: String, challenge: String, response: String): Boolean = privateKey() match {
    case key if key.length > 0 =>
      val reCaptcha = new ReCaptchaImpl()
      reCaptcha.setPrivateKey(privateKey())
      reCaptcha.checkAnswer(addr, challenge, response).isValid
    case _ => true
  }

}
