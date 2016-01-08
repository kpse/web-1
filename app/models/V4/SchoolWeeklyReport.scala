package models.V4

import java.sql.Connection

import anorm.SqlParser._
import anorm._
import controllers.helper.CacheHelper._
import models.json_models.SchoolIntro
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants._
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import play.Logger
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class SchoolWeeklyReport(id: Long, school_id: Long, week_start: String, week_end: String, logged_once: Long, logged_ever: Long, created_at: Long, child_count: Long, parent_count: Long) {
  def save = DB.withConnection {
    implicit c =>
      val time = System.currentTimeMillis
      Logger.debug(s"save school $school_id weekly statistics at $time")
      SQL(s"insert into school_weekly_statistics (school_id, week_start, week_end, child_count, parent_count, logged_once, logged_ever, created_at) values " +
        s"({school_id}, {week}, {end}, {child}, {parent}, {once}, {ever}, {time})")
        .on(
          'school_id -> school_id,
          'week -> week_start,
          'end -> week_end,
          'child -> child_count,
          'parent -> parent_count,
          'once -> logged_once,
          'ever -> logged_ever,
          'time -> time
        ).executeInsert()
  }
}

object SchoolWeeklyReport {
  implicit val writeSchoolWeeklyReport = Json.writes[SchoolWeeklyReport]
  implicit val readSchoolWeeklyReport = Json.reads[SchoolWeeklyReport]

  implicit val SchoolWeeklyReportCacheKey = "school_weekly_counting"
  createKeyCache(SchoolWeeklyReportCacheKey)

  val dayPattern: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd")

  def weeklyStatistics = {
    val weekStart = DateTime.now().minusWeeks(1).withDayOfWeek(1)
    SchoolIntro.allIds map (weeklyCountingLogic(_, weekStart))
  }

  def weeklyHistoryData(kg: Long, start: DateTime) = DB.withConnection {
    implicit c =>
      SQL(s"select * from school_weekly_statistics where school_id={kg} and week_start={start} order by uid DESC limit 1")
        .on('kg -> kg.toString, 'start -> dayPattern.print(start)).as(simple singleOpt)
  }

  def weeklyStats(agentId: Long)(implicit c: Connection) = {
      val start = dayPattern.print(DateTime.now().minusWeeks(4).withDayOfWeek(MONDAY))
      val end = dayPattern.print(DateTime.now().minusWeeks(1).withDayOfWeek(MONDAY))
      Logger.debug(s"from = $start")
      Logger.debug(s"to = $end")
      SQL(s"select s.* from school_weekly_statistics s, agentschool a where week_start >= {from} and week_start <= {to} " +
        s"and agent_id={agent} and a.school_id = s.school_id and a.status=1")
        .on(
          'agent -> agentId,
          'from -> start,
          'to -> end
        ).as(simple *)
  }

  def weeklyCountingLogic(schoolId: Long, weekStart: DateTime): SchoolWeeklyReport = {
    val startDateString: String = dayPattern.print(weekStart)

    val cacheKey: String = s"weekly_count_for_${schoolId}_at_start_day_$startDateString"
    val value: SchoolWeeklyReport = digFromCache[SchoolWeeklyReport](cacheKey, 3600 * 24 * 5, () => {
      weeklyHistoryData(schoolId, weekStart).getOrElse {
        val weekStartTimeStamp = weekStart.withDayOfWeek(MONDAY).withMillisOfDay(1)
        val weekEndTimeStamp = weekStart.plusWeeks(1).withDayOfWeek(MONDAY).withMillisOfDay(1)
        val weekEndString = dayPattern.print(weekStart.withDayOfWeek(SUNDAY))
        val data = SchoolOperationReport.collectInGivenTimePeriod(schoolId, weekStartTimeStamp, weekEndTimeStamp)
        val weekly = data.toWeekly(startDateString, weekEndString)
        weekly.save
        weekly
      }
    })
    value
  }

  val simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[String]("week_start") ~
      get[String]("week_end") ~
      get[Long]("logged_once") ~
      get[Long]("logged_ever") ~
      get[Long]("child_count") ~
      get[Long]("parent_count") ~
      get[Long]("created_at") map {
      case id ~ school ~ week ~ endWeek ~ once ~ ever ~ child ~ parent ~ created =>
        SchoolWeeklyReport(id, school.toLong, week, endWeek, once, ever, created, child, parent)
    }
  }

}

