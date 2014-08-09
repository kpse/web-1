package models

import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current
import anorm.SqlParser._
import anorm._
import anorm.~
import helper.MD5Helper.md5



case class RawVideoMember(account: String)
case class VideoMember(id: String, account: Option[String], password: Option[String], school_id: Option[Long]){
  def update = DB.withConnection {
    implicit c =>
      SQL("update videomembers set account={account} where parent_id={id}")
        .on('account -> generateAccount(account), 'id -> id ).executeUpdate()
  }
  def create = DB.withConnection {
    implicit c =>
      SQL("insert into videomembers (parent_id, school_id, account) " +
        "values ({id}, {kg}, {account})")
        .on('kg -> school_id, 'account -> generateAccount(account), 'id -> id ).executeInsert()
  }

  def isExisting = DB.withConnection {
    implicit c =>
      SQL("select count(1) from videomembers where school_id={kg} and parent_id={id}")
        .on('kg -> school_id, 'id -> id ).as(get[Long]("count(1)") single) > 0
  }

  def isAccountDuplicated = DB.withConnection {
    implicit c =>
      SQL("select count(1) from videomembers where account={account}")
        .on('account -> account ).as(get[Long]("count(1)") single) > 0
  }

  private def generateAccount(base: Option[String]) = {
    base match {
      case Some(key) => key
      case None => md5(s"$school_id$id")
    }
  }

}

object VideoMember {
  implicit val write = Json.writes[VideoMember]
  implicit val read = Json.reads[VideoMember]

  def all(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from videomembers where school_id={kg}").on('kg -> kg).as(simple *)
  }

  def show(kg: Long, id: String) = DB.withConnection {
    implicit c =>
      SQL("select * from videomembers where school_id={kg} and parent_id={id}")
        .on('kg -> kg, 'id -> id).as(simple singleOpt)
  }

  val simple = {
    get[String]("school_id") ~
    get[String]("parent_id") ~
    get[String]("account") map {
      case kg ~ id ~ account =>
        VideoMember(id, Some(account), Some("123"), Some(kg.toLong))
    }
  }
}
