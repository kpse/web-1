package controllers.V2

import controllers.ClassController._
import controllers.Secured
import models.V2.ChildrenPlan
import models.V2.ChildrenPlan._
import models._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Controller, SimpleResult}

object ChildrenPlanController extends Controller with Secured {
  def index(kg: Long, driverId: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(ChildrenPlan.index(kg, driverId)))
  }
  
  def createOrUpdate(kg: Long, childId: String) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[ChildrenPlan].map {
        handleCreation(kg, childId)
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def delete(kg: Long, childId: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(ChildrenPlan.delete(kg, childId)))
  }

  def show(kg: Long, childId: String) = IsLoggedIn {
    u => _ =>
      ChildrenPlan.show(kg, childId) match {
        case Some(p) =>
          Ok(Json.toJson(p))
        case None =>
          NotFound(Json.toJson(ErrorResponse("${childId}没有对应的乘车计划。(No such children plan)")))
      }
  }

  def handleCreation(kg: Long, childId: String): PartialFunction[ChildrenPlan, SimpleResult] = {
    guardSchoolId(kg) orElse
      guardChildId(childId) orElse
      updatePlan orElse
      createPlan
  }

  def guardSchoolId(kg: Long): PartialFunction[ChildrenPlan, SimpleResult] = {
    case (plan) if plan.school_id != kg =>
      BadRequest(Json.toJson(ErrorResponse("错误的学校id.(Wrong school id in creating/updating children plan)")))
  }

  def guardChildId(childId: String): PartialFunction[ChildrenPlan, SimpleResult] = {
    case (plan) if plan.child_id != childId =>
      BadRequest(Json.toJson(ErrorResponse("错误的小孩id.(Wrong child id in creating/updating children plan)")))
  }

  val createPlan: PartialFunction[ChildrenPlan, SimpleResult] = {
    case (plan) =>
      plan.create match {
        case Some(p) =>
          Ok(Json.toJson(p))
        case None =>
          InternalServerError(Json.toJson(ErrorResponse("创建校车计划失败, 请联系管理员.(Error in creating children plan)")))
      }
  }
  
  val updatePlan: PartialFunction[ChildrenPlan, SimpleResult] = {
    case (plan) if plan.exists =>
      plan.update match {
        case Some(p) =>
          Ok(Json.toJson(p))
        case None =>
          InternalServerError(Json.toJson(ErrorResponse("更新校车计划失败, 请联系管理员.(Error in creating children plan)")))
      }
  }

  def batch(kg: Long) = IsLoggedIn(parse.json) {
    u => request =>
      request.body.validate[List[ChildrenPlan]].map {
        case (plans) =>
           val results: List[SimpleResult] = plans.map { (p) =>
             handleCreation(kg, p.child_id)(p)
           }
          results.find(r => r.header.status != OK).getOrElse(Ok(Json.toJson(new SuccessResponse(s"成功创建${plans.length}个计划(successful created or updated plans)"))))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }
}
