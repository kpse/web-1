package controllers

import play.api.mvc.Controller
import models._
import play.api.libs.json.{JsError, Json}
import play.Logger
import scala.Some
import controllers.helper.JsonLogger.loggedJson

object ConversationController extends Controller with Secured {

  implicit val write = Json.writes[Conversation]
  implicit val write1 = Json.writes[Sender]
  implicit val write2 = Json.writes[MediaContent]
  implicit val write3 = Json.writes[ChatSession]
  implicit val write4 = Json.writes[SuccessResponse]

  def index(kg: Long, phone: String, from: Option[Long], to: Option[Long], most: Option[Int]) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(Conversation.newIndex(kg, phone, from, to, most)))
  }

  implicit val read = Json.reads[Conversation]

  def create(kg: Long, phone: String, retrieveRecentFrom: Option[Long]) = IsLoggedIn(parse.json) {
    u =>
      request =>
        Logger.info(request.body.toString())
        request.body.validate[Conversation].map {
          case (conversation) if Parent.phoneExists(kg, phone) =>
            val allRelationships: List[Relationship] = Relationship.index(kg, Some(phone), None, None)
            allRelationships.map {
              case r: Relationship =>
                r.child.map {
                  case child =>
                    ChatSession.create(kg, ChatSession(child.child_id.getOrElse(""), None, None, conversation.content,
                      MediaContent(conversation.image.getOrElse("")), ChatSession.retrieveSender(kg, conversation)))
                }
            }
            retrieveRecentFrom match {
              case Some(from) =>
                Ok(loggedJson(Conversation.newIndex(kg, phone, Some(from), None, Some(25))))
              case _ =>
                Ok(loggedJson(SuccessResponse("成功创建%d条信息".format(allRelationships.size))))
            }

          case _ => BadRequest("该号码系统未能识别。")
        }.recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }
}
