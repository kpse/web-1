package models.V8

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class IMKeyword(id: Option[Long], word: String) {
  def update(kg: Long): Option[IMKeyword] = DB.withConnection {
    implicit c =>
      SQL("update im_keywords set word={word} " +
        " where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg.toString,
          'word -> word,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (IMKeyword.show(kg, _))
  }

  def create(kg: Long): Option[IMKeyword] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into im_keywords (school_id, word, updated_at, created_at) values (" +
        "{school_id}, {word}, {time}, {time})")
        .on(
          'school_id -> kg.toString,
          'word -> word,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (IMKeyword.show(kg, _))
  }
}

object IMKeyword {

  implicit val readIMKeyword = Json.reads[IMKeyword]
  implicit val writeIMKeyword = Json.writes[IMKeyword]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from im_keywords where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to,
          'most -> most
        ).as(simple *)
  }

  def show(kg: Long, id: Long): Option[IMKeyword] = DB.withConnection {
    implicit c =>
      SQL(s"select * from im_keywords where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update im_keywords set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }


  val simple = {
    get[Long]("uid") ~
      get[String]("word") map {
      case id ~ word =>
        IMKeyword(Some(id), word)
    }
  }

}
