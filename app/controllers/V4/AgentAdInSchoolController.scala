package controllers.V4

import controllers.Secured
import models.V4.{AgentSchoolAd, AgentContractor}
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object AgentAdInSchoolController extends Controller with Secured {
  def show(agentId: Long, kg: Long, id: Long) = IsAgentLoggedIn {
    u => _ =>
      AgentSchoolAd.show(id, agentId, kg) match {
        case Some(x) =>
          Ok(Json.toJson(x))
        case None =>
          NotFound(Json.toJson(ErrorResponse("没有找到对应代理商广告。(No such agent advertisement)")))
      }
  }

  def index(agentId: Long, kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsAgentLoggedIn { u => _ =>
    Ok(Json.toJson(AgentSchoolAd.index(agentId, kg, from, to, most)))
  }

  def create(agentId: Long, kg: Long) = IsAgentLoggedIn(parse.json) { u => request =>
    request.body.validate[AgentSchoolAd].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) if s.hasBeenDeleted(agentId, kg) =>
        Ok(Json.toJson(s.undoDeletion(agentId, kg)))
      case (s) =>
        Ok(Json.toJson(s.create(agentId, kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(agentId: Long, kg: Long, id: Long) = IsAgentLoggedIn(parse.json) { u => request =>
    request.body.validate[AgentSchoolAd].map {
      case (s) if s.id != Some(id) =>
        BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
      case (s) =>
        Ok(Json.toJson(s.update(agentId, kg)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(agentId: Long, kg: Long, id: Long) = IsAgentLoggedIn { u => _ =>
    AgentSchoolAd.deleteById(id, agentId, kg)
    Ok(Json.toJson(new SuccessResponse()))
  }

  def published(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsAgentLoggedIn {
    u => _ =>
      Ok(Json.toJson(AgentSchoolAd.published(kg, from, to, most)))
  }
  def publishedActivities(kg: Long, contractorId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsAgentLoggedIn {
    u => _ =>
      Ok(Json.toJson(AgentSchoolAd.published(kg, from, to, most)))
  }
}
