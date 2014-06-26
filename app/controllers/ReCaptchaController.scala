package controllers

import play.api.mvc.{Action, Controller}
import models.ReCaptcha

object ReCaptchaController extends Controller {
  def captcha = Action {
    Ok(ReCaptcha.render())
  }

  def captchaView = Action {
    Ok(views.html.captcha())
  }
}
