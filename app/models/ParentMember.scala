package models

import play.api.libs.json.Json

case class ParentMember(phone: String, timestamp: Long)
object ParentMember {
  implicit val parentMemberReader = Json.reads[ParentMember]

}
