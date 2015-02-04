package controllers

import controllers.ParentController._
import controllers.helper.JsonLogger.loggedJson
import models.ParentMember._
import models._
import play.api.Play
import play.api.libs.json.JsError
import play.api.mvc.{Action, Controller}

object MemberController extends Controller {
  def createToken = Play.current.configuration.getString("member.enable.token").getOrElse("createToken")
  def deleteToken = Play.current.configuration.getString("member.disable.token").getOrElse("deleteToken")

  def create(phone: String) = Action(parse.json) {
    implicit request =>
      request.headers.get("token") match {
        case Some(t) if t.equals(createToken) =>
          request.body.validate[ParentMember].map {
            case (member) =>
              Parent.phoneSearch(phone) match {
                case Some(parent) if Charge.limitExceed(parent.school_id) =>
                  Ok(loggedJson(ErrorResponse("已达到学校授权人数上限，无法再开通新号码，请联系幼乐宝技术支持4009984998")))
                case None if ParentMember.phoneSearch(phone).nonEmpty =>
                  Ok(loggedJson(ErrorResponse(s"号码($phone)已开通过会员，请检查数据.(Duplicated enabling)", 2)))
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
        case _ =>
          NotFound
      }
  }

  def delete(phone: String) = Action {
    implicit request =>
      request.headers.get("token") match {
        case Some(t) if t.equals(deleteToken) =>
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
        case _ =>
          NotFound
      }
  }
}
