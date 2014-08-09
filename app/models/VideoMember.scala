package models

import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current
import anorm.SqlParser._
import anorm._
import anorm.~

case class VideoMember(id: String, account: String, password: String, school_id: Option[Long])

object VideoMember {
  implicit val write = Json.writes[VideoMember]

  def all(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from videomembers where school_id={kg}").on('kg -> kg).as(simple *)
  }

  val simple = {
    get[String]("school_id") ~
    get[String]("parent_id") ~
    get[String]("account") map {
      case kg ~ id ~ account =>
        VideoMember(id, account, "123", Some(kg.toLong))
    }
  }
}
