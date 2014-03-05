package models

import play.api.db.DB
import anorm._
import anorm.SqlParser._
import anorm.~
import models.json_models.CheckNotification
import models.json_models.CheckInfo
import play.api.Play.current
import models.helper.RangerHelper.rangerQueryWithField

object DailyLog {
  val simple = {
    get[String]("child_id") ~
      get[Long]("check_at") ~
      get[String]("record_url") ~
      get[String]("pushid") ~
      get[Int]("notice_type") ~
      get[String]("parent_name") ~
      get[Int]("device") map {
      case child_id ~ timestamp ~ url ~ pushid ~ notice_type ~ name ~ device =>
        new CheckNotification(timestamp, notice_type, child_id, pushid, url, name, device)
    }
  }

  def all(kg: Long, parentId: String, childId: String, from: Option[Long], to: Option[Long]) = DB.withConnection {
    implicit c =>
      SQL("select * from dailylog where child_id={child_id} and school_id={school_id} " + rangerQueryWithField(from, to, Some("check_at")))
        .on(
          'child_id -> childId,
          'school_id -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
  }


  def create(check: CheckNotification, request: CheckInfo) = DB.withConnection {
    implicit c =>
      SQL("insert into dailylog (child_id, pushid, record_url, check_at, card_no, notice_type, school_id, parent_name, device) " +
        "values ({child_id}, {pushid}, {url}, {check_at}, {card_no}, {notice_type}, {school_id}, {parent_name}, {device})")
        .on(
          'child_id -> check.child_id,
          'pushid -> check.pushid,
          'url -> check.record_url,
          'check_at -> check.timestamp,
          'card_no -> request.card_no,
          'notice_type -> request.notice_type,
          'school_id -> request.school_id,
          'parent_name -> check.parent_name,
          'device -> check.device
        ).executeInsert()
      check
  }

}
