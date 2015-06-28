package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class UserPrivilege(id: Option[Long], user_id: Option[Long], privilege_id: Option[Long], memo: Option[String]) {
  def exists(id: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from localuserprivilege where uid={id}")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(id: Long) = exists(id) match {
    case true =>
      update(id)
    case false =>
      create(id)
  }


  def update(kg: Long): Option[UserPrivilege] = DB.withConnection {
    implicit c =>
      SQL("update localuserprivilege set school_id={school_id}, user_id={user_id}, privilege_id={privilege_id}, " +
        "memo={memo}, updated_at={time} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'user_id -> user_id,
          'privilege_id -> privilege_id,
          'memo -> memo,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (UserPrivilege.show(kg, _))
  }

  def create(kg: Long): Option[UserPrivilege] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into localuserprivilege (school_id, user_id, privilege_id, memo, updated_at) values (" +
        "{school_id}, {user_id}, {privilege_id}, {memo}, {time})")
        .on(
          'school_id -> kg,
          'user_id -> user_id,
          'privilege_id -> privilege_id,
          'memo -> memo,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (UserPrivilege.show(kg, _))
  }
}

object UserPrivilege {
  implicit val writeUserPrivilege = Json.writes[UserPrivilege]
  implicit val readUserPrivilege = Json.reads[UserPrivilege]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from localuserprivilege where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from localuserprivilege where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update localuserprivilege set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  def simple = {
    get[Long]("uid") ~
      get[Option[Long]]("user_id") ~
      get[Option[Long]]("privilege_id") ~
      get[Option[String]]("memo") map {
      case id ~ user ~ p ~ memo =>
        UserPrivilege(Some(id), user, p, memo)
    }
  }
}
