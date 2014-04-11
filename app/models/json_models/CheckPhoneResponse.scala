package models.json_models

import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._

case class CheckPhone(phonenum: String)

case class CheckPhoneResponse(check_phone_result: String)

object CheckPhoneResponse {
  def handle(request: CheckPhone) = DB.withConnection {
    implicit c =>
      val result = SQL("select active from accountinfo a, parentinfo p " +
        "where p.phone=a.accountid and p.member_status=1 and p.status=1 and accountid={phone}")
        .on('phone -> request.phonenum).as(get[Int]("active") singleOpt)
      result match {
        case Some(0) =>
          //todo: quick fix of number expire issue
          //new CheckPhoneResponse("1101")
          new CheckPhoneResponse("1102")
        case Some(x) =>
          new CheckPhoneResponse("1102")
        case None =>
          val numberExists = SQL("select count(1) from accountinfo where accountid={phone}").on('phone -> request.phonenum).as(get[Long]("count(1)") single)
          numberExists match {
            case 0 =>
              new CheckPhoneResponse("1100")
            case 1 =>
              new CheckPhoneResponse("1101")
          }
      }
  }

}
