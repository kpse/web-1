package models

import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current
import anorm.SqlParser._
import anorm._
import anorm.~
import helper.MD5Helper.md5

case class VideoProvider(token: String, school_id: Long)

object VideoProvider {

  implicit val write = Json.writes[VideoProvider]

  def create(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("insert into videoprovidertoken (school_id, token) " +
        "values ({kg}, {token})")
        .on('kg -> kg, 'token -> md5(s"Token$kg(*)")).executeInsert()
  }

  def index = DB.withConnection {
    implicit c =>
      SQL("select * from videoprovidertoken").as(simple *)
  }

  def show(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from videoprovidertoken where school_id={kg}").on('kg -> kg).as(simple singleOpt)
  }

  val simple = {
    get[String]("school_id") ~
    get[String]("token") map {
      case kg ~ token =>
        VideoProvider(token, kg.toLong)
    }
  }

}
