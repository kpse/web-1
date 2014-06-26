package models

import play.api.Play.current
import java.util.Properties
import net.tanesha.recaptcha.ReCaptchaFactory

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

}
