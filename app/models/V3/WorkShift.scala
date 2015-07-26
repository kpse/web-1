package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current
import scala.language.postfixOps

case class WorkShiftDate(id: Option[Long], base_id: Option[Long], date: Option[String], status: Option[Int]) {
  def exists(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from workshiftdate where uid={id}")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(id: Long, base: Long) = exists(id) match {
    case true =>
      update(id, base)
    case false =>
      create(id, base)
  }

  def update(kg: Long, base: Long): Option[WorkShiftDate] = DB.withConnection {
    implicit c =>
      SQL("update workshiftdate set date={date}, updated_at={time}, shift_status={status} where school_id={school_id} and uid={id} and base_id={base_id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'base_id -> base,
          'date -> date,
          'status -> status,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      WorkShiftDate.show(kg, base_id.getOrElse(0), id.getOrElse(0))
  }

  def create(kg: Long, base: Long): Option[WorkShiftDate] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into workshiftdate (school_id, base_id, `date`, shift_status, updated_at) values (" +
        "{school_id}, {base_id}, {date}, {status}, {time})")
        .on(
          'school_id -> kg,
          'base_id -> base,
          'date -> date,
          'status -> status,
          'time -> System.currentTimeMillis
        ).executeInsert()
      WorkShiftDate.show(kg, base_id.getOrElse(0), insert.getOrElse(0))
  }
}

case class WorkerInShift(id: Option[Long], base_id: Option[Long], user_id: Option[Long], user_type: Option[Int]) {
  def exists(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from workerinshift where uid={id}")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(id: Long, base: Long) = exists(id) match {
    case true =>
      update(id, base)
    case false =>
      create(id, base)
  }


  def update(kg: Long, base: Long): Option[WorkerInShift] = DB.withConnection {
    implicit c =>
      SQL("update workerinshift set name={name}, sn={sn}, updated_at={time} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'base -> base,
          'user -> user_id,
          'type -> user_type,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      WorkerInShift.show(kg, base_id.getOrElse(0), id.getOrElse(0))
  }

  def create(kg: Long, base: Long): Option[WorkerInShift] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into workerinshift (school_id, base_id, user_id, user_type, updated_at) values (" +
        "{school_id}, {base}, {user}, {type}, {time})")
        .on(
          'school_id -> kg,
          'base -> base,
          'user -> user_id,
          'type -> user_type,
          'time -> System.currentTimeMillis
        ).executeInsert()
      WorkerInShift.show(kg, base_id.getOrElse(0), insert.getOrElse(0))
  }
}


case class WorkShift(id: Option[Long], shift_name: Option[String], start_time: Option[String], end_time: Option[String], is_same_day: Option[Int]) {
  def update(kg: Long): Option[WorkShift] = DB.withConnection {
    implicit c =>
      SQL("update workshift set name={name}, start_time={start}, end_time={end}, same_day={same}, updated_at={time} " +
        "where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'start -> start_time,
          'end -> end_time,
          'name -> shift_name,
          'same -> is_same_day,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      WorkShift.show(kg, id.getOrElse(0))
  }

  def create(kg: Long): Option[WorkShift] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into workshift (school_id, name, start_time, end_time, same_day, updated_at) values (" +
        "{school_id}, {name}, {start}, {end}, {same}, {time})")
        .on(
          'school_id -> kg,
          'start -> start_time,
          'end -> end_time,
          'name -> shift_name,
          'same -> is_same_day,
          'time -> System.currentTimeMillis
        ).executeInsert()
      WorkShift.show(kg, insert.getOrElse(0))
  }
}

object WorkShift {
  implicit val writeWorkCheck = Json.writes[WorkShift]
  implicit val readWorkCheck = Json.reads[WorkShift]
  implicit val timeSpanField = "updated_at"

  def generateSpan(from: Option[Long], to: Option[Long], most: Option[Int])(implicit timeSpanField: String): String = {
    from.getOrElse(0L) + to.getOrElse(0L) match {
      case range if range > 1388534400000L =>
        var result = ""
        from foreach { _ => result = s" and $timeSpanField > {from} " }
        to foreach { _ => result = s"$result and $timeSpanField < {to} " }
        s"$result order by uid DESC limit ${most.getOrElse(25)}"
      case _ =>
        var result = ""
        from foreach { _ => result = " and uid > {from} " }
        to foreach { _ => result = s"$result and uid < {to} " }
        s"$result order by uid DESC limit ${most.getOrElse(25)}"
    }
  }

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from workshift where school_id={kg} and status=1 ${generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from workshift where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update workshift set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  def simple = {
    get[Long]("uid") ~
      get[Option[String]]("name") ~
      get[Option[String]]("start_time") ~
      get[Option[String]]("end_time") ~
      get[Option[Int]]("same_day") map {
      case id ~ name ~ start ~ end ~ same =>
        WorkShift(Some(id), name, start, end, same)
    }
  }
}

object WorkShiftDate {
  implicit val writeWorkCheck = Json.writes[WorkShiftDate]
  implicit val readWorkCheck = Json.reads[WorkShiftDate]

  def index(kg: Long, shiftId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from workshiftdate where school_id={kg} and base_id={base} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'base -> shiftId,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def show(kg: Long, shiftId: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from workshiftdate where school_id={kg} and uid={id} and base_id={base} and status=1")
        .on(
          'kg -> kg.toString,
          'base -> shiftId,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, shiftId: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update workshiftdate set status=0 where uid={id} and school_id={kg} and base_id={base} and status=1")
        .on(
          'kg -> kg.toString,
          'base -> shiftId,
          'id -> id
        ).executeUpdate()
  }

  def simple = {
    get[Long]("uid") ~
      get[Option[Long]]("base_id") ~
      get[Option[Int]]("shift_status") ~
      get[Option[String]]("date") map {
      case id ~ base ~ status ~ date =>
        WorkShiftDate(Some(id), base, date, status)
    }
  }
}

object WorkerInShift {
  implicit val writeWorkCheck = Json.writes[WorkerInShift]
  implicit val readWorkCheck = Json.reads[WorkerInShift]

  def index(kg: Long, shiftId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from workerinshift where school_id={kg} and base_id={base} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'base -> shiftId,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def show(kg: Long, shiftId: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from workerinshift where school_id={kg} and uid={id} and base_id={base} and status=1")
        .on(
          'kg -> kg.toString,
          'base -> shiftId,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, shiftId: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update workerinshift set status=0 where uid={id} and school_id={kg} and base_id={base} and status=1")
        .on(
          'kg -> kg.toString,
          'base -> shiftId,
          'id -> id
        ).executeUpdate()
  }

  def simple = {
    get[Long]("uid") ~
      get[Option[Long]]("base_id") ~
      get[Option[Long]]("user_id") ~
      get[Option[Int]]("user_type") map {
      case id ~ base ~ user ~ uType =>
        WorkerInShift(Some(id), base, user, uType)
    }
  }
}
