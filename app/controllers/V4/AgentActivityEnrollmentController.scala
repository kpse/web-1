package controllers.V4

import controllers.Secured
import models.V4.{AgentActivityEnrollment, AgentRawActivity}
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object AgentActivityEnrollmentController extends Controller with Secured {
  def index(agentId: Long, activityId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsAgentLoggedIn { u => _ =>
    Ok(Json.toJson(AgentActivityEnrollment.index(agentId, activityId, from, to, most)))
  }

  def create(kg: Long, activityId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[AgentActivityEnrollment].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) if AgentActivityEnrollment.show(kg, activityId, s.parent_id).nonEmpty =>
        BadRequest(Json.toJson(ErrorResponse("不能重复报名同一活动。(duplicated enrollment)", 3)))
      case (s) =>
        s.create(kg, activityId)
        Ok(Json.toJson(new SuccessResponse()))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def show(kg: Long, activityId: Long, parentId: String) = IsLoggedIn { u => _ =>
    AgentActivityEnrollment.show(kg, activityId, parentId) match {
      case Some(x) =>
        Ok(Json.toJson(x))
      case None =>
        NotFound(Json.toJson(ErrorResponse(s"${parentId}没有报名活动$activityId。(No such enrollment)")))
    }

  }
}
