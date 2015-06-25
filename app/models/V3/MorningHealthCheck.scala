package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class MorningHealthCheck(id: Option[Long], recorder_id: Option[Long], writer_id: Option[Long], checked_at: Option[Long], memo: Option[String], students: Option[List[StudentHealthCheck]]) {
  def update(kg: Long): Option[MorningHealthCheck] = DB.withTransaction {
    implicit c =>
      try {
        SQL("update morningcheckrecord set school_id={school_id}, recorder_id={recorder_id}, writer_id={writer_id}, memo={memo}, " +
          "updated_at={time} where school_id={school_id} and uid={id}")
          .on(
            'id -> id,
            'school_id -> kg,
            'recorder_id -> recorder_id,
            'writer_id -> writer_id,
            'memo -> memo,
            'time -> System.currentTimeMillis
          ).executeUpdate()
        id foreach {
          case i =>
            MorningHealthCheck.cleanStudents(kg, i)
            students foreach {
              _ foreach {
                _.handle(kg, i)
              }
            }
        }
        c.commit()
        MorningHealthCheck.show(kg, id.getOrElse(0))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }

  def create(kg: Long): Option[MorningHealthCheck] = DB.withTransaction {
    implicit c =>
      try {
        val insert: Option[Long] = SQL("insert into morningcheckrecord (school_id, recorder_id, writer_id, memo, updated_at) values (" +
          "{school_id}, {recorder_id}, {writer_id}, {memo}, {time})")
          .on(
            'school_id -> kg,
            'recorder_id -> recorder_id,
            'writer_id -> writer_id,
            'memo -> memo,
            'time -> System.currentTimeMillis
          ).executeInsert()
        insert foreach {
          case i =>
            MorningHealthCheck.cleanStudents(kg, i)
            students foreach {
              _ foreach {
                _.handle(kg, i)
              }
            }
        }
        c.commit()
        MorningHealthCheck.show(kg, insert.getOrElse(0))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }
}

case class StudentHealthCheck(id: Option[Long], record_id: Option[Long], student_id: Option[Long], temperature: Option[String],
                              check_status_id: Option[Long], check_status_name: Option[String], relative_words: Option[String],
                              memo: Option[String]) {
  def exists(id: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from studentmorningcheckrecord where uid={id}")
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


  def update(kg: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL("update studentmorningcheckrecord set school_id={school_id}, record_id={record_id}, student_id={student_id}, " +
        "temperature={temperature}, check_status_id={check_status_id}, check_status_name={check_status_name}, relative_words={relative_words}, memo={memo}, " +
        "updated_at={time} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'record_id -> base,
          'student_id -> student_id,
          'temperature -> temperature,
          'check_status_id -> check_status_id,
          'check_status_name -> check_status_name,
          'relative_words -> relative_words,
          'memo -> memo,
          'time -> System.currentTimeMillis
        ).executeUpdate()
  }

  def create(kg: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL("insert into studentmorningcheckrecord (school_id, record_id, student_id, temperature, check_status_id, check_status_name, relative_words, memo, updated_at) values (" +
        "{school_id}, {record_id}, {student_id}, {temperature}, {check_status_id}, {check_status_name}, {relative_words}, {memo}, {time})")
        .on(
          'school_id -> kg,
          'record_id -> base,
          'student_id -> student_id,
          'temperature -> temperature,
          'check_status_id -> check_status_id,
          'check_status_name -> check_status_name,
          'relative_words -> relative_words,
          'memo -> memo,
          'time -> System.currentTimeMillis
        ).executeInsert()
  }
}

object MorningHealthCheck {

  implicit val writeStudentHealthCheck = Json.writes[StudentHealthCheck]
  implicit val readStudentHealthCheck = Json.reads[StudentHealthCheck]

  implicit val writeMorningHealthCheck = Json.writes[MorningHealthCheck]
  implicit val readMorningHealthCheck = Json.reads[MorningHealthCheck]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from morningcheckrecord where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def studentIndex(kg: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from studentmorningcheckrecord where school_id={kg} and status=1 and record_id={base}")
        .on(
          'kg -> kg.toString,
          'base -> base
        ).as(simpleStudent *)
  }

  def cleanStudents(kg: Long, base: Long) = DB.withTransaction {
    implicit c =>
      SQL(s"update studentmorningcheckrecord set status=0 where school_id={kg} and status=1 and record_id={base}")
        .on(
          'kg -> kg.toString,
          'base -> base
        ).executeUpdate()
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from morningcheckrecord where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update morningcheckrecord set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
      cleanStudents(kg, id)
  }

  def simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[Option[Long]]("recorder_id") ~
      get[Option[Long]]("writer_id") ~
      get[Option[String]]("memo") ~
      get[Option[Long]]("updated_at") map {
      case id ~ kg ~ recorder ~ writer ~ memo ~ time =>
        MorningHealthCheck(Some(id), recorder, writer, time, memo, Some(studentIndex(kg.toLong, id)))
    }
  }

  def simpleStudent = {
    get[Long]("uid") ~
      get[Option[Long]]("record_id") ~
      get[Option[Long]]("student_id") ~
      get[Option[String]]("temperature") ~
      get[Option[Long]]("check_status_id") ~
      get[Option[String]]("check_status_name") ~
      get[Option[String]]("relative_words") ~
      get[Option[String]]("memo") map {
      case id ~ base ~ student ~ temperature ~ statusId ~ name ~ words ~ memo =>
        StudentHealthCheck(Some(id), base, student, temperature, statusId, name, words, memo)
    }
  }
}
