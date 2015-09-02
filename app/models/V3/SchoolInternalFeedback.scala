package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class SchoolInternalFeedback(id: Option[Long], notification_id: Long, class_id: Int, created_at: Option[Long]) {
  def update(kg: Long, notificationId: Long): Option[SchoolInternalFeedback] = DB.withConnection {
    implicit c =>
      SQL("update internalfeedback set school_id={school_id}, class_id={class_id}" +
        "where school_id={school_id} and uid={id} and notification_id={notification_id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'notification_id -> notificationId,
          'class_id -> class_id
        ).executeUpdate()
      id flatMap (SchoolInternalFeedback.show(kg, notification_id, _))
  }

  def create(kg: Long, notificationId: Long): Option[SchoolInternalFeedback] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into internalfeedback (school_id, notification_id, class_id, created_at) values (" +
        "{school_id}, {notification_id}, {class_id}, {time})")
        .on(
          'school_id -> kg,
          'notification_id -> notificationId,
          'class_id -> class_id,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (SchoolInternalFeedback.show(kg, notification_id, _))
  }
}

object SchoolInternalFeedback {
  implicit val readSchoolInternalFeedback = Json.reads[SchoolInternalFeedback]
  implicit val writeSchoolInternalFeedback = Json.writes[SchoolInternalFeedback]

  def show(kg: Long, notificationId: Long, id: Long): Option[SchoolInternalFeedback] = DB.withConnection {
    implicit c =>
      SQL(s"select * from internalfeedback where school_id={kg} and uid={id} and notification_id={nId} and status=1")
        .on(
          'kg -> kg.toString,
          'nId -> notificationId,
          'id -> id
        ).as(simple(kg) singleOpt)
  }

  def index(kg: Long, notificationId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from internalfeedback where school_id={kg} and notification_id={nId} and status=1" +
        s" ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'nId -> notificationId,
          'from -> from,
          'to -> to
        ).as(simple(kg) *)
  }

  def deleteById(kg: Long, notificationId: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update internalfeedback set status=0 where uid={id} and notification_id={nId} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'nId -> notificationId,
          'id -> id
        ).executeUpdate()
  }

  def simple(kg: Long) = {
    get[Long]("uid") ~
      get[Long]("notification_id") ~
      get[Int]("class_id") ~
      get[Option[Long]]("created_at") map {
      case id ~ nId ~ classId ~ time =>
        SchoolInternalFeedback(Some(id), nId, classId, time)
    }
  }
}
