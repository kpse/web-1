package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class SchoolLogStore(id: Option[Long], url: String, logged_day: Long) {
  def update(kg: Long): Option[SchoolLogStore] = DB.withConnection {
    implicit c =>
      SQL("update logstorage set url={url}, logged_day={time} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'url -> url,
          'time -> logged_day
        ).executeUpdate()
      id flatMap (SchoolLogStore.show(kg, _))
  }

  def create(kg: Long): Option[SchoolLogStore] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into logstorage (school_id, url, logged_day) values (" +
        "{school_id}, {url}, {time})")
        .on(
          'school_id -> kg,
          'url -> url,
          'time -> logged_day
        ).executeInsert()
      insert flatMap (SchoolLogStore.show(kg, _))
  }
}

object SchoolLogStore {
  implicit val readSchoolLogStore = Json.reads[SchoolLogStore]
  implicit val writeSchoolLogStore = Json.writes[SchoolLogStore]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]): List[SchoolLogStore] = DB.withConnection {
    implicit c =>
      SQL(s"select * from logstorage where school_id={kg} and status=1" +
        s" ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple(kg) *)
  }

  def show(kg: Long, id: Long): Option[SchoolLogStore] = DB.withConnection {
    implicit c =>
      SQL(s"select * from logstorage where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple(kg) singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update logstorage set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  def simple(kg: Long) = {
    get[Long]("uid") ~
      get[String]("url") ~
      get[Long]("logged_day") map {
      case id ~ url ~ time =>
        SchoolLogStore(Some(id), url, time)
    }
  }


}

