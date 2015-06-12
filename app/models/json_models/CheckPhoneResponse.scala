package models.json_models

import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import models.PushableNumber.convertPhoneToPushableNumber

case class CheckPhone(phonenum: String)

case class CheckPhoneResponse(check_phone_result: String)

object Check {

  def apply(request: CheckPhone) = DB.withConnection {
    implicit c =>
      val result = SQL("select active from accountinfo a, parentinfo p " +
        "where p.phone=a.accountid and p.member_status=1 and p.status=1 and accountid={phone}")
        .on('phone -> request.phonenum).as(get[Int]("active") singleOpt)
      result match {
        case Some(x) =>
          CheckPhoneResponse("1102")
        case None if request.phonenum.hasBeenDeleted =>
          CheckPhoneResponse ("1100")
        case None if request.phonenum.existsInPushRecord =>
          CheckPhoneResponse("1101")
        case None =>
          CheckPhoneResponse ("1100")
      }
  }

}
