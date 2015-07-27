package models.json_models

import anorm.SqlParser._
import anorm.{~, _}
import models.Advertisement
import org.joda.time.DateTime
import play.Logger
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

/*  check_type & notice_type
 0  enter school
 1  leave school
 10 get on bus at morning
 11 get off bus at morning
 12 get on bus at afternoon
 13 get off bus at afternoon
*/

case class CheckInfo(school_id: Long, card_no: String, card_type: Int, notice_type: Int, record_url: String, timestamp: Long, id: Option[Long] = None) {
  def toNotifications: List[CheckNotification] = DB.withConnection {
    implicit c =>
      def generateNotice(childName: String): IOSField = {
        IOSField("%s提醒您：您的孩子 %s 已于 %s 打卡%s。".format(CheckingMessage.advertisementOf(school_id), childName,
          new DateTime(timestamp).toString("HH:mm:ss"),
          notice_type match {
            case 1 => "入园"
            case 0 => "离开幼儿园"
            case 10 => "上车"
            case 13 => "下车"
          }))
      }

      val simpleCheck = {
        get[String]("child_id") ~
          get[String]("pushid") ~
          get[String]("channelid") ~
          get[String]("parent_name") ~
          get[String]("childinfo.name") ~
          get[Int]("device") map {
          case child_id ~ pushid ~ channelid ~ name ~ childName ~ 3 =>
            CheckNotification(timestamp, notice_type, child_id, pushid, channelid, record_url, name, 3, None, Some(CheckingMessage.advertisementOf(school_id)))
          case child_id ~ pushid ~ channelid ~ name ~ childName ~ 4 =>
            CheckNotification(timestamp, notice_type, child_id, pushid, channelid, record_url, name, 4,
              Some(generateNotice(childName)), Some(CheckingMessage.advertisementOf(school_id)))
        }

      }
      SQL(
        """
          |select a.pushid, c.child_id, c.name, a.channelid,
          |  (select p.name from parentinfo p, relationmap r where p.parent_id = r.parent_id and r.card_num={card_num}) as parent_name,
          |  a.device from accountinfo a, childinfo c, parentinfo p, relationmap r
          |where p.parent_id = r.parent_id and r.child_id = c.child_id and
          |p.phone = a.accountid and c.child_id in (select child_id from relationmap r where r.card_num={card_num})
        """.stripMargin)
        .on(
          'card_num -> card_no
        ).as(simpleCheck *)
  }
  def create = DB.withTransaction {
    implicit c =>
      save(toNotifications)
  }

  def save(notifications: List[CheckNotification]): Option[Long] = DB.withTransaction {
    implicit c =>
      Logger.info(s"messages in saving checking info: ${notifications}")
      notifications match {
        case x::xs =>
          SQL("insert into dailylog (child_id, record_url, check_at, card_no, notice_type, school_id, parent_name) " +
            "values ({child_id}, {url}, {check_at}, {card_no}, {notice_type}, {school_id}, {parent_name})")
            .on(
              'child_id -> x.child_id,
              'url -> x.record_url,
              'check_at -> x.timestamp,
              'card_no -> card_no,
              'notice_type -> notice_type,
              'school_id -> school_id,
              'parent_name -> x.parent_name
            ).executeInsert()
        case _ => None
      }
  }
}

case class CheckChildInfo(school_id: Long, child_id: String, check_type: Int, timestamp: Long) {
  def create = toCheckInfo.create

  def toCheckInfo: CheckInfo =
    CheckInfo(school_id, "", 0, check_type, "", System.currentTimeMillis)

  def toNotifications = DB.withConnection {
    implicit c =>
      def generateNotice(childName: String): IOSField = {
        IOSField("%s提醒您：您的孩子 %s 已于 %s %s。".format(CheckingMessage.advertisementOf(school_id), childName,
          new DateTime(timestamp).toString("HH:mm:ss"),
          if (check_type == 11) "下车" else "上车"))
      }

      val simpleChildCheck = {
        get[String]("child_id") ~
          get[String]("pushid") ~
          get[String]("channelid") ~
          get[String]("parent_name") ~
          get[String]("childinfo.name") ~
          get[Int]("device") map {
          case child_id ~ pushid ~ channelid ~ name ~ childName ~ 3 =>
            CheckNotification(timestamp, check_type, child_id, pushid, channelid, "", name, 3, None, Some(CheckingMessage.advertisementOf(school_id)))
          case child_id ~ pushid ~ channelid ~ name ~ childName ~ 4 =>
            CheckNotification(timestamp, check_type, child_id, pushid, channelid, "", name, 4,
              Some(generateNotice(childName)), Some(CheckingMessage.advertisementOf(school_id)))
        }

      }
      SQL(
        """
          |select a.pushid, c.child_id, c.name, a.channelid, '' as parent_name,
          |  a.device from accountinfo a, childinfo c, parentinfo p, relationmap r
          |where p.parent_id = r.parent_id and r.child_id = c.child_id and
          |p.phone = a.accountid and c.child_id={child_id}
        """.stripMargin)
        .on(
          'child_id -> child_id
        ).as(simpleChildCheck *)
  }
}

case class CheckNotification(timestamp: Long, notice_type: Int, child_id: String, pushid: String, channelid: String, record_url: String, parent_name: String, device: Int, aps: Option[IOSField], ad: Option[String] = None)

case class IOSField(alert: String, sound: String = "", badge: Int = 1)

object CheckingMessage {
  def convert(check: CheckInfo) = check.toNotifications
  def convertChildCheck(check: CheckChildInfo) = check.toNotifications

  implicit val checkChildInfoReads = Json.reads[CheckChildInfo]
  implicit val checkChildInfoWrites = Json.writes[CheckChildInfo]
  implicit val checkInfoReads = Json.reads[CheckInfo]
  implicit val checkInfoWrites = Json.writes[CheckInfo]

  def advertisementOf(schoolId: Long): String = Advertisement.index(schoolId) match {
    case x :: xs => x.name
    case _ => "幼乐宝"
  }
}


