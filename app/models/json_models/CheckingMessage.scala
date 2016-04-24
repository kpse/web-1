package models.json_models

import java.sql.Connection

import anorm.SqlParser._
import anorm.{~, _}
import models.Advertisement
import models.V3.CheckingRecordV3
import org.joda.time.DateTime
import play.api.Logger
import play.api.Play.current
import play.api.cache.Cache
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

case class EmployeeCheckInfo(school_id: Long, employee_id: Long, card_no: String, card_type: Int, notice_type: Int, record_url: String, timestamp: Long, id: Option[Long] = None) {
  def create = DB.withConnection {
    implicit c =>
      SQL("insert into employeedailylog (employee_id, record_url, checked_at, card, notice_type, card_type, school_id) " +
        "values ({employee_id}, {url}, {check_at}, {card}, {notice_type}, {card_type}, {school_id})")
        .on(
          'employee_id -> employee_id,
          'url -> record_url,
          'check_at -> timestamp,
          'card -> card_no,
          'notice_type -> notice_type,
          'card_type -> card_type,
          'school_id -> school_id
        ).executeInsert()
  }
}

case class CheckInfo(school_id: Long, card_no: String, card_type: Int, notice_type: Int, record_url: String, timestamp: Long, id: Option[Long] = None) {
  private val logger: Logger = Logger(classOf[CheckInfo])

  def toNotifications(implicit connection: Connection): List[CheckNotification] = {
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
        get[String]("accountid") ~
        get[Int]("device") map {
        case child_id ~ pushid ~ channelid ~ name ~ childName ~ phone ~ 3 =>
          CheckNotification(timestamp, notice_type, child_id, pushid, channelid, record_url, name, 3, Some(generateNotice(childName)), Some(CheckingMessage.advertisementOf(school_id)), Some(phone))
        case child_id ~ pushid ~ channelid ~ name ~ childName ~ phone ~ 4 =>
          CheckNotification(timestamp, notice_type, child_id, pushid, channelid, record_url, name, 4,
            Some(generateNotice(childName)), Some(CheckingMessage.advertisementOf(school_id)), Some(phone))
      }

    }
    SQL(
      """
        | select a.pushid, c.child_id, c.name, a.channelid,
        |  (select p.name from parentinfo p, relationmap r where p.parent_id = r.parent_id and r.card_num={card_num} limit 1) as parent_name,
        |  a.device, a.accountid from accountinfo a, childinfo c, parentinfo p, relationmap r
        | where p.parent_id = r.parent_id and r.child_id = c.child_id and p.school_id={kg} and
        | p.phone = a.accountid and c.child_id = (select child_id from relationmap r where r.card_num={card_num} limit 1)
      """.stripMargin)
      .on(
        'card_num -> card_no,
        'kg -> school_id.toString
      ).as(simpleCheck *).groupBy(_.channelid).mapValues(_.head).values.toList
  }

  def create = DB.withConnection {
    implicit c =>
      val allNotifications = toNotifications(c)
      allNotifications.take(1).foreach(_.saveToCache(school_id))
      save(allNotifications)(c)
      allNotifications
  }

  def createWithCallback(callback: (Long) => Option[CheckingRecordV3])(implicit connection: Connection): Option[CheckingRecordV3] = {
    val allNotifications = toNotifications(connection)
    allNotifications.take(1).foreach(_.saveToCache(school_id))
    save(allNotifications)(connection) flatMap callback
  }


  def save(notifications: List[CheckNotification])(implicit connection: Connection): Option[Long] = {
    logger.debug(s"messages in saving checking info: $notifications")
    notifications match {
      case x :: xs =>
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
  def create = DB.withConnection {
    implicit c =>
      val allNotifications: List[CheckNotification] = toNotifications
      allNotifications.take(1).foreach(_.saveToCache(school_id))
      toCheckInfo.save(allNotifications)
      allNotifications
  }

  def toCheckInfo: CheckInfo =
    CheckInfo(school_id, "", 0, check_type, "", System.currentTimeMillis)

  def toNotifications(implicit connection: Connection) = {
      def generateNotice(childName: String): IOSField = {
        IOSField("%s提醒您：您的孩子 %s 已于 %s %s。".format(CheckingMessage.advertisementOf(school_id), childName,
          new DateTime(timestamp).toString("HH:mm:ss"),
          if (check_type == 11) "下车入园" else "离园上车"))
      }

      val simpleChildCheck = {
        get[String]("child_id") ~
          get[String]("pushid") ~
          get[String]("channelid") ~
          get[String]("parent_name") ~
          get[String]("childinfo.name") ~
          get[String]("accountid") ~
          get[Int]("device") map {
          case childId ~ pushid ~ channelid ~ name ~ childName ~ phone ~ 3 =>
            CheckNotification(timestamp, check_type, childId, pushid, channelid, "", name, 3, None, Some(CheckingMessage.advertisementOf(school_id)), Some(phone))
          case childId ~ pushid ~ channelid ~ name ~ childName ~ phone ~ 4 =>
            CheckNotification(timestamp, check_type, childId, pushid, channelid, "", name, 4,
              Some(generateNotice(childName)), Some(CheckingMessage.advertisementOf(school_id)), Some(phone))
        }

      }
      SQL(
        """
          |select a.pushid, c.child_id, c.name, a.channelid, '' as parent_name,
          |  a.device, a.accountid from accountinfo a, childinfo c, parentinfo p, relationmap r
          |where p.parent_id = r.parent_id and r.child_id = c.child_id and
          |p.phone = a.accountid and c.child_id={child_id} and p.school_id={kg}
        """.stripMargin)
        .on(
          'child_id -> child_id,
          'kg -> school_id.toString
        ).as(simpleChildCheck *).groupBy(_.channelid).mapValues(_.head).values.toList
  }
}

case class CheckNotification(timestamp: Long, notice_type: Int, child_id: String, pushid: String, channelid: String,
                             record_url: String, parent_name: String, device: Int, aps: Option[IOSField], ad: Option[String] = None, phone: Option[String] = None) {
  private val logger: Logger = Logger(classOf[CheckNotification])
  def saveToCache(kg: Long) = {
    notice_type match {
      case 0|1|11|12 =>
        val cacheKey: String = s"dailylog_${kg}_$child_id"
        val maybeCheckNotifications: Option[List[Long]] = Cache.getAs[List[Long]](cacheKey)
        logger.debug(s"CheckNotification save to cache : ${maybeCheckNotifications}")
        maybeCheckNotifications match {
          case Some(notifications) =>
            logger.info(s"already checked $maybeCheckNotifications , $cacheKey = ${this}")
            Cache.set(cacheKey, notifications.dropWhile(_ <= DateTime.now().withTimeAtStartOfDay().getMillis) ::: List(timestamp))
          case None =>
            logger.info(s"first check today $cacheKey = ${this}")
            Cache.set(cacheKey, List(timestamp))
        }
      case _ =>
        logger.debug(s"do not cache notice_type 12, 13 : ${this}")
    }

  }
}

case class IOSField(alert: String, sound: String = "", badge: Int = 1)

object CheckingMessage {
  def convert(check: CheckInfo) = DB.withConnection { implicit c => check.toNotifications(c) }

  def convertChildCheck(check: CheckChildInfo) = DB.withConnection { implicit c => check.toNotifications(c) }

  implicit val checkChildInfoReads = Json.reads[CheckChildInfo]
  implicit val checkChildInfoWrites = Json.writes[CheckChildInfo]
  implicit val checkInfoReads = Json.reads[CheckInfo]
  implicit val checkInfoWrites = Json.writes[CheckInfo]
  implicit val readEmployeeCheckInfo = Json.reads[EmployeeCheckInfo]
  implicit val writeEmployeeCheckInfo = Json.writes[EmployeeCheckInfo]

  def advertisementOf(schoolId: Long): String = Advertisement.index(schoolId) match {
    case x :: xs => x.name
    case _ => "幼乐宝"
  }
}


