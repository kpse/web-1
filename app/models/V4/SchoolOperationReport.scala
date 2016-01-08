package models.V4

import anorm.SqlParser._
import anorm._
import controllers.helper.CacheHelper._
import models.json_models.SchoolIntro
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import play.Logger
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class SchoolOperationReport(id: Long, school_id: Long, month: String, day: String, logged_once: Long, logged_ever: Long, created_at: Long, child_count: Long, parent_count: Long) {
  def save(time: DateTime) = DB.withConnection {
    implicit c =>
      Logger.debug(s"save school $school_id statistics at $time")
      SQL(s"insert into school_statistics (school_id, month, day, child_count, parent_count, logged_once, logged_ever, created_at) values " +
        s"({school_id}, {month}, {day}, {child}, {parent}, {once}, {ever}, {time})")
        .on(
          'school_id -> school_id,
          'month -> month,
          'day -> day,
          'child -> child_count,
          'parent -> parent_count,
          'once -> logged_once,
          'ever -> logged_ever,
          'time -> System.currentTimeMillis()
        ).executeInsert()
  }

  def toWeekly(weekStart: String) = SchoolWeeklyReport(id, school_id, weekStart, logged_once, logged_ever, created_at, child_count, parent_count)

}

object SchoolOperationReport {
  implicit val writeSchoolOperationReport = Json.writes[SchoolOperationReport]
  implicit val readSchoolOperationReport = Json.reads[SchoolOperationReport]

  implicit val statisticsControllerCacheKey = "school_monthly_counting"
  createKeyCache(statisticsControllerCacheKey)


  val pattern: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMM")
  val dayPattern: DateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd")

  def collectInGivenTimePeriod(school_id: Long, start: DateTime, end: DateTime): SchoolOperationReport = DB.withConnection {
    implicit c =>
      val firstMilli = start.getMillis
      val lastMilli = end.getMillis
      Logger.debug(s"collectInGivenTimePeriod firstMilli = $firstMilli ${dayPattern.print(start.getMillis)}")
      Logger.debug(s"collectInGivenTimePeriod lastMilli = $lastMilli ${dayPattern.print(start.getMillis)}")
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
      val childCount: Long = SQL("SELECT count(distinct child_id) count FROM childinfo where school_id={kg} and status=1 and created_at < {end}")
        .on(
          'kg -> school_id,
          'end -> lastMilli
        ).as(get[Long]("count") single)
      val parentCount: Long = SQL("SELECT count(distinct parent_id) count FROM parentinfo where school_id={kg} and status=1 and created_at < {end}")
        .on(
          'kg -> school_id,
          'end -> lastMilli
        ).as(get[Long]("count") single)
      SchoolOperationReport(0, school_id, pattern.print(start), dayPattern.print(end), loggedOnce, loggedEver, System.currentTimeMillis, childCount, parentCount)
  }

  def timePeriodOfMonth(lastMonth: DateTime): (DateTime, DateTime) = {
    val startDate = lastMonth.withDayOfMonth(1).withMillisOfDay(1)
    val endDate = lastMonth.plusMonths(1).withDayOfMonth(1).withMillisOfDay(1)
    Tuple2(startDate, endDate)
  }

  def monthlyCountingLogic(schoolId: Long, lastMonth: DateTime): SchoolOperationReport = {
    val day: String = dayPattern.print(lastMonth)
    val cacheKey: String = s"monthlyCount_for_${schoolId}_at_day_$day"
    val value: SchoolOperationReport = digFromCache[SchoolOperationReport](cacheKey, 3600 * 6, () => {
      lastMonthHistoryData(schoolId, lastMonth).getOrElse {
        val start = lastMonth.withDayOfMonth(1).withMillisOfDay(1)
        val end = lastMonth.plusMonths(1).withDayOfMonth(1).withMillisOfDay(1)
        val data = SchoolOperationReport.collectInGivenTimePeriod(schoolId, start, end)
        data.copy(day = "").save(lastMonth)
        data
      }
    })
    value
  }


  def tillTodayCountingLogic(schoolId: Long, today: DateTime): SchoolOperationReport = {
    val day: String = dayPattern.print(today)
    val cacheKey: String = s"countTillToday_for_${schoolId}_at_day_$day"
    val value: SchoolOperationReport = digFromCache[SchoolOperationReport](cacheKey, 3600 * 6, () => {
      specificDayData(schoolId, today).getOrElse {
        val start = today.withDayOfMonth(1).withMillisOfDay(1)
        val end = today.withMillisOfDay(1)
        val data = SchoolOperationReport.collectInGivenTimePeriod(schoolId, start, end)
        data.copy(month = "").save(today)
        data
      }
    })
    value
  }

  def monthlyStatistics = {
    val lastMonth = DateTime.now().minusMonths(1)
    SchoolIntro.allIds map (monthlyCountingLogic(_, lastMonth))
  }

  def dailyStatistics = {
    val today = DateTime.now()
    SchoolIntro.allIds map (tillTodayCountingLogic(_, today))
  }

  def lastMonthHistoryData(kg: Long, month: DateTime) = DB.withConnection {
    implicit c =>
      SQL(s"select * from school_statistics where school_id={kg} and month={month} order by uid DESC limit 1")
        .on('kg -> kg.toString, 'month -> pattern.print(month)).as(simpleStatistics singleOpt)
  }

  def specificDayData(kg: Long, day: DateTime) = DB.withConnection {
    implicit c =>
      SQL(s"select * from school_statistics where school_id={kg} and day={day} order by uid DESC limit 1")
        .on('kg -> kg.toString, 'day -> dayPattern.print(day)).as(simpleStatistics singleOpt)
  }


  val simpleStatistics = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[String]("month") ~
      get[String]("day") ~
      get[Long]("logged_once") ~
      get[Long]("logged_ever") ~
      get[Long]("child_count") ~
      get[Long]("parent_count") ~
      get[Long]("created_at") map {
      case id ~ school ~ month ~ day ~ once ~ ever ~ child ~ parent ~ created =>
        SchoolOperationReport(id, school.toLong, month, day, once, ever, created, child, parent)
    }
  }
}
