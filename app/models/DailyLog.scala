package models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import anorm.~
import models.json_models.CheckNotification
import models.json_models.CheckInfo
import play.api.Play.current
import models.helper.RangerHelper.rangerQueryWithField
import org.joda.time.DateTime
import play.Logger
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.Json

import scala.language.postfixOps

case class DailyLog(timestamp: Long, notice_type: Int, child_id: String, record_url: String, parent_name: String, card: Option[String] = None)

case class EmployeeDailyLog(timestamp: Long, notice_type: Int, employee_id: Long, record_url: String, card: String)

case class DailyLogStats(class_id: Int, count: Long, school_id: Long, date: String)

object DailyLog {

  implicit val readEmployeeDailyLog = Json.reads[EmployeeDailyLog]
  implicit val writeEmployeeDailyLog = Json.writes[EmployeeDailyLog]

  def generateDays: List[Long] = (0 to 9).map(DateTime.now().minusDays(_).toLocalDate.toDateTimeAtStartOfDay.toInstant.getMillis).toList

  def singleDay(kg: Long, startTimeStamp: Long) = {
    get[Int]("class_id") ~
      get[Long]("count") map {
      case id ~ count =>
        DailyLogStats(id, count, kg, new DateTime(startTimeStamp).toString(DateTimeFormat.forPattern("yyyy-MM-dd")))
    }
  }

  def singleDayLog(kg: Long, start: Long, end: Long) = DB.withConnection {
    implicit c =>
      Logger.info("select class_id, count(distinct d.child_id) as count from dailylog d, childinfo c where c.child_id=d.child_id and c.school_id=d.school_id and c.school_id=%s and check_at > %d and check_at < %d group by class_id".format(kg, start, end))
      SQL("select class_id, count(distinct d.child_id) as count from dailylog d, childinfo c where c.child_id=d.child_id and c.school_id=d.school_id and c.school_id={kg} and check_at > {start} and check_at < {end} group by class_id")
        .on('kg -> kg, 'start -> start, 'end -> end).as(singleDay(kg, start) *)
  }

  def countHistory(kg: Long) = {
    val days: List[Long] = generateDays
    val dayBounds: List[(Long, Long)] = days.zip(days.tail).take(9)
    dayBounds.foldRight(List[DailyLogStats]())({ (day: (Long, Long), all: List[DailyLogStats]) => all ::: singleDayLog(kg, day._2, day._1) })
  }

  def generateClassQuery(classes: String) = " child_id IN ( SELECT child_id FROM childinfo WHERE school_id={kg} AND class_id IN (%s) AND status=1) ".format(classes)

  def lastCheckInClasses(kg: Long, classes: String) = DB.withConnection {
    implicit c =>
      SQL("SELECT * FROM dailylog d, " +
        " (SELECT MAX(check_at) last_check, child_id FROM `dailylog` WHERE school_id={kg} AND " + generateClassQuery(classes) +
        "  AND check_at > {today} AND check_at < {now} GROUP BY child_id) a " +
        " WHERE d.child_id=a.child_id AND d.check_at=a.last_check ")
        .on(
          'kg -> kg.toString,
          'today -> DateTime.now().toLocalDate.toDateTimeAtStartOfDay.toInstant.getMillis,
          'now -> System.currentTimeMillis
        ).as(simple *)
  }

  val simple = {
    get[String]("child_id") ~
      get[Long]("check_at") ~
      get[Option[String]]("record_url") ~
      get[Int]("notice_type") ~
      get[String]("card_no") ~
      get[String]("parent_name") map {
      case child_id ~ timestamp ~ url ~ notice_type ~ card ~ name if card.isEmpty =>
        DailyLog(timestamp, notice_type, child_id, url.getOrElse(""), name)
      case child_id ~ timestamp ~ url ~ notice_type ~ card ~ name =>
        DailyLog(timestamp, notice_type, child_id, url.getOrElse(""), name, Some(card))
    }
  }
  val employeeSimple = {
    get[Long]("employee_id") ~
      get[Long]("checked_at") ~
      get[Option[String]]("record_url") ~
      get[Int]("notice_type") ~
      get[String]("card") map {
      case id ~ timestamp ~ url ~ notice_type ~ card =>
        EmployeeDailyLog(timestamp, notice_type, id, url.getOrElse(""), card)

    }
  }

  def all(kg: Long, childId: String, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL("select * from dailylog where child_id={child_id} and school_id={school_id} " + rangerQueryWithField(from, to, Some("check_at"), most))
        .on(
          'child_id -> childId,
          'school_id -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def employeeCheck(kg: Long, employeeId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL("select * from employeedailylog where employee_id={employee} and school_id={school_id} " + rangerQueryWithField(from, to, Some("checked_at"), most))
        .on(
          'employee -> employeeId,
          'school_id -> kg.toString,
          'from -> from,
          'to -> to
        ).as(employeeSimple *)
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from dailylog where uid={id} and school_id={school_id}")
        .on(
          'id -> id,
          'school_id -> kg.toString
        ).as(detail(kg) singleOpt)
  }

  def detail(kg: Long) = {
    get[Long]("uid") ~
      get[String]("card_no") ~
      get[Long]("check_at") ~
      get[String]("record_url") ~
      get[Int]("notice_type") map {
      case id ~ card ~ timestamp ~ url ~ notice_type =>
        CheckInfo(kg, card, 0, notice_type, url, timestamp, Some(id))
    }
  }

}
