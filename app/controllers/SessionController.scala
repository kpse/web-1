package controllers

import play.api.mvc.Controller
import play.api.libs.json.{JsError, Json}
import models.{DailyLog, MediaContent, Sender, ChatSession}
import play.Logger

object SessionController extends Controller with Secured {

  implicit val read = Json.reads[Sender]
  implicit val read1 = Json.reads[MediaContent]
  implicit val read2 = Json.reads[ChatSession]

  implicit val write = Json.writes[Sender]
  implicit val write1 = Json.writes[MediaContent]
  implicit val write2 = Json.writes[ChatSession]

  def index(kg: Long, topicId: String, from: Option[Long], to: Option[Long], most: Option[Int]) = IsAuthenticated {
    u => _ =>
      Ok(Json.toJson(ChatSession.index(kg, topicId, from, to).take(most.getOrElse(25)).sortBy(_.id)))
  }

  def create(kg: Long, sessionId: String, retrieveRecentFrom: Option[Long]) = IsAuthenticated(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString())
        request.body.validate[ChatSession].map {
          case (message) =>
            val created = ChatSession.create(kg, message)
            retrieveRecentFrom match {
              case Some(from) =>
                Ok(Json.toJson(ChatSession.index(kg, sessionId, Some(from), None).take(25)))
              case _ =>
                Ok(Json.toJson(created))
            }
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  def indexInClasses(kg: Long, classIds: String) = IsAuthenticated {
    u =>
      _ =>
        Ok(Json.toJson(ChatSession.lastMessageInClasses(kg, classIds)))
  }
}
