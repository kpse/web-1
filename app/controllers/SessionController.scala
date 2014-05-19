package controllers

import play.api.mvc.Controller
import play.api.libs.json.{JsError, Json}
import models._
import play.Logger
import models.MediaContent
import models.Sender
import scala.Some
import controllers.helper.JsonLogger.loggedJson

object SessionController extends Controller with Secured {

  implicit val read = Json.reads[Sender]
  implicit val read1 = Json.reads[MediaContent]
  implicit val read2 = Json.reads[ChatSession]

  implicit val write = Json.writes[Sender]
  implicit val write1 = Json.writes[MediaContent]
  implicit val write2 = Json.writes[ChatSession]
  implicit val write3 = Json.writes[Employee]
  implicit val write4 = Json.writes[Parent]
  implicit val write5 = Json.writes[ErrorResponse]

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

  def senderDetail(kg: Long, senderId: String, senderType: String) = IsAuthenticated {
    u => _ =>
      senderType match {
        case "t" =>
          Ok(Json.toJson(Employee.findById(kg, senderId)))
        case "p" =>
          Ok(Json.toJson(Parent.findById(kg, senderId)))
        case other =>
          BadRequest(loggedJson(ErrorResponse("不存在的发送者类型%s".format(other.toString))))

      }

  }

  def history(kg: Long, topicId: String, from: Option[Long], to: Option[Long], most: Option[Int]) = IsAuthenticated {
    u => _ =>
      Ok(Json.toJson(ChatSession.history(kg, topicId, from, to).take(most.getOrElse(25)).sortBy(_.id)))
  }
}
