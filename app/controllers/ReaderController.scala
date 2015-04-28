package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.{JsError, Json}
import models._

object ReaderController extends Controller with Secured {
  implicit val write = Json.writes[ReaderRecord]
  implicit val read = Json.reads[ReaderRecord]

  def lastRead(kg: Long, topicId: String, readerId: String) = IsLoggedIn {
    u => _ =>
      Ok(Json.toJson(ReaderMarker.last(kg, topicId, readerId).getOrElse(ReaderRecord(kg, readerId, topicId, 0, Some(0)))))
  }

  def markRead(kg: Long, topicId: String, readerId: String) = IsLoggedIn(parse.json) {
    u =>
      request =>
        request.body.validate[ReaderRecord].map {
          case (r) if r.school_id == kg && r.topic.equals(topicId) && r.reader.equals(readerId) =>
            Ok(Json.toJson(ReaderMarker.save(r)))
        } recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }

  def employeeLastRead(kg: Long, employeeId: String) = IsLoggedIn {
    u =>
      request =>
        val accesses: List[UserAccess] = UserAccess.queryByUsername(u, kg)
        val children: List[ChildInfo] = UserAccess.isSupervisor(accesses) match {
          case true =>
            Children.findAllInClass(kg, None, Some(true))
          case false =>
            Children.findAllInClass(kg, Some(UserAccess.allClasses(accesses)), Some(true))
        }
        val records: List[Option[ReaderRecord]] = children.map {
          case child =>
            ReaderMarker.last(kg, child.child_id.getOrElse(""), employeeId)
        }.filter(_.nonEmpty)
        Ok(Json.toJson(records))
  }
}
