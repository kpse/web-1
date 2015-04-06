package models.json_models

import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._
import anorm.~
import org.joda.time.DateTime
import models.{Advertisement, DailyLog}
import play.api.libs.json.Json

/*  check_type & notice_type
 0  enter school
 1  leave school
 10 get on bus at morning
 11 get off bus at morning
 12 get on bus at afternoon
 13 get off bus at afternoon
*/

case class CheckInfo(school_id: Long, card_no: String, card_type: Int, notice_type: Int, record_url: String, timestamp: Long)

case class CheckChildInfo(school_id: Long, child_id: String, check_type: Int, timestamp: Long) {
  def toCheckInfo: CheckInfo =
    CheckInfo(school_id, "", 0, check_type, "", System.currentTimeMillis)
}

case class CheckNotification(timestamp: Long, notice_type: Int, child_id: String, pushid: String, channelid: String, record_url: String, parent_name: String, device: Int, aps: Option[IOSField], ad: Option[String] = None)

case class IOSField(alert: String, sound: String = "", badge: Int = 1)

object CheckingMessage {
  implicit val checkChildInfoReads = Json.reads[CheckChildInfo]
  implicit val checkChildInfoWrites = Json.writes[CheckChildInfo]
  implicit val checkInfoReads = Json.reads[CheckInfo]
  implicit val checkInfoWrites = Json.writes[CheckInfo]

  def convert(request: CheckInfo): List[CheckNotification] = DB.withConnection {
    implicit c =>
      def generateNotice(childName: String): IOSField = {
        IOSField("%s提醒您：您的孩子 %s 已于 %s 打卡%s。".format(advertisementOf(request.school_id), childName,
          new DateTime(request.timestamp).toString("HH:mm:ss"),
          if (request.notice_type == 1) "入园" else "离开幼儿园"))
      }

      val simpleCheck = {
        get[String]("child_id") ~
          get[String]("pushid") ~
          get[String]("channelid") ~
          get[String]("parent_name") ~
          get[String]("childinfo.name") ~
          get[Int]("device") map {
          case child_id ~ pushid ~ channelid ~ name ~ childName ~ 3 =>
            CheckNotification(request.timestamp, request.notice_type, child_id, pushid, channelid, request.record_url, name, 3, None, Some(advertisementOf(request.school_id)))
          case child_id ~ pushid ~ channelid ~ name ~ childName ~ 4 =>
            CheckNotification(request.timestamp, request.notice_type, child_id, pushid, channelid, request.record_url, name, 4,
              Some(generateNotice(childName)), Some(advertisementOf(request.school_id)))
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
          'card_num -> request.card_no
        ).as(simpleCheck *)
  }

  def convertChildCheck(request: CheckChildInfo) = DB.withConnection {
    implicit c =>
      def generateNotice(childName: String): IOSField = {
        IOSField("%s提醒您：您的孩子 %s 已于 %s %s。".format(advertisementOf(request.school_id), childName,
          new DateTime(request.timestamp).toString("HH:mm:ss"),
          if (request.check_type == 11) "下车" else "上车"))
      }

      val simpleChildCheck = {
        get[String]("child_id") ~
          get[String]("pushid") ~
          get[String]("channelid") ~
          get[String]("parent_name") ~
          get[String]("childinfo.name") ~
          get[Int]("device") map {
          case child_id ~ pushid ~ channelid ~ name ~ childName ~ 3 =>
            CheckNotification(request.timestamp, request.check_type, child_id, pushid, channelid, "", name, 3, None, Some(advertisementOf(request.school_id)))
          case child_id ~ pushid ~ channelid ~ name ~ childName ~ 4 =>
            CheckNotification(request.timestamp, request.check_type, child_id, pushid, channelid, "", name, 4,
              Some(generateNotice(childName)), Some(advertisementOf(request.school_id)))
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
          'child_id -> request.child_id
        ).as(simpleChildCheck *)
  }


  def advertisementOf(schoolId: Long): String = Advertisement.index(schoolId) match {
    case x :: xs => x.name
    case _ => "幼乐宝"
  }
}


