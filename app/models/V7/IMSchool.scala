package models.V7

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class IMSchool(id: Option[Long], school_id: Long, updated_at: Option[Long] = None, created_at: Option[Long] = None) {

  def exists(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from im_school_group where school_id={school_id} limit 1")
        .on(
          'school_id -> kg
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(kg: Long) = exists(kg) match {
    case true =>
      update(kg)
    case false =>
      create(kg)
  }

  def create(kg: Long): Option[IMSchool] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into im_school_group (school_id, updated_at, created_at) values (" +
        "{school_id}, {time}, {time})")
        .on(
          'school_id -> kg,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap ( id => IMSchool.show(kg))
  }

  def update(kg: Long): Option[IMSchool] = DB.withConnection {
    implicit c =>
      SQL("update im_school_group set status=1, updated_at={time} where school_id={school_id} and status=0")
        .on(
          'school_id -> kg,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      IMSchool.show(kg)
  }
}

object IMSchool {
  implicit val writeIMSchool = Json.writes[IMSchool]
  implicit val readIMSchool = Json.reads[IMSchool]

  def show(kg: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from im_school_group where school_id={kg} and status=1 limit 1")
        .on(
          'kg -> kg.toString
        ).as(simple singleOpt)
  }

  def index(kg: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from im_school_group where school_id={kg} and status=1 limit 1")
        .on(
          'kg -> kg.toString
        ).as(simple *)
  }

  def delete(kg: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update im_school_group set status=0 where school_id={kg} and status=1")
        .on(
          'kg -> kg
        ).executeUpdate()
  }


  val simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[Option[Long]]("updated_at") ~
      get[Option[Long]]("created_at") map {
      case id ~ school ~ updated ~ created =>
        IMSchool(Some(id), school.toLong, updated, created)
    }
  }

}
