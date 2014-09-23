package controllers

import models._
import models.VideoMember._
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}

object VideoMemberController extends Controller with Secured {

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(VideoMember.all(kg)))
  }

  def show(kg: Long, id: String) = IsLoggedIn { u => _ =>
    VideoMember.show(kg, id) match {
      case Some(member) =>
        Ok(Json.toJson(member))
      case None =>
        NotFound
    }

  }

  def create(kg: Long) = IsLoggedIn(parse.json) { u =>
    implicit request =>
      request.body.validate[VideoMember].map {
        case (member) if !member.school_id.equals(Some(kg)) =>
          BadRequest(Json.toJson(ErrorResponse("请提供一致的信息。")))
        case (member) =>
          createOrUpdate(member)
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def update(kg: Long, id: String) = IsLoggedIn(parse.json) { u =>
    implicit request =>
      request.body.validate[VideoMember].map {
        case (member) if !member.school_id.equals(Some(kg)) || !id.equals(member.id) =>
          BadRequest(Json.toJson(ErrorResponse("请提供一致的信息。")))
        case (member) =>
          createOrUpdate(member)
      }.recoverTotal {
        e => BadRequest("Detected error:" + JsError.toFlatJson(e))
      }
  }

  def createOrUpdate(videoMember: VideoMember) = videoMember match {
    case (member) if member.isAccountDuplicated =>
      BadRequest(Json.toJson(ErrorResponse(s"提供的账号${member.account.get}重复。")))
    case (member) if member.isExisting =>
      member.update
      Ok(Json.toJson(VideoMember.show(member.school_id.get, member.id)))
    case (member) =>
      member.create
      Ok(Json.toJson(VideoMember.show(member.school_id.get, member.id)))
  }

  def externalIndex(token: String) = Action {
    RawVideoMember.validate(token) match {
      case Some(schoolId) if schoolId.toLong > 0 =>
        Ok(Json.toJson(RawVideoMember.index(schoolId.toLong)))
      case None =>
        Forbidden(Json.toJson(ErrorResponse("不合法的token，请联系幼乐宝工作人员。")))
    }

  }

  def available(kg: Long) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(VideoMember.available(kg)))
  }

  def externalCreate(kg: Long) = OperatorPage(parse.json) {
    u => implicit request =>
      VideoProvider.create(kg)
      Ok(Json.toJson(new SuccessResponse))
  }

  def delete(kg: Long, id: String) = IsLoggedIn {
    u => _ =>
      VideoMember.delete(kg, id)
      Ok(Json.toJson(new SuccessResponse))
  }
}
