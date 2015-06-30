package controllers.V4

import controllers.Secured
import models.ErrorResponse
import models.V4.KulebaoAgent
import play.api.libs.json.Json
import play.api.mvc.Controller

object AgentController extends Controller with Secured {
  def show(id: Long) = IsAgentLoggedIn {
    u => _ =>
      KulebaoAgent.show(id) match {
        case Some(x) =>
          Ok(Json.toJson(x))
        case None =>
          NotFound(Json.toJson(ErrorResponse("没有找到对应代理商。(No such agent)")))
      }
  }
}
