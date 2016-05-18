package models.V2

import models.SchoolConfig
import play.api.libs.json.Json
import play.api.libs.ws.WS

import scala.concurrent.{Future,ExecutionContext}
import ExecutionContext.Implicits.global

case class SchoolSms(school_id: Long, available: Long, consumers: Long, pagination: Long = 65)

object SchoolSms {
  implicit val readSchoolSms = Json.reads[SchoolSms]
  implicit val writeSchoolSms = Json.writes[SchoolSms]

  def config(schoolId: Long): Future[SchoolSms] = {
    val account = SchoolConfig.valueOfKey(schoolId, "smsPushAccount").getOrElse("")
    val password = SchoolConfig.valueOfKey(schoolId, "smsPushPassword").getOrElse("")
    WS.url(s"http://mb345.com:999/ws/SelSum.aspx?CorpID=${account}&Pwd=${password}").get() map {
      case (res) if res.body.toInt > 0 =>
        SchoolSms(schoolId, res.body.toInt, allFeaturePhones)
      case _ =>
        SchoolSms(schoolId, 0, allFeaturePhones)
    }
  }

  def allFeaturePhones = 120

}
