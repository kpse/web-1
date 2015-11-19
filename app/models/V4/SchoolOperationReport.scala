package models.V4

import anorm.SqlParser._
import anorm._
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class SchoolOperationReport(id: Long, school_id: Long, month: String, logged_once: Long, logged_ever: Long, created_at: Long, child_count: Long, parent_count: Long)

object SchoolOperationReport {
  implicit val writeSchoolOperationReport = Json.writes[SchoolOperationReport]
  implicit val readSchoolOperationReport = Json.reads[SchoolOperationReport]

  val pattern: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMM")

  def collectTheWholeMonth(school_id: Long, lastMonth: DateTime): SchoolOperationReport = DB.withConnection {
    implicit c =>
      val firstMilli = lastMonth.withDayOfMonth(1).withMillisOfDay(1).getMillis
      val lastMilli = lastMonth.plusMonths(1).withDayOfMonth(1).withMillisOfDay(1).getMillis
      Logger.debug(s"collectTheWholeMonth firstMilli = $firstMilli")
      Logger.debug(s"collectTheWholeMonth lastMilli = $lastMilli")
      val loggedOnce: Long = SQL("SELECT count(distinct b.phone) count" +
        "  FROM bindinghistory b," +
        "       parentinfo p" +
        " where b.phone= p.phone" +
        "   and p.school_id= {kg}" +
        "   and p.status= 1 and b.updated_at > {begin} and b.updated_at < {end}")
        .on(
          'kg -> school_id.toString,
          'begin -> firstMilli,
          'end -> lastMilli
        ).as(get[Long]("count") single)
      val loggedEver: Long = SQL("SELECT count(distinct b.phone) count" +
        "  FROM bindinghistory b," +
        "       parentinfo p" +
        " where b.phone= p.phone" +
        "   and p.school_id= {kg}" +
        "   and p.status= 1 and b.updated_at < {end}")
        .on(
          'kg -> school_id.toString,
          'begin -> firstMilli,
          'end -> lastMilli
        ).as(get[Long]("count") single)
      val childCount: Long = SQL("SELECT count(distinct child_id) count FROM childinfo where school_id={kg} and status=1")
        .on(
          'kg -> school_id
        ).as(get[Long]("count") single)
      val parentCount: Long = SQL("SELECT count(distinct parent_id) count FROM parentinfo where school_id={kg} and status=1")
        .on(
          'kg -> school_id
        ).as(get[Long]("count") single)
      SchoolOperationReport(0, school_id, pattern.print(lastMonth), loggedOnce, loggedEver, System.currentTimeMillis, childCount, parentCount)
  }
}
