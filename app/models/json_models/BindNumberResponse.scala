package models.json_models

import anorm.SqlParser._
import anorm.{~, _}
import models.MemberPhoneNumber.convertPhoneToMemberPhoneNumber
import models.PushableNumber._
import play.api.Logger
import play.api.Play.current
import play.api.db.DB

case class BindingNumber(phonenum: String, user_id: String, channel_id: String, device_type: Option[String], access_token: String)

case class BindNumberResponse(error_code: Int,
                              access_token: String = "",
                              username: String = "",
                              account_name: String = "",
                              school_id: Long = 0, school_name: String = "")

object Binding {

  def response(updateTime: String) = {
    get[String]("accountid") ~
      get[String]("parentinfo.name") ~
      get[String]("school_id") ~
      get[String]("schoolinfo.name") ~
      get[Int]("active") map {
      case accountid ~ parent ~ schoolId ~ schoolName ~ active =>
        BindNumberResponse(0, updateTime, parent, accountid, schoolId.toLong, schoolName)
    }
  }
  @Deprecated
  def apply(request: BindingNumber) = DB.withConnection {
    implicit c =>
      val row = SQL("select a.*, p.name, p.school_id, s.name " +
        "from accountinfo a, parentinfo p, schoolinfo s, chargeinfo c " +
        "where s.school_id=p.school_id and a.accountid = p.phone and c.school_id=p.school_id " +
        "and c.status=1 and p.status=1 and member_status=1 " +
        "and accountid={accountid} and pwd_change_time={token}")
        .on(
          'accountid -> request.phonenum,
          'token -> request.access_token.toLong
        ).as(response(request.access_token) singleOpt)
      Logger.info(row.toString)
      row match {
        case Some(r) =>
          updateTokenAfterBinding(request)
          r
        case res if res.isEmpty && request.phonenum.existsDisregardingToken =>
          BindNumberResponse(3)
        case res if res.isEmpty && request.phonenum.isExpired =>
          BindNumberResponse(2)
        case None =>
          BindNumberResponse(1)
      }
  }


}

