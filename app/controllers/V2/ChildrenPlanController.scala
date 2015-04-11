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
        guardSchoolId(kg) orElse
          guardChildId(childId) orElse
          updatePlan orElse
          createPlan
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
      Ok(Json.toJson(ChildrenPlan.show(kg, childId)))
  }

  def guardSchoolId(kg: Long): PartialFunction[ChildrenPlan, SimpleResult] = {
    case (plan) if plan.school_id != kg =>
      BadRequest(Json.toJson(ErrorResponse("错误的学校id.(Wrong school id in creating/updating children plan)")))
  }

  def guardDriverId(driverId: String): PartialFunction[ChildrenPlan, SimpleResult] = {
    case (plan) if plan.driver_id != driverId =>
      BadRequest(Json.toJson(ErrorResponse("错误的驾驶员id.(Wrong driver id in creating/updating children plan)")))
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
}
