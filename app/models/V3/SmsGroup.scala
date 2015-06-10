package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class SmsGroupMember(id: Option[Long], name: Option[String], phone: Option[String]) {
  def create(kg: Long, group: Long): Option[Long] = DB.withConnection {
    implicit c =>
      SQL("insert into smsgroupmember (school_id, group_id, name, phone) values (" +
        "{school_id}, {group}, {name}, {phone})")
        .on(
          'school_id -> kg,
          'group -> group,
          'name -> name,
          'phone -> phone
        ).executeInsert()
  }
}

case class SmsGroup(id: Option[Long], name: Option[String], members: List[SmsGroupMember]) {
  def exists(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from smsgroup where uid={id}")
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

  def handleMembers(kg: Long, group: Long, members: List[SmsGroupMember]) = DB.withConnection {
    implicit c =>
      SQL("delete from smsgroupmember where group_id={group} and school_id={school_id}")
        .on(
          'school_id -> kg,
          'group -> group
        ).execute()
      members foreach (_.create(kg, group))
  }

  def update(kg: Long): Option[SmsGroup] = DB.withTransaction {
    implicit c =>
      try {
        SQL("update smsgroup set name={name} where school_id={school_id} and uid={id}")
          .on(
            'id -> id,
            'school_id -> kg,
            'name -> name
          ).executeUpdate()
        id foreach (handleMembers(kg, _, members))
        c.commit()
        SmsGroup.show(kg, id.getOrElse(0))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }

  def create(kg: Long): Option[SmsGroup] = DB.withTransaction {
    implicit c =>
      try {
        val insert: Option[Long] = SQL("insert into smsgroup (school_id, name, created_at) values (" +
          "{school_id}, {name}, {time})")
          .on(
            'school_id -> kg,
            'name -> name,
            'time -> System.currentTimeMillis
          ).executeInsert()
        insert foreach (handleMembers(kg, _, members))
        c.commit()
        SmsGroup.show(kg, insert.getOrElse(0))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }
}

object SmsGroup {

  implicit val writeSmsGroupMember = Json.writes[SmsGroupMember]
  implicit val readSmsGroupMember = Json.reads[SmsGroupMember]
  implicit val writeSmsGroup = Json.writes[SmsGroup]
  implicit val readSmsGroup = Json.reads[SmsGroup]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from smsgroup where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple(kg) *)
  }

  def memberOf(kg: Long, group: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from smsgroupmember where school_id={kg} and group_id={group}")
        .on(
          'kg -> kg.toString,
          'group -> group
        ).as(member *)
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from smsgroup where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple(kg) singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update smsgroup set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  def simple(kg: Long) = {
    get[Long]("uid") ~
      get[Option[String]]("name") map {
      case id ~ name =>
        SmsGroup(Some(id), name, memberOf(kg, id))
    }
  }

  val member = {
    get[Long]("uid") ~
      get[Option[String]]("name") ~
      get[Option[String]]("phone") map {
      case id ~ name ~ phone =>
        SmsGroupMember(Some(id), name, phone)
    }
  }
}