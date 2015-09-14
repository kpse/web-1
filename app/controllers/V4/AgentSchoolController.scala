package controllers.V4

import controllers.Secured
import models.Charge.writeActiveCount
import models.V4.AgentSchool
import models.{Charge, ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object AgentSchoolController extends Controller with Secured {
   def show(agentId: Long, id: Long) = IsAgentLoggedIn {
     u => _ =>
       AgentSchool.show(id, agentId) match {
         case Some(x) =>
           Ok(Json.toJson(x))
         case None =>
           NotFound(Json.toJson(ErrorResponse("没有找到对应代理商下辖学校。(No such agent school)")))
       }
   }

   def index(agentId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = IsAgentLoggedIn { u => _ =>
     Ok(Json.toJson(AgentSchool.index(agentId, from, to, most)))
   }

   def create(agentId: Long) = IsAgentLoggedIn(parse.json) { u => request =>
     request.body.validate[AgentSchool].map {
       case (s) if s.id.isDefined =>
         BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
       case (s) if s.connected =>
         BadRequest(Json.toJson(ErrorResponse("该学校已经被关联(the school has been connected to agent already)", 5)))
       case (s) =>
         Ok(Json.toJson(s.create(agentId)))
     }.recoverTotal {
       e => BadRequest("Detected error:" + JsError.toFlatJson(e))
     }
   }

   def update(agentId: Long, id: Long) = IsAgentLoggedIn(parse.json) { u => request =>
     request.body.validate[AgentSchool].map {
       case (s) if s.id != Some(id) =>
         BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
       case (s) =>
         Ok(Json.toJson(s.update(agentId)))
     }.recoverTotal {
       e => BadRequest("Detected error:" + JsError.toFlatJson(e))
     }
   }

   def delete(agentId: Long, id: Long) = IsAgentLoggedIn { u => _ =>
     AgentSchool.deleteById(id, agentId)
     Ok(Json.toJson(new SuccessResponse()))
   }

  def schoolData(agentId: Long, kg: Long) = IsAgentLoggedIn { u => _ =>
    AgentSchool.exists(agentId, kg) match {
      case true =>
        Ok(Json.toJson(Charge.countActivePhones(kg)))
      case false =>
        NotFound(Json.toJson(ErrorResponse("权限不足(insufficient privilege)", 4)))
    }

  }
 }
