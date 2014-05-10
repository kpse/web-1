package models

import play.api.Play.current
import anorm.SqlParser._
import anorm._
import play.api.db.DB
import models.helper.RangerHelper._
import anorm.~
import scala.Some

case class ChatSession(topic: String, timestamp: Option[Long], id: Option[Long], content: String, media: MediaContent, sender: Sender)

case class Sender(id: String, `type`: Option[String] = Some("t"))

case class MediaContent(url: String, `type`: Option[String] = Some("image"))


object ChatSession {
  val simple = {
    get[String]("session_id") ~
      get[Long]("update_at") ~
      get[Long]("uid") ~
      get[String]("content") ~
      get[String]("media_url") ~
      get[String]("media_type") ~
      get[String]("sender") ~
      get[String]("sender_type") map {
      case topic ~ t ~ id ~ content ~ url ~ mediaType ~ sender ~ senderType =>
        ChatSession(topic, Some(t), Some(id), content, MediaContent(url, Some(mediaType)), Sender(sender, Some(senderType)))
    }
  }

  def create(kg: Long, session: ChatSession) = DB.withConnection {
    implicit c =>
      val time = System.currentTimeMillis
      val id = SQL("INSERT INTO sessionlog (school_id, session_id, content, media_url, media_type, sender, update_at, sender_type) values" +
        "({kg}, {id}, {content}, {url}, {media_type}, {sender}, {update_at}, {sender_type})").on(
          'kg -> kg.toString,
          'id -> session.topic,
          'content -> session.content,
          'url -> session.media.url,
          'media_type -> session.media.`type`,
          'sender -> session.sender.id,
          'sender_type -> session.sender.`type`,
          'update_at -> time
        ).executeInsert()
      ChatSession(session.topic, Some(time), id, session.content, session.media, session.sender)
  }

  def index(kg: Long, sessionId: String, from: Option[Long], to: Option[Long]) = DB.withConnection {
    implicit c =>
      SQL("select * from sessionlog where school_id={kg} and session_id={id} " +
        rangerQuery(from, to))
        .on(
          'kg -> kg.toString,
          'id -> sessionId,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

}
