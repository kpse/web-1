package models

import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._
import anorm.~
import play.Logger


case class Feedback(id: Option[Long], phone: String, content: String, timestamp: Option[Long], comment: Option[String], source: Option[String] = None) {
  def create = DB.withConnection {
    implicit c =>
      SQL("insert into feedback (phone, content, source, comment, update_at) values ({phone}, {content}, {source}, {comment}, {timestamp})")
        .on('phone -> phone,
          'content -> content,
          'source -> source.getOrElse("android parent"),
          'comment -> comment.getOrElse(""),
          'timestamp -> System.currentTimeMillis).executeInsert()
  }
}

object Feedback {
  def update(feedback: Feedback) = DB.withConnection {
    implicit c =>
      SQL("update feedback set phone={phone}, content={content}, source={source}, comment={comment} where uid={id} ")
        .on(
          'id -> feedback.id,
          'phone -> feedback.phone,
          'content -> feedback.content,
          'source -> feedback.source.getOrElse("android parent"),
          'comment -> feedback.comment.getOrElse("")).executeUpdate()
  }

  val simple = {
    get[Option[Long]]("uid") ~
      get[String]("phone") ~
      get[String]("content") ~
      get[Option[Long]]("update_at") ~
      get[Option[String]]("source") ~
      get[Option[String]]("comment") map {
      case id ~ phone ~ content ~ timestamp ~ source ~ comment =>
        Feedback(id, phone, content, timestamp, comment, source)
    }
  }

  def index(source: Option[String]) = DB.withConnection {
    implicit c =>
      source match {
        case Some(s) =>
          SQL("select * from feedback where source={source}").on('source -> s).as(simple *)
        case None =>
          SQL("select * from feedback").as(simple *)
      }

  }


}
