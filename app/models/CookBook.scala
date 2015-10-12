package models

import play.api.Logger
import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._
import anorm.~
import helper.JsonStringHelper.any2JsonString

case class CookbookPreviewResponse(error_code: Int, school_id: Long, cookbook_id: Long, timestamp: Long)

case class DayCookbook(breakfast: Option[String], lunch: Option[String], dinner: Option[String], extra: Option[String])

case class WeekCookbook(mon: Option[DayCookbook], tue: Option[DayCookbook], wed: Option[DayCookbook], thu: Option[DayCookbook], fri: Option[DayCookbook])

case class CookbookDetail(error_code: Option[Int], school_id: Long, cookbook_id: Option[Long], timestamp: Option[Long], week: WeekCookbook)


object CookBook {
  private val logger = Logger(classOf[CookbookDetail])
  def insertNew(cookbook: CookbookDetail) : Option[CookbookDetail] = DB.withTransaction {
    implicit c =>
      try {
        SQL("update cookbookinfo set status=0 where school_id={school_id} and cookbook_id={cookbook_id}")
          .on('school_id -> cookbook.school_id.toString,
            'cookbook_id -> cookbook.cookbook_id
          ).executeUpdate

        val time = System.currentTimeMillis
        val newId: Option[Long] = SQL("insert into cookbookinfo set school_id={school_id}, cookbook_id={cookbook_id}, extra_tip={extra_tip}," +
          "timestamp={timestamp}, status=1, mon_breakfast={mon_breakfast}, mon_lunch={mon_lunch}, mon_dinner={mon_dinner}, mon_extra={mon_extra}, " +
          "tue_breakfast={tue_breakfast}, tue_lunch={tue_lunch}, tue_dinner={tue_dinner}, tue_extra={tue_extra}, " +
          "wed_breakfast={wed_breakfast}, wed_lunch={wed_lunch}, wed_dinner={wed_dinner}, wed_extra={wed_extra}, " +
          "thu_breakfast={thu_breakfast}, thu_lunch={thu_lunch}, thu_dinner={thu_dinner}, thu_extra={thu_extra}, " +
          "fri_breakfast={fri_breakfast}, fri_lunch={fri_lunch}, fri_dinner={fri_dinner}, fri_extra={fri_extra}")
          .on('school_id -> cookbook.school_id.toString,
            'cookbook_id -> cookbook.cookbook_id.getOrElse(23),
            'extra_tip -> "",
            'timestamp -> time,
            'mon_breakfast -> cookbook.week.mon.getOrElse(emptyDay).breakfast,
            'mon_lunch -> cookbook.week.mon.getOrElse(emptyDay).lunch,
            'mon_dinner -> cookbook.week.mon.getOrElse(emptyDay).dinner,
            'mon_extra -> cookbook.week.mon.getOrElse(emptyDay).extra,
            'tue_breakfast -> cookbook.week.tue.getOrElse(emptyDay).breakfast,
            'tue_lunch -> cookbook.week.tue.getOrElse(emptyDay).lunch,
            'tue_dinner -> cookbook.week.tue.getOrElse(emptyDay).dinner,
            'tue_extra -> cookbook.week.tue.getOrElse(emptyDay).extra,
            'wed_breakfast -> cookbook.week.wed.getOrElse(emptyDay).breakfast,
            'wed_lunch -> cookbook.week.wed.getOrElse(emptyDay).lunch,
            'wed_dinner -> cookbook.week.wed.getOrElse(emptyDay).dinner,
            'wed_extra -> cookbook.week.wed.getOrElse(emptyDay).extra,
            'thu_breakfast -> cookbook.week.thu.getOrElse(emptyDay).breakfast,
            'thu_lunch -> cookbook.week.thu.getOrElse(emptyDay).lunch,
            'thu_dinner -> cookbook.week.thu.getOrElse(emptyDay).dinner,
            'thu_extra -> cookbook.week.thu.getOrElse(emptyDay).extra,
            'fri_breakfast -> cookbook.week.fri.getOrElse(emptyDay).breakfast,
            'fri_lunch -> cookbook.week.fri.getOrElse(emptyDay).lunch,
            'fri_dinner -> cookbook.week.fri.getOrElse(emptyDay).dinner,
            'fri_extra -> cookbook.week.fri.getOrElse(emptyDay).extra
          ).executeInsert()
        c.commit()
        findById(newId.get)
      }
      catch {
        case t: Throwable  =>
          logger.warn("error %s".format(t.toString))
          c.rollback()
          findById(-1)
      }


  }

