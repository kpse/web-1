package models

import play.api.Play.current
import java.util.Properties
import net.tanesha.recaptcha.{ReCaptchaImpl, ReCaptchaFactory}
import play.Logger
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._

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
      checkAnswer(addr, challenge, response, reCaptcha)
    case _ => true
  }

  def checkAnswer(addr: String, challenge: String, response: String, reCaptcha: ReCaptchaImpl): Boolean = {
    try {
      val future: Future[Boolean] = Future {
        reCaptcha.checkAnswer(addr, challenge, response).isValid
      }
      Await result(future, 5.seconds)
    }
    catch {
      case e: Throwable =>
        Logger.info(e.getLocalizedMessage)
        true
    }
  }

  def simpleCheck(challenge: String, answer: String) = {
    val m = Map("e6f7681249d77e4e69c69fe7866f532b" -> "damage",
      "d08b787a9bfd5a25626ce28e422de506" -> "fertile",
      "fced89169747395b75103c3a613bbb50" -> "hanging")
    answer.nonEmpty && answer.equalsIgnoreCase(m.getOrElse(challenge, ""))
  }
}
