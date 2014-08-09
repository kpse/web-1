package controllers

import models.VideoMember
import play.api.libs.json.Json
import play.api.mvc.Controller

object VideoMemberController extends Controller with Secured {

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(VideoMember.all(kg)))
  }

  def show(kg: Long, id: String) = IsLoggedIn { u => _ =>
    val member: Option[VideoMember] = VideoMember.show(kg, id)
    member match {
      case Some(member) =>
        Ok(Json.toJson(member))
      case None =>
        NotFound
    }

  }
}
