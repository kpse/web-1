package controllers

import models.VideoMember
import play.api.libs.json.Json
import play.api.mvc.Controller

object VideoMemberController extends Controller with Secured {

  def index(kg: Long) = IsLoggedIn { u => _ =>
    Ok(Json.toJson(VideoMember.all(kg)))
  }
}
