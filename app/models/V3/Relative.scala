package models.V3

import java.util.Date

import anorm.SqlParser._
import anorm._
import models.Parent
import models.helper.RangerHelper
import models.helper.TimeHelper._
import play.Logger
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class ParentExt(display_name: Option[String], social_id: Option[String], nationality: Option[String], fixed_line: Option[String], memo: Option[String]) {
  def extExists(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from parentext where base_id={base_id}")
        .on(
          'base_id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def handleExt(id: Long) = extExists(id) match {
    case true =>
      update(id)
    case false =>
      create(id)
  }

  def update(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("update parentext set display_name={display}, social_id={social_id}, " +
        "nationality={nationality}, fixed_line={fixed_line}, memo={memo} " +
        " where base_id={base_id}")
        .on(
          'base_id -> id,
          'display -> display_name,
          'social_id -> social_id,
          'nationality -> nationality,
          'fixed_line -> fixed_line,
          'memo -> memo
        ).executeUpdate()
  }

  def create(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("insert into parentext (base_id, display_name, social_id, nationality, fixed_line, memo) values (" +
        "{base_id}, {display}, {social_id}, {nationality}, {fixed_line}, {memo})")
        .on(
          'base_id -> id,
          'display -> display_name,
          'social_id -> social_id,
          'nationality -> nationality,
          'fixed_line -> fixed_line,
          'memo -> memo
        ).executeInsert()
  }
}

case class Relative(id: Option[Long], basic: Parent, ext: Option[ParentExt]) {
  def update(callback: (Parent) => Option[Parent]): Option[Relative] = DB.withTransaction {
    implicit c =>
      try {
        val updatedParent: Option[Parent] = callback(basic)
        ext foreach (_.handleExt(id.get))
        c.commit()
        Logger.info(updatedParent.toString)
        ext match {
          case Some(x) =>
            Some(Relative(updatedParent.get.id, updatedParent.get, Some(x)))
          case None =>
            Some(Relative(updatedParent.get.id, updatedParent.get, None))
        }
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }

  def create: Option[Relative] = DB.withTransaction {
    implicit c =>
      try {
        val createdParent: Option[Parent] = Parent.create(basic.school_id, basic)
        ext foreach (_.handleExt(createdParent.get.id.get))
        c.commit()
        Logger.info(createdParent.toString)
        ext match {
          case Some(x) =>
            Some(Relative(createdParent.get.id, createdParent.get, Some(x)))
          case None =>
            Some(Relative(createdParent.get.id, createdParent.get, None))
        }
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }
}

object Relative {
  implicit val writeParentExt = Json.writes[ParentExt]
  implicit val readParentExt = Json.reads[ParentExt]
  implicit val writeRelative = Json.writes[Relative]
  implicit val readRelative = Json.reads[Relative]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      val relatives: List[Relative] = SQL(s"select * from parentinfo where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
      relatives map (c => c.copy(ext = extend(c.id.get)))
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      val relative: Option[Relative] = SQL(s"select * from parentinfo where status=1 and uid={id}")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
      relative map (c => c.copy(ext = extend(c.id.get)))
  }

  def extend(id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from parentext where base_id = {id}")
        .on(
          'id -> id
        ).as(simpleExt singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update parentinfo set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  def removeDirtyDataIfExists(r: Parent) = DB.withConnection {
    implicit c =>
      val deletableCondition: String = " where status=0 and (phone={phone} or parent_id={id}) "
      val execute: Boolean = SQL(s"delete from accountinfo where accountid={phone}")
        .on(
          'phone -> r.phone
        ).execute()
      val execute1: Boolean = SQL(s"delete from parentext where base_id in (select uid from parentinfo $deletableCondition)")
        .on(
          'id -> r.parent_id.getOrElse(""),
          'phone -> r.phone
        ).execute()
      val execute2: Boolean = SQL(s"delete from parentinfo $deletableCondition")
        .on(
          'id -> r.parent_id.getOrElse(""),
          'phone -> r.phone
        ).execute()
      Logger.info(s"relative removeDirtyDataIfExists $r $execute $execute1 $execute2")
  }

  val simple = {
    get[Long]("uid") ~
      get[String]("parent_id") ~
      get[String]("school_id") ~
      get[String]("parentinfo.name") ~
      get[String]("phone") ~
      get[Int]("parentinfo.gender") ~
      get[Option[String]]("parentinfo.picurl") ~
      get[Date]("parentinfo.birthday") ~
      get[Int]("member_status") ~
      get[Int]("parentinfo.status") ~
      get[Option[String]]("parentinfo.company") ~
      get[Long]("parentinfo.update_at") ~
      get[Long]("parentinfo.created_at") map {
      case uid ~ id ~ kg ~ name ~ phone ~ gender ~ portrait ~ birthday ~ member ~ status ~ company ~ t ~ created =>
        val info: Parent = Parent(Some(id), kg.toLong, name, phone, Some(portrait.getOrElse("")), gender, birthday.toDateOnly, Some(t), Some(member), Some(status), company, None, Some(created), Some(uid))
        Relative(Some(uid), info, None)
    }
  }

  val simpleExt = {
    get[Option[String]]("display_name") ~
      get[Option[String]]("social_id") ~
      get[Option[String]]("nationality") ~
      get[Option[String]]("fixed_line") ~
      get[Option[String]]("memo") map {
      case display ~ socialId ~ nationality ~ fixedLine ~ memo =>
        ParentExt(display, socialId, nationality, fixedLine, memo)
    }
  }

}
