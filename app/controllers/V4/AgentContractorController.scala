package controllers.V4

import controllers.Secured
import models.V4.{AgentContractor, AgentSchool}
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object AgentContractorController extends Controller with Secured {
  def show(agentId: Long, id: Long) = IsAgentLoggedIn {
    u => _ =>
      AgentContractor.show(id, agentId) match {
        case Some(x) =>
          Ok(Json.toJson(x))
        case None =>
          NotFound(Json.toJson(ErrorResponse("没有找到对应代理商广告。(No such agent advertisement)")))
      }
  }

  def index(agentId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsAgentLoggedIn { u => _ =>
    Ok(Json.toJson(AgentContractor.index(agentId, from, to, most)))
  }

  def create(agentId: Long) = IsAgentLoggedIn(parse.json) { u => request =>
    request.body.validate[AgentContractor].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(agentId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(agentId: Long, id: Long) = IsAgentLoggedIn(parse.json) { u => request =>
    request.body.validate[AgentContractor].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(agentId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(agentId: Long, id: Long) = IsAgentLoggedIn { u => _ =>
    AgentContractor.deleteById(id, agentId)
    Ok(Json.toJson(new SuccessResponse()))
  }

  def preview(agentId: Long, id: Long) = IsAgentLoggedIn(parse.json) { u => request =>
    request.body.validate[AgentContractor].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.preview(agentId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def publish(agentId: Long, id: Long) = IsOperator(parse.json) { u => request =>
    request.body.validate[AgentContractor].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.publish(agentId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def reject(agentId: Long, id: Long) = IsAgentLoggedIn(parse.json) { u => request =>
    request.body.validate[AgentContractor].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.reject(agentId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def active(agentId: Long, id: Long) = IsAgentLoggedIn(parse.json) { u => request =>
    request.body.validate[AgentContractor].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.active(agentId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }


  def deactive(agentId: Long, id: Long) = IsAgentLoggedIn(parse.json) { u => request =>
    request.body.validate[AgentContractor].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.deactive(agentId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

}

