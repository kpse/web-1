package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class SchoolInternalNotification(id: Option[Long], class_id: Int, title: String, content: String, created_at: Option[Long]) {
  def update(kg: Long): Option[SchoolInternalNotification] = DB.withConnection {
    implicit c =>
      SQL("update internalnotification set title={title}, content={content}, class_id={class_id}, updated_at={time} " +
        "where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'title -> title,
          'content -> content,
          'class_id -> class_id,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (SchoolInternalNotification.show(kg, _))
  }

  def create(kg: Long): Option[SchoolInternalNotification] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into internalnotification (school_id, title, content, class_id, updated_at, created_at) values (" +
        "{school_id}, {title}, {content}, {class_id}, {time}, {time})")
        .on(
          'school_id -> kg,
          'title -> title,
          'content -> content,
          'class_id -> class_id,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (SchoolInternalNotification.show(kg, _))
  }
}

object SchoolInternalNotification {
  implicit val readSchoolInternalNotification = Json.reads[SchoolInternalNotification]
  implicit val writeSchoolInternalNotification = Json.writes[SchoolInternalNotification]

  def show(kg: Long, id: Long): Option[SchoolInternalNotification] = DB.withConnection {
    implicit c =>
      SQL(s"select * from internalnotification where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple(kg) singleOpt)
  }

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from internalnotification where school_id={kg} and status=1" +
        s" ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple(kg) *)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update internalnotification set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  def simple(kg: Long) = {
    get[Long]("uid") ~
      get[Int]("class_id") ~
      get[String]("title") ~
      get[String]("content") ~
      get[Option[Long]]("created_at") map {
      case id ~ classId ~ title ~ content ~ time =>
        SchoolInternalNotification(Some(id), classId, title, content, time)
    }
  }

}
