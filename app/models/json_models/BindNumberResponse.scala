package models.json_models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import play.api.Logger
import play.api.Play.current
import java.sql.Date
import models.helper.MD5Helper.md5

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

  def schoolExpired(kg: String) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from chargeinfo where status=1 and school_id={kg}")
      .on('kg -> kg).as(get[Long]("count(1)") single) == 0l
  }

  def handle(request: BindingNumber) = DB.withConnection {
    implicit c =>
      val firstRow = SQL("select a.*, p.name, p.school_id, s.name " +
        "from accountinfo a, parentinfo p, schoolinfo s " +
        "where s.school_id=p.school_id and a.accountid = p.phone " +
        "and accountid={accountid} and pwd_change_time={token}")
        .on(
          'accountid -> request.phonenum,
          'token -> request.access_token
        ).apply
      Logger.info(firstRow.toString)
      val updateTime = System.currentTimeMillis
      firstRow match {
        case row if row.isEmpty =>
          new BindNumberResponse(1, "", "", "", 0, "")
        case row if isExpired(row(0)[String]("accountid")) || schoolExpired(row(0)[String]("school_id")) =>
          new BindNumberResponse(2, "", "", "", 0, "")
        case row if row(0)[Int]("active") == 0 =>
          SQL("update accountinfo set password={new_password}, pwd_change_time={timestamp}, pushid={pushid}, device={device}, active=1 where accountid={accountid}")
            .on('accountid -> request.phonenum,
              'new_password -> generateNewPassword(request.phonenum),
              'pushid -> request.user_id,
              'timestamp -> updateTime,
              'device -> convertToCode(request.device_type)
            ).executeUpdate
          Logger.info("binding: first activate..phone: %s at %s".format(request.phonenum, new Date(updateTime).toString))
          new BindNumberResponse(0, updateTime.toString, row(0)[String]("parentinfo.name"), request.phonenum, row(0)[String]("school_id").toLong, row(0)[String]("schoolinfo.name"))
        case row if row(0)[Int]("active") == 1 =>
          SQL("update accountinfo set pushid={pushid}, device={device}, active=1, pwd_change_time={timestamp} " +
            "where accountid={accountid}")
            .on('accountid -> request.phonenum,
              'pushid -> request.user_id,
              'timestamp -> updateTime,
              'device -> convertToCode(request.device_type)
            ).executeUpdate
          Logger.info("binding: refresh token %s..phone: %s".format(request.user_id, request.phonenum))
          new BindNumberResponse(0, updateTime.toString, row(0)[String]("parentinfo.name"), request.phonenum, row(0)[String]("school_id").toLong, row(0)[String]("schoolinfo.name"))
      }

  }
}