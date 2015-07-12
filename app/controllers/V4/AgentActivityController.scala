package controllers.V4

import controllers.Secured
import models.V4.AgentActivity
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object AgentActivityController extends Controller with Secured {
  def show(agentId: Long, contractorId: Long, id: Long) = IsAgentLoggedIn {
    u => _ =>
      AgentActivity.show(id, agentId, contractorId) match {
        case Some(x) =>
          Ok(Json.toJson(x))
        case None =>
          NotFound(Json.toJson(ErrorResponse("没有找到对应代理商广告。(No such agent advertisement)")))
      }
  }

  def index(agentId: Long, contractorId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsAgentLoggedIn { u => _ =>
    Ok(Json.toJson(AgentActivity.index(agentId, contractorId, from, to, most)))
  }

  def create(agentId: Long, contractorId: Long) = IsAgentLoggedIn(parse.json) { u => request =>
    request.body.validate[AgentActivity].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(agentId, contractorId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(agentId: Long, contractorId: Long, id: Long) = IsAgentLoggedIn(parse.json) { u => request =>
    request.body.validate[AgentActivity].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(agentId, contractorId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(agentId: Long, contractorId: Long, id: Long) = IsAgentLoggedIn { u => _ =>
    AgentActivity.deleteById(id, agentId, contractorId)
    Ok(Json.toJson(new SuccessResponse()))
  }

  def preview(agentId: Long, contractorId: Long, id: Long) = IsAgentLoggedIn(parse.json) { u => request =>
    request.body.validate[AgentActivity].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.preview(agentId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def publish(agentId: Long, contractorId: Long, id: Long) = IsOperator(parse.json) { u => request =>
    request.body.validate[AgentActivity].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.publish(agentId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def reject(agentId: Long, contractorId: Long, id: Long) = IsAgentLoggedIn(parse.json) { u => request =>
    request.body.validate[AgentActivity].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.reject(agentId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }
}
