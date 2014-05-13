package models

import play.api.Play.current
import anorm.SqlParser._
import anorm._
import play.api.db.DB
import models.helper.RangerHelper._
import anorm.~
import scala.Some
import org.joda.time.DateTime

case class ChatSession(topic: String, timestamp: Option[Long], id: Option[Long], content: String, media: MediaContent, sender: Sender)

case class Sender(id: String, `type`: Option[String] = Some("t"))

case class MediaContent(url: String, `type`: Option[String] = Some("image"))


object ChatSession {
  def retrieveSender(kg: Long, senderId: Option[String]): Sender = {
    senderId.fold(Sender(""))({
      case phone if Employee.phoneExists(phone) =>
        Sender(Employee.show(phone).get.id.get)
      case phone if Parent.phoneSearch(phone).isDefined =>
        Sender(Parent.phoneSearch(phone).get.parent_id.get, Some("p"))
    })
  }

  def generateClassQuery(classes: String): String = "class_id in (%s)".format(classes)

  def lastMessageInClasses(kg: Long, classes: String) = DB.withConnection {
    implicit c =>
      SQL("select * from sessionlog s," +
        "(select session_id, MAX(update_at) last from sessionlog where " +
        "session_id in (select child_id from childinfo where " + generateClassQuery(classes) + " and school_id={kg} and status=1) " +
        "group by session_id) a where a.last=s.update_at")
        .on(
          'kg -> kg.toString
        ).as(simple *)
  }

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
