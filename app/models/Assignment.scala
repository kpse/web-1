package models

import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._
import play.Logger
import models.helper.RangerHelper._

case class Assignment(id: Long, timestamp: Long, title: String, content: String, publisher: String, icon_url: String, class_id: Int)

object Assignment {

  val simple = {
    get[Long]("uid") ~
      get[Long]("timestamp") ~
      get[String]("title") ~
      get[String]("content") ~
      get[String]("publisher") ~
      get[String]("image") ~
      get[Int]("class_id") map {
      case id ~ t ~ title ~ content ~ publisher ~ image ~ classId =>
        Assignment(id, t, title, content, publisher, image, classId)
    }
  }

  def convertToArray(classes: Option[String]) = {
    classes match {
      case None => Seq()
      case Some(ids) => ids.split(",").map(_.toInt).toSeq
    }
  }

  def index(kg: Long, classId: Option[String], from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      classId match {
        case Some(ids) =>
          SQL("select * from assignment where school_id={kg} and class_id in (%s) ".format(ids) + rangerQuery(from, to))
            .on(
              'kg -> kg.toString,
              'from -> from,
              'to -> to
            ).as(simple *)
        case None =>
          SQL("select * from assignment where school_id={kg} " + rangerQuery(from, to))
            .on(
              'kg -> kg.toString,
              'from -> from,
              'to -> to
            ).as(simple *)
      }

  }
}
