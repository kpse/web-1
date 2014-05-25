package models

import play.api.Play.current
import play.api.db.DB
import anorm._
import anorm.SqlParser._
import models.helper.RangerHelper._
import anorm.~
import scala.Some

case class Conversation(phone: String, timestamp: Long, id: Option[Long], content: String, image: Option[String], sender: Option[String], sender_id: Option[String])

@Deprecated
object Conversation {
  def retrieveSender(kg: Long, sender: Sender) = {
    sender.`type` match {
      case Some("t") =>
        Employee.findById(kg, sender.id).fold(("", "")) {
          case employee => (employee.name, employee.phone)
        }
      case Some("p") =>
        Parent.findById(kg, sender.id).fold(("", "")) {
          case parent => ("", parent.phone)
        }
      case other => ("", "")
    }
  }

  def newIndex(kg: Long, phone: String, from: Option[Long], to: Option[Long], most: Option[Int]) = {
    Relationship.index(kg, Some(phone), None, None).flatMap {
      case r: Relationship =>
        r.child.fold(List[ChatSession]())({
          case child =>
            ChatSession.index(kg, child.child_id.getOrElse("0"), from, to).take(most.getOrElse(25))
        })
    }.take(most.getOrElse(25)).map {
      case session =>
        val sender = Conversation.retrieveSender(kg, session.sender)
        Conversation(phone, session.timestamp.getOrElse(0), session.id, session.content, Some(session.media.getOrElse(MediaContent("")).url), Some(sender._1), Some(sender._2))
    }.sortBy(_.id)

  }

  @Deprecated
  val simple = {
    get[String]("phone") ~
      get[Long]("update_at") ~
      get[Long]("uid") ~
      get[String]("content") ~
      get[Option[String]]("image") ~
      get[Option[String]]("sender") ~
      get[Option[String]]("sender_id") map {
      case phone ~ t ~ id ~ content ~ image ~ sender ~ senderId =>
        Conversation(phone, t, Some(id), content, image, Some(sender.getOrElse("")), Some(senderId.getOrElse("")))
    }
  }

  @Deprecated
  def create(kg: Long, conversation: Conversation) = DB.withConnection {
    implicit c =>
      val time = System.currentTimeMillis
      val id = SQL("INSERT INTO conversation (school_id, phone, content, image, sender, update_at, sender_id) values" +
        "({kg}, {phone}, {content}, {image}, {sender}, {update_at}, {sender_id})").on(
          'kg -> kg.toString,
          'phone -> conversation.phone,
          'content -> conversation.content,
          'image -> conversation.image,
          'sender -> conversation.sender,
          'sender_id -> conversation.sender_id,
          'update_at -> time
        ).executeInsert()
      Conversation(conversation.phone, time, id, conversation.content, conversation.image, conversation.sender, conversation.sender_id)
  }

  @Deprecated
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
