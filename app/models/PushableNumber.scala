package models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import play.api.Play.current
import models.json_models.BindingNumber

case class PushableNumber(phone: String) {

  def existsInPushRecord = DB.withConnection {
    implicit c =>
      SQL("select count(1) from accountinfo where accountid={phone}")
        .on('phone -> phone).as(get[Long]("count(1)") single) > 0
  }

  def existsDisregardingToken(implicit memberStatusScope: String = "1"): Boolean = DB.withConnection {
    implicit c =>
      SQL("select count(1) " +
        "from accountinfo a, parentinfo p, chargeinfo c " +
        "where c.school_id=p.school_id and a.accountid = p.phone " +
        s"and p.status=1 and c.status=1 and member_status in ($memberStatusScope) " +
        "and accountid={accountid}")
        .on(
          'accountid -> phone
        ).as(get[Long]("count(1)") single) > 0
  }
}

object PushableNumber {
  implicit def convertPhoneToPushableNumber(phone: String) = PushableNumber(phone)

  def updateTokenAfterBinding(binding: BindingNumber, updateTime: Long) = DB.withConnection {
    implicit c =>
      SQL("update accountinfo set pushid={pushid}, device={device}, active=1, " +
        "pwd_change_time={timestamp} where accountid={accountid}")
        .on('accountid -> binding.phonenum,
          'pushid -> binding.user_id,
          'timestamp -> updateTime,
          'device -> convertToCode(binding.device_type)
        ).executeUpdate
  }

  def convertToCode(deviceType: Option[String]) = {
    //device_type => 1: web 2: pc 3:android 4:ios 5:wp
    deviceType match {
      case Some(ios) if ios.equalsIgnoreCase("ios") => 4
      case _ => 3
    }
  }
}
