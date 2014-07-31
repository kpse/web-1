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
    val m = Map(
      "e6f7681249d77e4e69c69fe7866f532b" -> "damage",
      "d08b787a9bfd5a25626ce28e422de506" -> "fertile",
      "7787466eff202c14b0055d79c62d6361" -> "chest",
      "bb76efb2453eb79acd5d494d285aca1c" -> "wound",
      "40997ced00f423dbfb431cbdb36fb541" -> "chief",
      "250b6df5a00e6de73db95ba7ab8c0914" -> "church",
      "a3e44c48f46b73d2097b7ada42f4b439" -> "paper",
      "b3ffebe131a983b21270f72ecbd027fa" -> "sudden",
      "3afb060d24982bda4715f9b2508f4aea" -> "stick",
      "1f851d3407be69a868a7063b4a75cd7f" -> "letter",
      "d126f90c1d24d8374b6362aeb887f1fa" -> "twist",
      "f0a94aa51d1359037c12d1f7d3dc623f" -> "slope",
      "b1bc362612130bfc14b315d6dfdb0e3d" -> "ready",
      "97a2d30e09db519fa15316291f145008" -> "military",
      "fced89169747395b75103c3a613bbb50" -> "hanging"
    )
    answer.nonEmpty && answer.equalsIgnoreCase(m.getOrElse(challenge, ""))
  }
}
