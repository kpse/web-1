package models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import anorm.~
import play.api.Play.current
import play.Logger

case class SchedulePreviewResponse(error_code: Int, school_id: Long, class_id: Long, schedule_id: Long, timestamp: Long)

case class DaySchedule(am: Option[String], pm: Option[String])

case class WeekSchedule(mon: Option[DaySchedule], tue: Option[DaySchedule], wed: Option[DaySchedule], thu: Option[DaySchedule], fri: Option[DaySchedule])

case class ScheduleDetail(error_code: Int, school_id: Long, class_id: Long, schedule_id: Long, timestamp: Long, week: WeekSchedule)

case class Schedule(school_id: Long, class_id: Long, week: WeekSchedule)


object Schedule {
  def create(kg: Long, classId: Long, schedule: Schedule) = insertNew(ScheduleDetail(0, kg, classId, 0L, 0L, schedule.week))

  def all(kg: Long, classId: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from scheduleinfo where status=1 and school_id={school_id} and class_id={class_id} limit 1")
        .on('school_id -> kg.toString,
          'class_id -> classId
        ).as(detail *)
  }


  def insertNew(schedule: ScheduleDetail) = DB.withTransaction {
    implicit c =>
      try {
        SQL("update scheduleinfo set status=0 where school_id={school_id} and class_id={class_id} and schedule_id={schedule_id}")
          .on('school_id -> schedule.school_id.toString,
            'class_id -> schedule.class_id,
            'schedule_id -> schedule.schedule_id
          ).executeUpdate

        val newId: Option[Long] = SQL("insert into scheduleinfo set school_id={school_id}, " +
          "schedule_id={schedule_id}, class_id={class_id}, timestamp={timestamp}, " +
          "mon_pm={mon_pm}, mon_am={mon_am}, " +
          "tue_pm={tue_pm}, tue_am={tue_am}, " +
          "wed_pm={wed_pm}, wed_am={wed_am}, " +
          "thu_pm={thu_pm}, thu_am={thu_am}, " +
          "fri_pm={fri_pm}, fri_am={fri_am}")
          .on('school_id -> schedule.school_id.toString,
            'schedule_id -> (schedule.schedule_id + 1),
            'class_id -> schedule.class_id,
            'timestamp -> System.currentTimeMillis,
            'mon_am -> schedule.week.mon.getOrElse(emptyDay).am,
            'tue_am -> schedule.week.tue.getOrElse(emptyDay).am,
            'wed_am -> schedule.week.wed.getOrElse(emptyDay).am,
            'thu_am -> schedule.week.thu.getOrElse(emptyDay).am,
            'fri_am -> schedule.week.fri.getOrElse(emptyDay).am,
            'mon_pm -> schedule.week.mon.getOrElse(emptyDay).pm,
            'tue_pm -> schedule.week.tue.getOrElse(emptyDay).pm,
            'wed_pm -> schedule.week.wed.getOrElse(emptyDay).pm,
            'thu_pm -> schedule.week.thu.getOrElse(emptyDay).pm,
            'fri_pm -> schedule.week.fri.getOrElse(emptyDay).pm
          ).executeInsert()
        c.commit()
        findById(newId.get)
      }
      catch {
        case t: Throwable =>
          Logger.info("error %s".format(t.toString))
          c.rollback()
          findById(-1)
      }


  }

  val emptyDay = {
    DaySchedule(None, None)
  }

  def findById(uid: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from scheduleinfo where status=1 and uid={uid}")
        .on('uid -> uid
        ).as(detail singleOpt)
  }

  def show(kg: Long, classId: Long, scheduleId: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from scheduleinfo where status=1 and school_id={school_id} and class_id={class_id} and schedule_id={schedule_id}")
        .on('school_id -> kg.toString,
          'class_id -> classId,
          'schedule_id -> scheduleId
        ).as(detail singleOpt)
  }

  val previewSimple = {
    get[String]("school_id") ~
      get[Int]("class_id") ~
      get[Int]("schedule_id") ~
      get[Long]("timestamp") map {
      case school_id ~ class_id ~ schedule_id ~ timestamp =>
        SchedulePreviewResponse(0, school_id.toLong, class_id, schedule_id, timestamp)
    }
  }

  def preview(kg: Long, classId: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from scheduleinfo where status=1 and school_id={school_id} and class_id={class_id}")
        .on('school_id -> kg.toString,
          'class_id -> classId
        ).as(previewSimple *)
  }

  val detail = {
    get[String]("school_id") ~
      get[Int]("class_id") ~
      get[Int]("schedule_id") ~
      get[Long]("timestamp") ~
      get[Option[String]]("mon_am") ~
      get[Option[String]]("tue_am") ~
      get[Option[String]]("wed_am") ~
      get[Option[String]]("thu_am") ~
      get[Option[String]]("fri_am") ~
      get[Option[String]]("mon_pm") ~
      get[Option[String]]("tue_pm") ~
      get[Option[String]]("wed_pm") ~
      get[Option[String]]("thu_pm") ~
      get[Option[String]]("fri_pm") map {
      case school_id ~ class_id ~ schedule_id ~ timestamp ~
        mon_am ~ tue_am ~ wed_am ~ thu_am ~ fri_am ~
        mon_pm ~ tue_pm ~ wed_pm ~ thu_pm ~ fri_pm =>
        val schedule = WeekSchedule(
          Some(DaySchedule(mon_am, mon_pm)),
          Some(DaySchedule(tue_am, tue_pm)),
          Some(DaySchedule(wed_am, wed_pm)),
          Some(DaySchedule(thu_am, thu_pm)),
          Some(DaySchedule(fri_am, fri_pm)))
        ScheduleDetail(0, school_id.toLong, class_id, schedule_id, timestamp, schedule)
    }
  }
}
