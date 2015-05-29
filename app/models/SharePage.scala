package models

import anorm.SqlParser._
import anorm.{~, _}
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class SharePage(id: Option[Long], token: String, title: String, content: String, sender: Sender, medium: List[MediaContent], comment: Option[String], timestamp: Long)

object SharePage {

  implicit val writesSender = Json.writes[Sender]
  implicit val writesMediaContent = Json.writes[MediaContent]
  implicit val writesSharePage = Json.writes[SharePage]

  def create(record: ChatSession) = DB.withConnection {
    implicit c =>
      val token: String = java.util.UUID.randomUUID.toString.takeWhile(!_.equals('-'))
      SQL("insert into sharedpages (token, original_id, created_at) values ({token}, {id}, {time})")
        .on('token -> token,
          'id -> record.id.get,
          'time -> System.currentTimeMillis).executeInsert()
      retrieve(token)
  }

  def retrieve(token: String): Option[SharePage] = DB.withConnection {
    implicit c =>
      SQL("select * from sharedpages where token={t}")
        .on('t -> token).as(simple singleOpt)
  }

  def findByOriginalId(originalId: Long): Option[SharePage] = DB.withConnection {
    implicit c =>
      SQL("select * from sharedpages where original_id={id}")
        .on('id -> originalId).as(simple singleOpt)
  }

  val simple = {
    get[Long]("uid") ~
    get[String]("token") ~
    get[Long]("original_id") ~
    get[Long]("created_at") ~
      get[Option[String]]("comment") map {
      case id ~ token ~ origin ~ time ~ comment =>
        ChatSession.findHistoryById(origin) match {
          case Some(x) =>
            SharePage(Some(id), token, "", x.content, x.sender, x.medium.getOrElse(List()), comment, time)
          case None =>
            SharePage(None, token, "", "", Sender(""), List(), None, 0)
        }

    }
  }

}
