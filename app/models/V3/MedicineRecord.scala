package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current


case class MedicineRecord(id: Option[Long], student_id: Long, title: String, content: String, updated_at: Option[Long]) {
  def create(kg: Long): Option[MedicineRecord] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into medicinerecord (school_id, student_id, title, content, updated_at) values (" +
        "{school_id}, {student}, {title}, {content}, {time})")
        .on(
          'school_id -> kg,
          'student -> student_id,
          'title -> title,
          'content -> content,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (MedicineRecord.show(kg, _))
  }

  def update(kg: Long): Option[MedicineRecord] = DB.withConnection {
    implicit c =>
      SQL("update medicinerecord set school_id={school_id}, student_id={student}, title={title}, " +
        "content={content}, updated_at={time} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'student -> student_id,
          'title -> title,
          'content -> content,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (MedicineRecord.show(kg, _))
  }
}

object MedicineRecord {
  implicit val readMedicineRecord = Json.reads[MedicineRecord]
  implicit val writeMedicineRecord = Json.writes[MedicineRecord]

  def show(kg: Long, id: Long): Option[MedicineRecord] = DB.withConnection {
    implicit c =>
      SQL(s"select * from medicinerecord where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from medicinerecord where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update medicinerecord set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  def simple = {
    get[Long]("uid") ~
      get[Long]("student_id") ~
      get[String]("title") ~
      get[String]("content") ~
      get[Option[Long]]("updated_at") map {
      case id ~ student ~ title ~ content ~ time =>
        MedicineRecord(Some(id), student, title, content, time)
    }
  }

}
