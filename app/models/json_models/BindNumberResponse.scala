package models.json_models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import play.api.Logger
import play.api.Play.current
import models.helper.MD5Helper.md5
import anorm.~
import scala.Some

case class BindingNumber(phonenum: String, user_id: String, channel_id: String, device_type: Option[String], access_token: String)

case class BindNumberResponse(error_code: Int,
                              access_token: String,
                              username: String,
                              account_name: String,
                              school_id: Long, school_name: String)

object BindNumberResponse {

  def generateNewPassword(number: String) = md5(number.drop(3))

  def convertToCode(deviceType: Option[String]) = {
    //device_type => 1: web 2: pc 3:android 4:ios 5:wp
    deviceType match {
      case Some(ios) if ios.equalsIgnoreCase("ios") => 4
      case _ => 3
    }
  }

  def isExpired(phone: String) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from parentinfo where member_status=1 and status=1 and phone={phone}")
        .on('phone -> phone).as(get[Long]("count(1)") single) == 0l
  }

  def schoolExpired(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from chargeinfo where status=1 and school_id={kg}")
        .on('kg -> kg.toString).as(get[Long]("count(1)") single) == 0l
  }


  def wrongToken(phone: String): Boolean = DB.withConnection {
    implicit c =>
      SQL("select count(1) " +
        "from accountinfo a, parentinfo p, chargeinfo c " +
        "where c.school_id=p.school_id and a.accountid = p.phone and p.status=1 and c.status=1" +
        "and accountid={accountid}")
        .on(
          'accountid -> phone
        ).as(get[Long]("count(1)") single) > 0
  }

  def response(updateTime: String) = {
    get[String]("accountid") ~
      get[String]("parentinfo.name") ~
      get[String]("school_id") ~
      get[String]("schoolinfo.name") ~
      get[Int]("active") map {
      case accountid ~ parent ~ schoolId ~ schoolName ~ active =>
        new BindNumberResponse(0, updateTime, parent, accountid, schoolId.toLong, schoolName)
    }
  }

  def handle(request: BindingNumber) = DB.withConnection {
    implicit c =>
      val updateTime = System.currentTimeMillis
      val row = SQL("select a.*, p.name, p.school_id, s.name " +
        "from accountinfo a, parentinfo p, schoolinfo s, chargeinfo c " +
        "where s.school_id=p.school_id and a.accountid = p.phone and c.school_id=p.school_id " +
        "and c.status=1 and p.status=1 " +
        "and accountid={accountid} and pwd_change_time={token}")
        .on(
          'accountid -> request.phonenum,
          'token -> request.access_token.toLong
        ).as(response(updateTime.toString) singleOpt)
      Logger.info(row.toString)
      row match {
        case None if wrongToken(request.phonenum) =>
          new BindNumberResponse(3, "", "", "", 0, "")
        case None =>
          new BindNumberResponse(1, "", "", "", 0, "")
        case Some(r) if isExpired(r.account_name) || schoolExpired(r.school_id) =>
          new BindNumberResponse(2, "", "", "", 0, "")
        case Some(r) =>
          updateTokenAfterBinding(request, updateTime)
          r
      }
  }

  def updateTokenAfterBinding(request: BindingNumber, updateTime: Long) = DB.withConnection {
    implicit c =>
      SQL("update accountinfo set pushid={pushid}, device={device}, active=1, pwd_change_time={timestamp} " +
        "where accountid={accountid}")
        .on('accountid -> request.phonenum,
          'pushid -> request.user_id,
          'timestamp -> updateTime,
          'device -> convertToCode(request.device_type)
        ).executeUpdate
  }
}