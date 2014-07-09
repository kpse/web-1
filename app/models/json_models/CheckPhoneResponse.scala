package models.json_models

import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class CheckPhone(phonenum: String)

case class CheckPhoneResponse(check_phone_result: String)


case class PushableNumber(phone: String) {

  def existsInPushRecord = DB.withConnection {
    implicit c =>
      SQL("select count(1) from accountinfo where accountid={phone}")
        .on('phone -> phone).as(get[Long]("count(1)") single) > 0
  }
}

object Check {

  implicit def convertPhoneToPushableNumber(phone: String) = PushableNumber(phone)

  def apply(request: CheckPhone) = DB.withConnection {
    implicit c =>
      val result = SQL("select active from accountinfo a, parentinfo p " +
        "where p.phone=a.accountid and p.member_status=1 and p.status=1 and accountid={phone}")
        .on('phone -> request.phonenum).as(get[Int]("active") singleOpt)
      result match {
        case Some(x) =>
          CheckPhoneResponse("1102")
        case None =>
          request.phonenum.existsInPushRecord match {
            case false =>
              CheckPhoneResponse("1100")
            case true =>
              CheckPhoneResponse("1101")
          }
      }
  }



}
