package models.V2

import anorm.SqlParser._
import anorm._
import models.SchoolConfig
import play.api.db.DB
import play.api.libs.json.Json
import play.api.libs.ws.WS

import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import play.api.Play.current

case class SchoolSms(school_id: Long, available: Long, consumers: Long, pagination: Long = 65)

object SchoolSms {
  implicit val readSchoolSms = Json.reads[SchoolSms]
  implicit val writeSchoolSms = Json.writes[SchoolSms]

  def config(schoolId: Long): Future[SchoolSms] = {
    val account = SchoolConfig.valueOfKey(schoolId, "smsPushAccount").getOrElse("")
    val password = SchoolConfig.valueOfKey(schoolId, "smsPushPassword").getOrElse("")
    WS.url(s"http://mb345.com:999/ws/SelSum.aspx?CorpID=${account}&Pwd=${password}").get() map {
      case (res) if res.body.toInt > 0 =>
        SchoolSms(schoolId, res.body.toInt, allFeaturePhones(schoolId))
      case _ =>
        SchoolSms(schoolId, 0, allFeaturePhones(schoolId))
    }
  }

  def allFeaturePhones(schoolId: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from parentinfo p, parentext ex where p.uid = ex.base_id and school_id={kg} and p.status=1 and sms_push=1")
        .on(
          'kg -> schoolId.toString
        ).as(get[Long]("count(1)") single)
  }

}
