package models.V3

import anorm.SqlParser._
import anorm._
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class Immune(id: Option[Long], name: Option[String], memo: Option[String])

case class ImmuneStudent(id: Option[Long], student_id: Long) {
  def exists(id: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from studentimmunerecord where uid={id}")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(id: Long, immuneId: Long) = exists(id) match {
    case true =>
      update(id, immuneId)
    case false =>
      create(id, immuneId)
  }


  def update(kg: Long, immuneId: Long) = DB.withConnection {
    implicit c =>
      SQL("update studentimmunerecord set school_id={school_id}, immune_id={immune_id}, student_id={student_id}, " +
        "updated_at={time} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'immune_id -> immuneId,
          'student_id -> student_id,
          'time -> System.currentTimeMillis
        ).executeUpdate()
  }

  def create(kg: Long, immuneId: Long) = DB.withConnection {
    implicit c =>
      SQL("insert into studentimmunerecord (school_id, immune_id, student_id, updated_at) values (" +
        "{school_id}, {immune_id}, {student_id}, {time})")
        .on(
          'school_id -> kg,
          'immune_id -> immuneId,
          'student_id -> student_id,
          'time -> System.currentTimeMillis
        ).executeInsert()
  }
}

case class ImmuneRecord(id: Option[Long], immune: Option[Immune], name: Option[String], description: Option[String], sub_id: Option[Long], sub_name: Option[String], memo: Option[String], created_at: Option[Long], students: Option[List[ImmuneStudent]] = None) {
  def exists(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from immunerecord where uid={id}")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(id: Long) = exists(id) match {
    case true =>
      update(id)
    case false =>
      create(id)
  }


  def update(kg: Long): Option[ImmuneRecord] = DB.withTransaction {
    implicit c =>
      try {
        SQL("update immunerecord set school_id={school_id}, immune_name={immune_name}, immune_memo={immune_memo}, name={name}, " +
          "description={description}, sub_id={sub_id}, sub_name={sub_name}, memo={memo} where school_id={school_id} and uid={id}")
          .on(
            'id -> id,
            'school_id -> kg,
            'immune_name -> immune.get.name,
            'immune_memo -> immune.get.memo,
            'name -> name,
            'description -> description,
            'sub_id -> sub_id,
            'sub_name -> sub_name,
            'memo -> memo,
            'time -> System.currentTimeMillis
          ).executeUpdate()
        id foreach {
          case i =>
            ImmuneRecord.cleanStudents(kg, i)
            students foreach {
              _ foreach {
                _.handle(kg, i)
              }
            }
        }
        c.commit()
        ImmuneRecord.show(kg, id.getOrElse(0))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }

  def create(kg: Long): Option[ImmuneRecord] = DB.withTransaction {
    implicit c =>
      try {
        val insert: Option[Long] = SQL("insert into immunerecord (school_id, immune_name, immune_memo, name, description, sub_id, sub_name, memo, created_at) values (" +
          "{school_id}, {immune_name}, {immune_memo}, {name}, {description}, {sub_id}, {sub_name}, {memo}, {time})")
          .on(
            'school_id -> kg,
            'immune_name -> immune.get.name,
            'immune_memo -> immune.get.memo,
            'name -> name,
            'description -> description,
            'sub_id -> sub_id,
            'sub_name -> sub_name,
            'memo -> memo,
            'time -> System.currentTimeMillis
          ).executeInsert()
        insert foreach {
          case i =>
            ImmuneRecord.cleanStudents(kg, i)
            students foreach {
              _ foreach {
                _.handle(kg, i)
              }
            }
        }
        c.commit()
        ImmuneRecord.show(kg, insert.getOrElse(0))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }
}


object ImmuneRecord {
  implicit val writeImmune = Json.writes[Immune]
  implicit val readImmune = Json.reads[Immune]
  implicit val writeImmuneStudent = Json.writes[ImmuneStudent]
  implicit val readImmuneStudent = Json.reads[ImmuneStudent]
  implicit val writeImmuneRecord = Json.writes[ImmuneRecord]
  implicit val readImmuneRecord = Json.reads[ImmuneRecord]

  def generateSpan(from: Option[Long], to: Option[Long], most: Option[Int]): String = {
    from.getOrElse(0L) + to.getOrElse(0L) match {
      case range if range > 1388534400000L =>
        var result = ""
        from foreach { _ => result = " and created_at > {from} " }
        to foreach { _ => result = s"$result and created_at <= {to} " }
        s"$result order by uid DESC limit ${most.getOrElse(25)}"
      case _ =>
        var result = ""
        from foreach { _ => result = " and uid > {from} " }
        to foreach { _ => result = s"$result and uid <= {to} " }
        s"$result order by uid DESC limit ${most.getOrElse(25)}"
    }
  }

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from immunerecord where school_id={kg} and status=1 ${generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def studentIndex(kg: Long, recordId: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from studentimmunerecord where school_id={kg} and status=1 and immune_id={recordId}")
        .on(
          'kg -> kg.toString,
          'recordId -> recordId
        ).as(simpleStudent *)
  }

  def cleanStudents(kg: Long, base: Long) = DB.withTransaction {
    implicit c =>
      SQL(s"update studentimmunerecord set status=0 where school_id={kg} and status=1 and immune_id={base}")
        .on(
          'kg -> kg.toString,
          'base -> base
        ).executeUpdate()
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from immunerecord where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update immunerecord set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
      cleanStudents(kg, id)
  }

  def simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[Option[String]]("immune_name") ~
      get[Option[String]]("immune_memo") ~
      get[Option[String]]("name") ~
      get[Option[String]]("description") ~
      get[Option[Long]]("sub_id") ~
      get[Option[String]]("sub_name") ~
      get[Option[String]]("memo") ~
      get[Option[Long]]("created_at") map {
      case id ~ kg ~ immune ~ immuneMemo ~ name ~ desc ~ subId ~ subName ~ memo ~ time =>
        ImmuneRecord(Some(id), Some(Immune(None, immune, immuneMemo)), name, desc, subId, subName, memo, time, Some(studentIndex(kg.toLong, id)))
    }
  }

  def simpleStudent = get[Long]("uid") ~
    get[Long]("student_id") map {
    case id ~ student =>
      ImmuneStudent(Some(id), student)
  }
}
