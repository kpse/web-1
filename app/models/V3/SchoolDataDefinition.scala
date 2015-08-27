package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class SchoolDataDefinition(id: Option[Long], school_id: Long, name: String, `type`: String, created_at: Option[Long]) {
  def update(kg: Long): Option[SchoolDataDefinition] = DB.withConnection {
    implicit c =>
      SQL("update datadefinition set name={name}, type={type}, created_at={time} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'name -> name,
          'type -> `type`,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (SchoolDataDefinition.show(kg, _))
  }

  def create(kg: Long): Option[SchoolDataDefinition] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into datadefinition (school_id, name, type, created_at) values (" +
        "{school_id}, {name}, {type}, {time})")
        .on(
          'school_id -> kg,
          'name -> name,
          'type -> `type`,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (SchoolDataDefinition.show(kg, _))
  }
}

object SchoolDataDefinition {
  implicit val readSchoolDataDefinition = Json.reads[SchoolDataDefinition]
  implicit val writeSchoolDataDefinition = Json.writes[SchoolDataDefinition]

  def show(kg: Long, id: Long): Option[SchoolDataDefinition] = DB.withConnection {
    implicit c =>
      SQL(s"select * from datadefinition where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]): List[SchoolDataDefinition] = DB.withConnection {
    implicit c =>
      SQL(s"select * from datadefinition where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update datadefinition set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  def simple = {
    get[Long]("uid") ~
    get[String]("school_id") ~
      get[String]("name") ~
      get[String]("type") ~
      get[Option[Long]]("created_at") map {
      case id ~ kg ~ name ~ typ ~ time =>
        SchoolDataDefinition(Some(id), kg.toLong, name, typ, time)
    }
  }

}
