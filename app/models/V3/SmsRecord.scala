package models.V3

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class SmsRecord(id: Option[Long], phone_list: Option[String], content: Option[String], created_at: Option[Long]) {
  def create(kg: Long): Option[SmsRecord] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into smsrecord (school_id, phone_list, content, created_at) values " +
        "({school_id}, {phones}, {content}, {time})")
        .on(
          'school_id -> kg,
          'phones -> phone_list,
          'content -> content,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert match {
        case Some(x) =>
          SmsRecord.show(kg, x)
        case None =>
          None
      }

  }
}

object SmsRecord {
  implicit val writeSmsRecord = Json.writes[SmsRecord]
  implicit val readSmsRecord = Json.reads[SmsRecord]

  def generateSpan(from: Option[Long], to: Option[Long], most: Option[Int]): String = {
    var result = ""
    from foreach { _ => result = " and uid > {from} " }
    to foreach { _ => result = s"$result and uid <= {to} " }
    s"$result limit ${most.getOrElse(25)}"
  }

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from smsrecord where school_id={kg} ${generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple(kg) *)
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from smsrecord where status=1 and uid={id}")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple(kg) singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"delete from smsrecord where uid={id} and school_id={kg}")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).execute()
  }

  def simple(kg: Long) = {
    get[Long]("uid") ~
      get[Option[String]]("phone_list") ~
      get[Option[String]]("content") ~
      get[Option[Long]]("created_at") map {
      case id ~ phones ~ content ~ time =>
        SmsRecord(Some(id), phones, content, time)
    }
  }
}
