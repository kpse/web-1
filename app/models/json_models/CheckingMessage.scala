package models.json_models

import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._
import anorm.~
import org.joda.time.DateTime
import models.DailyLog

case class CheckInfo(school_id: Long, card_no: String, card_type: Int, notice_type: Int, record_url: String, timestamp: Long)

case class CheckNotification(timestamp: Long, notice_type: Int, child_id: String, pushid: String, record_url: String, parent_name: String, device: Int, aps: Option[IOSField])

case class IOSField(alert: String, sound: String = "", badge: Int = 1)

object CheckingMessage {



  def convert(request: CheckInfo): List[CheckNotification] = DB.withConnection {
    implicit c =>
      def generateNotice(childName: String): IOSField = {
        IOSField("您的孩子 %s 已于 %s 打卡%s。".format(childName,
          new DateTime(request.timestamp).toString("HH:mm:ss"),
          if (request.notice_type==1) "入园" else "离开幼儿园"))
      }
      val simple = {
        get[String]("child_id") ~
          get[String]("pushid") ~
          get[String]("parent_name") ~
          get[String]("childinfo.name") ~
          get[Int]("device") map {
          case child_id ~ pushid ~ name ~ childName ~ 3  =>
            CheckNotification(request.timestamp, request.notice_type, child_id, pushid, request.record_url, name, 3, None)
          case child_id ~ pushid ~ name ~ childName ~ 4  =>
            CheckNotification(request.timestamp, request.notice_type, child_id, pushid, request.record_url, name, 4,
              Some(generateNotice(childName)))
        }

      }
      SQL(
        """
          |select a.pushid, c.child_id, c.name,
          |  (select p.name from parentinfo p, relationmap r where p.parent_id = r.parent_id and r.card_num={card_num}) as parent_name,
          |  a.device from accountinfo a, childinfo c, parentinfo p, relationmap r
          |where p.parent_id = r.parent_id and r.child_id = c.child_id and
          |p.phone = a.accountid and c.child_id in (select child_id from relationmap r where r.card_num={card_num})
        """.stripMargin)
        .on(
          'card_num -> request.card_no
        ).as(simple *)
  }
}


