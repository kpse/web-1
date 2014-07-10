package models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import play.api.Play.current

case class MemberPhoneNumber(phone: String) {

  def isExpired(implicit expireScope: String="0") = isMemberExpired(expireScope) || schoolExpired

  def isMemberExpired(implicit expireScope: String) = DB.withConnection {
    implicit c =>
      SQL(s"select count(1) from parentinfo where member_status in ($expireScope) and status=1 and phone={phone}")
        .on('phone -> phone).as(get[Long]("count(1)") single) > 0
  }

  def schoolExpired = DB.withConnection {
    implicit c =>
      SQL("select count(1) from chargeinfo c, parentinfo p " +
        "where c.school_id=p.school_id and p.phone={phone} and c.status=0 and p.status=1")
        .on('phone -> phone).as(get[Long]("count(1)") single) > 0
  }

}

object MemberPhoneNumber {
  implicit def convertPhoneToMemberPhoneNumber(phone: String) = MemberPhoneNumber(phone)
}
