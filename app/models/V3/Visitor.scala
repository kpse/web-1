package models.V3

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class Visitor(id: Option[Long], name: Option[String], certification_type: Option[String], certification_number: Option[String], reason: Option[String],
                   time: Option[Long], quantity: Option[Int], visitor_user_id: Option[Long], visitor_user_type: Option[Int],
                   memo: Option[String], photo_record: Option[String], SGID_picture: Option[String]) {
  def create(kg: Long): Option[Visitor] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into visitor (school_id, name,certificate_type, certificate_number, reason, visited_at, quantity, visitor_user_id, visitor_user_type," +
        "memo, photo_record, SGID_picture) values ({school_id}, {name}, {type}, {number}, {reason}, {time}, {quantity}, {visitor_user_id}, " +
        "{visitor_user_type}, {memo}, {photo_record}, {SGID_picture})")
        .on(
          'id -> id,
          'school_id -> kg,
          'name -> name,
          'type -> certification_type,
          'number -> certification_type,
          'reason -> reason,
          'time -> System.currentTimeMillis,
          'quantity -> quantity,
          'visitor_user_id -> visitor_user_id,
          'visitor_user_type -> visitor_user_type,
          'memo -> memo,
          'photo_record -> photo_record,
          'SGID_picture -> SGID_picture
        ).executeInsert()
      Visitor.show(kg, insert.getOrElse(0))
  }
}

object Visitor {
  implicit val writeVisitor = Json.writes[Visitor]
  implicit val readVisitor = Json.reads[Visitor]

  def generateSpan(from: Option[Long], to: Option[Long], most: Option[Int]): String = {
    var result = ""
    from foreach { _ => result = " and uid > {from} " }
    to foreach { _ => result = s"$result and uid <= {to} " }
    s"$result limit ${most.getOrElse(25)}"
  }

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from visitor c where c.school_id={kg} and status=1 ${generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from visitor c where c.school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update visitor set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  val simple = {
    get[Long]("uid") ~
      get[Option[String]]("school_id") ~
      get[Option[String]]("name") ~
      get[Option[String]]("certificate_type") ~
      get[Option[String]]("certificate_number") ~
      get[Option[String]]("reason") ~
      get[Option[Long]]("visited_at") ~
      get[Option[Int]]("quantity") ~
      get[Option[Long]]("visitor_user_id") ~
      get[Option[Int]]("visitor_user_type") ~
      get[Option[String]]("memo") ~
      get[Option[String]]("photo_record") ~
      get[Option[String]]("sgid_picture") map {
      case id ~ schoolId ~ name ~ cType ~ number ~ reason ~ time ~ quantity ~ user ~ uType ~ memo ~ photo ~ sgid =>
        Visitor(Some(id), name, cType, number, reason, time, quantity, user, uType, memo, photo, sgid)
    }
  }
}
