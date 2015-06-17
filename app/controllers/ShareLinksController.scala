package controllers

import controllers.SessionController._
import controllers.helper.QiniuHelper
import models.SharePage._
import models._
import play.Logger
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

  def triggerStreaming(page: Option[SharePage]) = page foreach {
    case p =>
      p.medium foreach {
        case MediaContent(u, t) if t == Some("video") =>
          val b = Bucket.parse(u)
          Logger.info(s"Bucket.parse : $b")
          b foreach QiniuHelper.triggerPfop

      }
  }

  def create(kg: Long, topicId: String, id: Long) = IsLoggedIn {
    u => request =>
      ChatSession.showHistory(kg, topicId, id) match {
        case Some(x) if x.id.nonEmpty =>
          val existing: Option[SharePage] = SharePage.findByOriginalId(x.id.get)
          existing match {
            case Some(item) =>
              Ok(Json.toJson(item))
            case None =>
              val page: Option[SharePage] = SharePage.create(x)
              triggerStreaming(page)
              Ok(Json.toJson(page))
          }
        case None =>
          NotFound(Json.toJson(ErrorResponse("没有这条历史记录(no such history record)")))
      }


  }
}
