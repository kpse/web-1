package controllers

import play.api.mvc.{WebSocket, Controller}
import models.ChatRoom
import play.api.libs.json.JsValue

object ChatController extends Controller with Secured {

  def chat(username: String) = WebSocket.async[JsValue] { request =>
    ChatRoom.join(username)
  }
}
