package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.{JsError, Json}
import models.{ReaderRecord, ReaderMarker}

object ReaderController extends Controller with Secured {
  implicit val write = Json.writes[ReaderRecord]
  implicit val read = Json.reads[ReaderRecord]

  def lastRead(kg: Long, topicId: String, readerId: String) = IsAuthenticated {
    u => _ =>
      Ok(Json.toJson(ReaderMarker.last(kg, topicId, readerId).getOrElse(ReaderRecord(kg, readerId, topicId, 0, Some(0)))))
  }

  def markRead(kg: Long, topicId: String, readerId: String) = IsAuthenticated(parse.json) {
    u =>
      request =>
        request.body.validate[ReaderRecord].map {
          case (r) if r.school_id == kg && r.topic.equals(topicId) && r.reader.equals(readerId) =>
            Ok(Json.toJson(ReaderMarker.save(r)))
        } recoverTotal {
          e => BadRequest("Detected error:" + JsError.toFlatJson(e))
        }
  }
}
