package models.V3

import anorm.SqlParser._
import anorm._
import models.DailyLog
import models.json_models.CheckInfo
import models.json_models.CheckingMessage._
import play.Logger
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

import scala.language.postfixOps

case class CheckingRecordV3(id: Option[Long], check_info: CheckInfo, create_user: Option[Long], memo: Option[String]) {
  def create: Option[CheckingRecordV3] = DB.withTransaction {
    implicit c =>
      try {
        val createdId: Option[Long] = check_info.create
        createdId foreach {
          id =>
            SQL("insert into checkingrecord (school_id, base_id, create_user, memo) values " +
              "({school_id}, {base_id}, {create_user}, {memo})")
              .on(
                'school_id -> check_info.school_id,
                'base_id -> id,
                'create_user -> create_user,
                'memo -> memo
              ).executeInsert()
        }
        c.commit()
        Logger.info(createdId.toString)
        Some(copy(id = createdId))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }

  def createErrorRecord: Option[CheckingRecordV3] = DB.withTransaction {
    implicit c =>
      try {
        val createdId: Option[Long] = check_info.create
        createdId foreach {
          id =>
            SQL("insert into checkingrecord (school_id, base_id, create_user, memo, error_status) values " +
              "({school_id}, {base_id}, {create_user}, {memo}, 0)")
              .on(
                'school_id -> check_info.school_id,
                'base_id -> id,
                'create_user -> create_user,
                'memo -> memo
              ).executeInsert()
        }
        c.commit()
        Logger.info(createdId.toString)
        Some(copy(id = createdId))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }
}

object ErrorCheckingRecordV3 {
  def errorChecked(): String = " and error_status=0"

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = CheckingRecordV3.index(kg, from, to, most)(errorChecked)

  def show(kg: Long, id: Long) = CheckingRecordV3.show(kg, id)(errorChecked)

  def deleteById(kg: Long, id: Long) = CheckingRecordV3.deleteById(kg, id)(errorChecked)
}

object CheckingRecordV3 {
  implicit val writeCardRecordV3 = Json.writes[CheckingRecordV3]
  implicit val readCardRecordV3 = Json.reads[CheckingRecordV3]

  implicit def condition(): String = " and error_status=1"

  def generateSpan(from: Option[Long], to: Option[Long], most: Option[Int]): String = {
    from.getOrElse(0L) + to.getOrElse(0L) match {
      case range if range > 1388534400000L =>
        var result = ""
        from foreach { _ => result = " and d.check_at > {from} " }
        to foreach { _ => result = s"$result and d.check_at < {to} " }
        s"$result order by c.uid DESC limit ${most.getOrElse(25)}"
      case _ =>
        var result = ""
        from foreach { _ => result = " and c.uid > {from} " }
        to foreach { _ => result = s"$result and c.uid < {to} " }
        s"$result order by c.uid DESC limit ${most.getOrElse(25)}"
    }


  }

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int])(implicit condition: () => String): List[CheckingRecordV3] = DB.withConnection {
    implicit c =>
      SQL(s"select c.* from checkingrecord c, dailylog d where c.base_id=d.uid and c.school_id={kg} ${condition()} ${generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple(kg) *)
  }

  def show(kg: Long, id: Long)(implicit condition: () => String): Option[CheckingRecordV3] = DB.withConnection {
    implicit c =>
      SQL(s"select * from checkingrecord where status=1 and uid={id} ${condition()}")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple(kg) singleOpt)
  }

  def deleteById(kg: Long, id: Long)(implicit condition: () => String): Boolean = DB.withConnection {
    implicit c =>
      SQL(s"delete from checkingrecord where uid={id} and school_id={kg} ${condition()}")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).execute()
  }

  def simple(kg: Long) = {
    get[Long]("uid") ~
      get[Long]("base_id") ~
      get[Long]("create_user") ~
      get[Option[String]]("memo") map {
      case id ~ logId ~ user ~ memo =>
        CheckingRecordV3(Some(id), DailyLog.show(kg, logId).get, Some(user), memo)
    }
  }
}
