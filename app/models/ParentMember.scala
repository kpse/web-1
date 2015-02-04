package models

import anorm.SqlParser._
import anorm._
import play.Logger
import play.api.db.DB
import play.api.Play.current
import anorm.~
import play.api.libs.json.Json

case class ParentMember(phone: String, sms_enabled_at: Long, received_at: Option[Long] = None) {
  def disable: Option[Parent] = DB.withTransaction {
    implicit c =>
      var parent: Option[Parent] = None
      try {
        ParentMember.delete(phone)
        parent = Parent.disableMember(phone)
        c.commit()
      } catch {
        case t: Throwable =>
          Logger.info(t.toString)
          Logger.info(t.getLocalizedMessage)
          c.rollback()
      }
      parent
  }


  def save = DB.withConnection {
    implicit c =>
      SQL("INSERT INTO membershiprecords (phone, sms_enabled_at, received_at) VALUES ({phone}, {time}, {receivedAt}) ")
        .on('phone -> phone, 'time -> sms_enabled_at, 'receivedAt -> System.currentTimeMillis).executeInsert()
  }

  def enable: Option[Parent] = DB.withTransaction {
    implicit c =>
      var parent: Option[Parent] = None
      try {
        save
        parent = Parent.enableMember(phone)
        c.commit()
      } catch {
        case t: Throwable =>
          Logger.info(t.toString)
          Logger.info(t.getLocalizedMessage)
          c.rollback()
      }
      parent
  }
}

object ParentMember {
  def delete(phone: String) = DB.withConnection {
    implicit c =>
      SQL("delete from membershiprecords where phone={phone}")
        .on('phone -> phone).execute()
  }


  val simple = {
    get[String]("phone") ~
      get[Long]("sms_enabled_at") ~
      get[Option[Long]]("sms_enabled_at") map {
      case phone ~ sms ~ receive =>
        ParentMember(phone, sms, receive)
    }
  }

  def phoneSearch(phone: String) = DB.withConnection {
    implicit c =>
      SQL("SELECT * FROM membershiprecords where phone={phone}")
        .on('phone -> phone).as(simple singleOpt)
  }

  implicit val parentMemberReader = Json.reads[ParentMember]

}