  val emptyDay = DayCookbook(None, None, None, None)

  def show(kg: Long, cookbookId: Long): Option[CookbookDetail] = DB.withConnection {
    implicit c =>
      SQL("select * from cookbookinfo where status=1 and school_id={school_id} and cookbook_id={cookbook_id}")
        .on('school_id -> kg.toString,
          'cookbook_id -> cookbookId
        ).as(detail singleOpt)
  }

  def findById(uid: Long): Option[CookbookDetail] = DB.withConnection {
    implicit c =>
      SQL("select * from cookbookinfo where status=1 and uid={uid}")
        .on('uid -> uid
        ).as(detail singleOpt)
  }

  def all(kg: Long): List[CookbookDetail] = DB.withConnection {
    implicit c =>
      SQL("select * from cookbookinfo where status=1 and school_id={school_id} limit 1")
        .on('school_id -> kg.toString
        ).as(detail *)
  }

  val previewSimple = {
    get[String]("school_id") ~
      get[Int]("cookbook_id") ~
      get[Long]("timestamp") map {
      case school_id ~ cookbook ~ timestamp =>
        CookbookPreviewResponse(0, school_id.toLong, cookbook, timestamp)
    }
  }

  val detail = {
    get[String]("school_id") ~
      get[Int]("cookbook_id") ~
      get[Long]("timestamp") ~
      get[Option[String]]("extra_tip") ~
      get[Option[String]]("mon_breakfast") ~
      get[Option[String]]("tue_breakfast") ~
      get[Option[String]]("wed_breakfast") ~
      get[Option[String]]("thu_breakfast") ~
      get[Option[String]]("fri_breakfast") ~
      get[Option[String]]("mon_lunch") ~
      get[Option[String]]("tue_lunch") ~
      get[Option[String]]("wed_lunch") ~
      get[Option[String]]("thu_lunch") ~
      get[Option[String]]("fri_lunch") ~
      get[Option[String]]("mon_dinner") ~
      get[Option[String]]("tue_dinner") ~
      get[Option[String]]("wed_dinner") ~
      get[Option[String]]("thu_dinner") ~
      get[Option[String]]("fri_dinner") ~
      get[Option[String]]("mon_extra") ~
      get[Option[String]]("tue_extra") ~
      get[Option[String]]("wed_extra") ~
      get[Option[String]]("thu_extra") ~
      get[Option[String]]("fri_extra") map {
      case school_id ~ cookbook ~ timestamp ~ extra_tip ~
        mon_breakfast ~ tue_breakfast ~ wed_breakfast ~ thu_breakfast ~ fri_breakfast ~
        mon_lunch ~ tue_lunch ~ wed_lunch ~ thu_lunch ~ fri_lunch ~
        mon_dinner ~ tue_dinner ~ wed_dinner ~ thu_dinner ~ fri_dinner ~
        mon_extra ~ tue_extra ~ wed_extra ~ thu_extra ~ fri_extra =>
        val weekCookbook = WeekCookbook(
          Some(DayCookbook(mon_breakfast.s, mon_lunch.s, mon_dinner.s, mon_extra.s)),
          Some(DayCookbook(tue_breakfast.s, tue_lunch.s, tue_dinner.s, tue_extra.s)),
          Some(DayCookbook(wed_breakfast.s, wed_lunch.s, wed_dinner.s, wed_extra.s)),
          Some(DayCookbook(thu_breakfast.s, thu_lunch.s, thu_dinner.s, thu_extra.s)),
          Some(DayCookbook(fri_breakfast.s, fri_lunch.s, fri_dinner.s, fri_extra.s)))
        CookbookDetail(Some(0), school_id.toLong, Some(cookbook), Some(timestamp), weekCookbook)
    }
  }

  def preview(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from cookbookinfo where status=1 and school_id={school_id}")
        .on('school_id -> kg.toString).as(previewSimple *)
  }

}
