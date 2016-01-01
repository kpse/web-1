package controllers.V4

import controllers.Secured
import models.V4.AgentPassword.{readAgentPassword, readAgentResetPassword}
import models.V4.AgentSchool.{writeAgentContractorInSchoolSummary, writeAgentReport}
import models.V4.KulebaoAgent.writeAgentStatistics
import models.V4._
import models.{Employee, ErrorResponse, SuccessResponse}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Controller, SimpleResult}

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

  def create = IsOperator(parse.json) { u => request =>
    request.body.validate[KulebaoAgent].map {
      idExistCheck orElse phoneCheck orElse nameCheck orElse createAgent
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def update(id: Long) = IsOperator(parse.json) { u => request =>
    request.body.validate[KulebaoAgent].map {
      idCheck(id) orElse phoneCheck orElse nameCheck orElse updateAgent
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def delete(id: Long) = IsAgentLoggedIn { u => _ =>
    KulebaoAgent.deleteById(id)
    Ok(Json.toJson(new SuccessResponse()))
  }

  def changePassword(agentId: Long) = IsAgentLoggedIn(parse.json) { u => request =>
    request.body.validate[AgentPassword].map {
      case (s) if !s.matched =>
        BadRequest(Json.toJson(ErrorResponse("提供的信息不匹配(information is incorrect)", 5)))
      case (s) =>
        Ok(Json.toJson(s.change))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  def resetPassword(agentId: Long) = IsAgentLoggedIn(parse.json) { u => request =>
    request.body.validate[AgentResetPassword].map {
      case (s) if !s.matched =>
        BadRequest(Json.toJson(ErrorResponse("提供的信息不匹配(information is incorrect)", 6)))
      case (s) =>
        Ok(Json.toJson(s.reset))
    }.recoverTotal {
      e => BadRequest("Detected error:" + JsError.toFlatJson(e))
    }
  }

  val nameCheck: PartialFunction[KulebaoAgent, SimpleResult] = {
    case (s) if s.name.isEmpty =>
      BadRequest(Json.toJson(ErrorResponse("请提供代理商名称(please provide name of the agent)", 4)))
    case (s) if s.login_name.isEmpty =>
      BadRequest(Json.toJson(ErrorResponse("必须提供代理商登陆名称(please provide login name of the agent)", 7)))
    case (s) if Employee.loginNameExists(s.login_name.get, s.id) =>
      BadRequest(Json.toJson(ErrorResponse("登陆名已存在，请另行选择(please provide another login name)", 8)))
  }

  def idCheck(id: Long): PartialFunction[KulebaoAgent, SimpleResult] = {
    case (s) if s.id != Some(id) =>
      BadRequest(Json.toJson(ErrorResponse("ID不匹配(id is not match)", 3)))
  }

  val idExistCheck: PartialFunction[KulebaoAgent, SimpleResult] = {
    case (s) if s.id.isDefined =>
      BadRequest(Json.toJson(ErrorResponse("更新请使用update接口(please use update interface)", 2)))
  }

  val phoneCheck: PartialFunction[KulebaoAgent, SimpleResult] = {
    case (s) if s.phone.isEmpty =>
      BadRequest(Json.toJson(ErrorResponse("必须提供电话号码(please provide phone number)", 9)))
    case (s) if KulebaoAgent.duplicatedPhone(s) =>
      BadRequest(Json.toJson(ErrorResponse("电话号与其他代理商重复(duplicated phone number)", 10)))
  }

  val updateAgent: PartialFunction[KulebaoAgent, SimpleResult] = {
    case (s) =>
      Ok(Json.toJson(s.update))
  }

  val createAgent: PartialFunction[KulebaoAgent, SimpleResult] = {
    case (s) =>
      Ok(Json.toJson(s.create))
  }

  def stats(agentId: Long) = IsAgentLoggedIn { u => _ =>
    Ok(Json.toJson(KulebaoAgent.stats(agentId)))
  }

  def summarise(kg: Long) = IsLoggedIn {
    u => _ =>
      AgentSchool.hasAgent(kg) match {
        case false =>
          Ok(Json.toJson(ErrorResponse(s"学校${kg}没有代理商(no agent for given school)", 11)))
        case true =>
          Ok(Json.toJson(AgentSchool.summarise(kg)))
      }

  }

  def monthlyStatistics() = IsOperator { u => _ =>
    KulebaoAgent.monthlyStatistics
    Ok(Json.toJson(new SuccessResponse))
  }

  def deleteStats(agentId: Long, id: Long) = IsOperator { u => _ =>
    KulebaoAgent.deleteStats(agentId, id)
    Ok(Json.toJson(new SuccessResponse))
  }

}
