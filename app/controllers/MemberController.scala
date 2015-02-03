package controllers

import controllers.ParentController._
import models._
import models.ParentMember._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller

object MemberController extends Controller with Secured {
  def create(phone: String) = IsAuthenticated(parse.json) {
    u => implicit request =>
      request.body.validate[ParentMember].map {
        case (member) =>
          Ok(Json.toJson(new SuccessResponse("会员已开通.(member enabled successfully)")))
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }

  }

  def delete(phone: String) = IsAuthenticated(parse.json) {
    u => implicit request =>
      Parent.phoneSearch(phone) match {
        case Some(x) =>
          Ok(Json.toJson(new SuccessResponse("会员已删除成功.(delete success, for testing)")))
        case None =>
          Ok(Json.toJson(ErrorResponse("电话没找到.(The phone number is not found)")))
      }
  }
}
