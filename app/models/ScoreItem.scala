package models

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import anorm.~
import scala.Some
import play.api.Play.current

case class ScoreItem(employee_id: String, count: Long)


object ScoreItem {
  def count(field: String) = {
    get[String](field) ~
      get[Long]("count(1)") map {
      case id ~ count =>
        ScoreItem(id, count)
    }
  }

  def countHistory(tableName: String, publisherFieldName: String="publisher_id")(kg: Long, employeeId: Option[String]) = DB.withConnection {
    implicit c =>
      employeeId match {
        case Some(id) =>
          List(SQL("select {id} as %s, count(1) from %s where school_id={kg} and %s={id}".format(publisherFieldName, tableName, publisherFieldName))
            .on('id -> id, 'kg -> kg).as(count(publisherFieldName) single))
        case None =>
          SQL("select %s, count(1) from %s where school_id={kg} and %s IS NOT NULL group by %s".format(publisherFieldName, tableName, publisherFieldName, publisherFieldName))
            .on('kg -> kg).as(count(publisherFieldName) *)
      }

  }

  def countAllHistory(tableName: String, publisherFieldName: String="publisher_id") = DB.withConnection {
    implicit c =>
          SQL("select %s, count(1) from %s where %s IS NOT NULL group by %s".format(publisherFieldName, tableName, publisherFieldName, publisherFieldName))
            .as(count(publisherFieldName) *)

  }
}