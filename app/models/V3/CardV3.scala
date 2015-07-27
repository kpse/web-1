package models.V3

import anorm.SqlParser._
import anorm._
import play.Logger
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class CardV3(id: Option[Long], number: String, origin: String) {

  def exists = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from cardrecord where uid={id}")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def originExists = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from cardrecord where origin={origin}")
        .on(
          'origin -> origin
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(kg: Long) = exists match {
    case true =>
      update(kg)
    case false =>
      create(kg)
  }

  def update(kg: Long): Option[CardV3] = DB.withConnection {
    implicit c =>
      SQL("update cardrecord set origin={origin}, number={number} " +
        " where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'number -> number,
          'origin -> origin
        ).executeUpdate()
      CardV3.show(kg, id.getOrElse(0))
  }

  def create(kg: Long): Option[CardV3] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into cardrecord (school_id, number, origin) values (" +
        "{school_id}, {number}, {origin})")
        .on(
          'school_id -> kg,
          'number -> number,
          'origin -> origin
        ).executeInsert()
      CardV3.show(kg, insert.getOrElse(0))
  }
}

object CardV3 {
  implicit val writeCardV3 = Json.writes[CardV3]
  implicit val readCardV3 = Json.reads[CardV3]

  def search(kg: Long, q: String) = DB.withConnection {
    implicit c =>
      SQL(s"select * from cardrecord c where origin={q} and status=1")
        .on(
          'kg -> kg.toString,
          'q -> q
        ).as(simple *)
  }

  def valid(q: String) = DB.withConnection {
    implicit c =>
      Logger.info(s"check card: $q")
      SQL(s"select count(1) from cardrecord c where origin={q} and status=1")
        .on(
          'q -> q
        ).as(get[Long]("count(1)") single) > 0
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from cardrecord c where c.school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update cardrecord set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  val simple = {
    get[Long]("uid") ~
      get[String]("origin") ~
      get[String]("number") map {
      case id ~ origin ~ number =>
        CardV3(Some(id), number, origin)
    }
  }

}
