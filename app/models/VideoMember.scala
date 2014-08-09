package models

import play.api.libs.json.Json


case class VideoMember(id: String, account: String, password: String)



object VideoMember {
  implicit val write = Json.writes[VideoMember]

}
