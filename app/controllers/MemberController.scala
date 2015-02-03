package controllers

import controllers.ParentController._
import models._
import models.ParentMember._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.Controller
import helper.JsonLogger.loggedJson

object MemberController extends Controller with Secured {
  def create(phone: String) = IsLoggedIn(parse.json) {
    u => implicit request =>
      request.body.validate[ParentMember].map {
        case (member) =>
          Parent.phoneSearch(phone) match {
            case Some(parent) if Charge.limitExceed(parent.school_id) =>
              Ok(loggedJson(ErrorResponse("已达到学校授权人数上限，无法再开通新号码，请联系幼乐宝技术支持4009984998")))
            case None =>
              member.save
              Ok(loggedJson(new SuccessResponse(s"号码($phone)会员资格已记录,等待录入家长信息到幼乐宝.(The membership is recorded)")))
            case _ =>
              member.enable match {
                case Some(x) =>
                  Ok(loggedJson(new SuccessResponse(s"号码($phone)会员已开通.(The membership enabled successfully)")))
                case None =>
                  Ok(loggedJson(ErrorResponse(s"号码($phone)会员开通异常.(The phone number is not enabled)")))
              }
          }

      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }

  }

  def delete(phone: String) = IsLoggedIn(parse.json) {
    u => implicit request =>
      Parent.phoneSearch(phone) match {
        case Some(x) =>
          ParentMember(x.phone, 0).disable
          ParentMember.delete(phone)
          Ok(loggedJson(new SuccessResponse("会员已删除成功.(Delete success, for testing)")))
        case p if ParentMember.phoneSearch(phone).nonEmpty =>
          ParentMember.delete(phone)
          Ok(loggedJson(new SuccessResponse("会员已删除成功.(Delete success, for testing)")))
        case None =>
          Ok(loggedJson(ErrorResponse("电话没找到.(The phone number is not found)")))
      }
  }
}
