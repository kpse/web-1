package controllers

import play.api.mvc.{Action, Controller}

object WeiXinController extends Controller {
  def entry(signature: String, timestamp: Long, nonce: String, echo: String) = Action {
    Ok(echo)
  }
}
