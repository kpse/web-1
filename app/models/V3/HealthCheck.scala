package models.V3

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class HealthCheck(id: Option[Long], number: String, origin: String) {
  def exists(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from cardrecord where uid={id}")
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

  def update(kg: Long): Option[HealthCheck] = DB.withConnection {
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

  def create(kg: Long): Option[HealthCheck] = DB.withConnection {
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


