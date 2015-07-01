package controllers.V4

import controllers.Secured
import models.{SuccessResponse, ErrorResponse}
import models.V4.KulebaoAgent
import play.api.libs.json.{JsError, Json}
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

  def index(from: Option[Long], to: Option[Long], most: Option[Int]) = IsAgentLoggedIn { u => _ =>
    Ok(Json.toJson(KulebaoAgent.index(from, to, most)))
  }

  def create = IsAgentLoggedIn(parse.json) { u => request =>
    request.body.validate[KulebaoAgent].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) if s.name.isEmpty =>
        BadRequest(Json.toJson(ErrorResponse("请提供代理商名称(please provide name of the agent)", 4)))
      case (s) =>
        Ok(Json.toJson(s.create))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(id: Long) = IsAgentLoggedIn(parse.json) { u => request =>
    request.body.validate[KulebaoAgent].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(id: Long) = IsAgentLoggedIn { u => _ =>
    KulebaoAgent.deleteById(id)
    Ok(Json.toJson(new SuccessResponse()))
  }
}
