package controllers

import play.api.mvc.{Action, Controller}
import models.Verification
import play.api.libs.ws.WS
import play.Logger

object SMSController extends Controller {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def sendVerificationCode(phone: String) = Action.async {
    val url = "http://mb345.com:999/ws/LinkWS.asmx/Send2"
    val generate = Verification.generate(phone)
    Logger.info(generate.toString)
    WS.url(url).withHeaders("Content-Type" -> "application/x-www-form-urlencoded;charset=UTF-8")
      .post(generate).map {
      response =>
        Ok(response.xml)
    }
  }


  def verify(phone: String) = Action {
    Ok
  }
}
