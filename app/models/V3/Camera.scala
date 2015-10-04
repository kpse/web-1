package models.V3

import anorm.SqlParser._
import anorm._
import models.ErrorResponse
import models.helper.RangerHelper
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class Camera(id: Option[Long], account: String, password: String, hardware: Hardware) {
  def update(kg: Long): Option[Camera] = DB.withTransaction {
    implicit c =>
      try {
        val updated: Option[Hardware] = hardware.update(kg)
        SQL(s"update camerarecord set hardware_id={hardware}, account={account}, password={password}, updated_at={time}" +
          s" where uid={id} and school_id={kg}")
          .on(
            'id -> id,
            'kg -> kg.toString,
            'hardware -> updated.map(_.id),
            'account -> account,
            'password -> password,
            'time -> System.currentTimeMillis()
          ).executeUpdate()
        c.commit()
        id flatMap (Camera.show(kg, _))

      }
      catch {
        case e: Throwable =>
          c.rollback()
          Logger.warn(e.getLocalizedMessage)
          None
      }
  }

  def create(kg: Long): Option[Camera] = DB.withTransaction {
    implicit c =>
      try {
        val create1: Option[Hardware] = hardware.create(kg)
        val insert: Option[Long] = SQL(s"insert into camerarecord (school_id, hardware_id, account, password, updated_at) values " +
          s"({kg}, {hardware}, {account}, {password}, {time})")
          .on(
            'kg -> kg.toString,
            'hardware -> create1.map(_.id),
            'account -> account,
            'password -> password,
            'time -> System.currentTimeMillis()
          ).executeInsert()
        c.commit()
        insert flatMap (Camera.show(kg, _))
      }
      catch {
        case e: Throwable =>
          c.rollback()
          Logger.warn(e.getLocalizedMessage)
          None
      }
  }

  def delete(kg: Long) = DB.withTransaction {
    implicit c =>
      try {
        SQL(s"update hardware set status=0 where uid={id} and school_id={kg} and status=1")
          .on(
            'kg -> kg.toString,
            'id -> hardware.id
          ).executeUpdate()

        SQL(s"update camerarecord set status=0 where uid={id} and school_id={kg} and status=1")
          .on(
            'kg -> kg.toString,
            'id -> id
          ).executeUpdate()
        c.commit()
      }
      catch {
        case e: Throwable =>
          c.rollback()
          Logger.warn(e.getLocalizedMessage)
          ErrorResponse(e.getLocalizedMessage)
      }
  }
}

object Camera {
  implicit val readCamera = Json.reads[Camera]
  implicit val writeCamera = Json.writes[Camera]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from camerarecord where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to,
          'most -> most
        ).as(simple *)
  }


  def show(kg: Long, id: Long): Option[Camera] = DB.withConnection {
    implicit c =>
      SQL(s"select * from camerarecord where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = Camera.show(kg, id).foreach(_.delete(kg))

  val simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[Long]("hardware_id") ~
      get[String]("account") ~
      get[String]("password") ~
      get[Long]("updated_at") map {
      case id ~ kg ~ hardware ~ account ~ password ~ time =>
        Tuple4(Some(id), account, password, Hardware.show(kg.toLong, hardware))
    } map {
      case (id, account, password, hardware) if hardware.nonEmpty =>
        Camera(id, account, password, hardware.get)
    }
  }

}