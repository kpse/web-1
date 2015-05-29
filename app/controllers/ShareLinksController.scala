package controllers

import controllers.SessionController._
import models._
import models.SharePage._
import play.api.libs.json.Json
import play.api.mvc._



object ShareLinksController extends Controller with Secured {

  def page(token: String) = Action {
      val page: Option[SharePage] = SharePage.retrieve(token)
      page match {
        case Some(p) if p.id.nonEmpty =>
          Ok(views.html.sharePage(p))
        case None =>
          NotFound(Json.toJson(ErrorResponse("没找到指定的分享记录(sharing record is not found)", 2)))
      }

  }

  def create(kg: Long, topicId: String, id: Long) = IsLoggedIn {
    u => request =>
      ChatSession.showHistory(kg, topicId, id) match {
        case Some(x) =>
          val page: Option[SharePage] = SharePage.create(x)
          Ok(Json.toJson(page))
        case None =>
          NotFound(Json.toJson(ErrorResponse("没有这条历史记录(no such history record)")))
      }


  }
}
