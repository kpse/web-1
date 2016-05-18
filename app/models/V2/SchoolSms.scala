package models.V2

import play.api.libs.json.Json

case class SchoolSms(school_id: Long, account: String, password: String, available: Long, consumers: Long)

object SchoolSms {
  implicit val readSchoolSms = Json.reads[SchoolSms]
  implicit val writeSchoolSms = Json.writes[SchoolSms]

  def config(schoolId: Long) = SchoolSms(schoolId, "BABY003370", "123qwe_", 5000, 120)

}
