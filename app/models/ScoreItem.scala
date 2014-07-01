package models

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import anorm.~
import scala.Some
import play.api.Play.current

case class ScoreItem(employee_id: String, count: Long)


object ScoreItem {
  def count = {
    get[String]("publisher_id") ~
      get[Long]("count(1)") map {
      case id ~ count =>
        ScoreItem(id, count)
    }
  }

  def countHistory(tableName: String)(kg: Long, employeeId: Option[String]) = DB.withConnection {
    implicit c =>
      employeeId match {
        case Some(id) =>
          List(SQL("select {id} as publisher_id, count(1) from " + tableName + " where school_id={kg} and publisher_id={id}")
            .on('id -> id, 'kg -> kg).as(count single))
        case None =>
          SQL("select publisher_id, count(1) from " + tableName + " where school_id={kg} group by publisher_id")
            .on('kg -> kg).as(count *)
      }

  }
}