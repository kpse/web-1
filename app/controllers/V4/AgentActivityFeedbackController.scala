package controllers.V4

import controllers.Secured
import models.V4.{AgentActivityFeedback, AgentRawActivity}
import models.{ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object AgentActivityFeedbackController extends Controller with Secured {
  def index(agentId: Long, activityId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsAgentLoggedIn { u => _ =>
    Ok(Json.toJson(AgentActivityFeedback.index(agentId, activityId, from, to, most)))
  }

  def create(kg: Long, activityId: Long) = IsLoggedIn(parse.json) { u => request =>
    request.body.validate[AgentActivityFeedback].map {
      case (s) if s.id.isDefined =>
        BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
      case (s) =>
        Ok(Json.toJson(s.create(kg, activityId)))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

}
