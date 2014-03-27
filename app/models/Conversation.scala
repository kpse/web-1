package models

import play.api.Play.current
import play.api.db.DB
import anorm._
import anorm.SqlParser._
import models.helper.RangerHelper._

case class Conversation(phone: String, timestamp: Long, id: Option[Long], content: String, image: Option[String], sender: Option[String], sender_id: Option[String])

object Conversation {
  val simple = {
    get[String]("phone") ~
      get[Long]("timestamp") ~
      get[Long]("uid") ~
      get[String]("content") ~
      get[Option[String]]("image") ~
      get[Option[String]]("sender") ~
      get[Option[String]]("sender_id") map {
      case phone ~ t ~ id ~ content ~ image ~ sender ~ senderId =>
        Conversation(phone, t, Some(id), content, image, Some(sender.getOrElse("")), Some(senderId.getOrElse("")))
    }
  }

  def create(kg: Long, conversation: Conversation) = DB.withConnection {
    implicit c =>
      val time = System.currentTimeMillis
      val id = SQL("INSERT INTO conversation (school_id, phone, content, image, sender, timestamp, sender_id) values" +
        "({kg}, {phone}, {content}, {image}, {sender}, {timestamp}, {sender_id})").on(
          'kg -> kg.toString,
          'phone -> conversation.phone,
          'content -> conversation.content,
          'image -> conversation.image,
          'sender -> conversation.sender,
          'sender_id -> conversation.sender_id,
          'timestamp -> time
        ).executeInsert()
      Conversation(conversation.phone, time, id, conversation.content, conversation.image, conversation.sender, conversation.sender_id)
  }

  def index(kg: Long, phone: String, from: Option[Long], to: Option[Long]) = DB.withConnection {
    implicit c =>
      SQL("select * from conversation where school_id={kg} and phone={phone} " +
        rangerQuery(from, to))
        .on(
          'kg -> kg.toString,
          'phone -> phone,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

}
