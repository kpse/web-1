package models.json_models

import java.sql.Connection

import anorm.SqlParser._
import anorm.{~, _}
import controllers.SMSController
import models._
import models.V3.{CheckingRecordV3, Relative}
import models.helper.MD5Helper._
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
          case _ => ""
        }))
    }
    def formatSmsPushContent(parentName: String, childName: String, checkAt: Long, checkingParentName: String, checkType: Int) = {
      s"${parentName}家长，你好，你的宝宝${childName}已于${CheckingMessage.checkingTimeDisplay(new DateTime(checkAt))}由${checkingParentName}刷卡${CheckingMessage.checkingDisplayValue(checkType)}。"
    }
    val simpleCheck = {
      get[String]("child_id") ~
        get[String]("pushid") ~
        get[String]("channelid") ~
        get[String]("parent_name") ~
        get[String]("childinfo.name") ~
        get[String]("accountid") ~
        get[String]("parentinfo.name") ~
        get[Int]("device") map {
        case child_id ~ pushid ~ channelid ~ name ~ childName ~ phone ~ receiver ~ 3 =>
          CheckNotification(timestamp, notice_type, child_id, pushid, channelid, record_url, name, 3,
            Some(generateNotice(childName)), Some(CheckingMessage.advertisementOf(school_id)), Some(phone), Some(formatSmsPushContent(receiver, childName, timestamp, name, notice_type)))
        case child_id ~ pushid ~ channelid ~ name ~ childName ~ phone ~ receiver ~ 4 =>
          CheckNotification(timestamp, notice_type, child_id, pushid, channelid, record_url, name, 4,
            Some(generateNotice(childName)), Some(CheckingMessage.advertisementOf(school_id)), Some(phone), Some(formatSmsPushContent(receiver, childName, timestamp, name, notice_type)))
      }

    }
    SQL(
      """
        | select a.pushid, c.child_id, c.name, a.channelid,
        |  (select p.name from parentinfo p, relationmap r where p.parent_id = r.parent_id and r.card_num={card_num} limit 1) as parent_name,
        |  a.device, a.accountid, p.name from accountinfo a, childinfo c, parentinfo p, relationmap r
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

    def formatSmsPushContent(parentName: String, childName: String, checkAt: Long, checkType: Int) = {
      s"${parentName}家长，你好，你的宝宝${childName}已于${CheckingMessage.checkingTimeDisplay(DateTime.now())}${CheckingMessage.checkingDisplayValue(checkType)}。"
    }

    val simpleChildCheck = {
      get[String]("child_id") ~
        get[String]("pushid") ~
        get[String]("channelid") ~
        get[String]("parent_name") ~
        get[String]("childinfo.name") ~
        get[String]("accountid") ~
        get[String]("parentinfo.name") ~
        get[Int]("device") map {
        case childId ~ pushid ~ channelid ~ name ~ childName ~ phone ~ receiver ~ 3 =>
          CheckNotification(timestamp, check_type, childId, pushid, channelid, "", name, 3, None, Some(CheckingMessage.advertisementOf(school_id)), Some(phone), Some(formatSmsPushContent(receiver, childName, timestamp, check_type)))
        case childId ~ pushid ~ channelid ~ name ~ childName ~ phone ~ receiver ~ 4 =>
          CheckNotification(timestamp, check_type, childId, pushid, channelid, "", name, 4,
            Some(generateNotice(childName)), Some(CheckingMessage.advertisementOf(school_id)), Some(phone), Some(formatSmsPushContent(receiver, childName, timestamp, check_type)))
      }

    }
    SQL(
      """
        |select a.pushid, c.child_id, c.name, a.channelid, '' as parent_name,
        |  a.device, a.accountid, p.name from accountinfo a, childinfo c, parentinfo p, relationmap r
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
                             record_url: String, parent_name: String, device: Int, aps: Option[IOSField], ad: Option[String] = None, phone: Option[String] = None, smsPushContent: Option[String] = None) {
  private val logger: Logger = Logger(classOf[CheckNotification])

  def saveToCache(kg: Long) = {
    notice_type match {
      case 0 | 1 | 11 | 12 =>
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

  def checkingTimeDisplay(time: DateTime) = time.toString("yyyy-MM-dd HH:mm:ss")

  def checkingDisplayValue(checkType: Int) = checkType match {
    case 1 => "入园"
    case 0 => "离园"
    case 10 => "上车"
    case 11 => "离开校车"
    case 12 => "坐上校车"
    case 13 => "下车"
    case _ => "未知"
  }

  private def individualSmsEnabled(schoolId: Long, phone: Option[String]): Boolean = phone.isDefined && Relative.findByPhone(schoolId, phone.get).exists(_.ext.exists(_.sms_push == Some(true)))

  private def schoolSmsEnabled(schoolId: Long) = {
    SchoolConfig.valueOfKey(schoolId, "smsPushAccount").exists(_.nonEmpty) &&
      SchoolConfig.valueOfKey(schoolId, "smsPushPassword").exists(_.nonEmpty) &&
      SchoolConfig.valueOfKey(schoolId, "smsPushSignature").exists(_.nonEmpty) &&
      SchoolConfig.valueOfKey(schoolId, "switch_sms_on_card_wiped").exists(_.equalsIgnoreCase("1"))
  }

  def smsPushEnabled(schoolId: Long, message: CheckNotification) = schoolSmsEnabled(schoolId) && individualSmsEnabled(schoolId, message.phone)


  def sendSmsInstead(schoolId: Long, message: CheckNotification) = {
    val provider: SMSProvider = new Mb365SMS {
      override def url() = "http://mb345.com:999/ws/LinkWS.asmx/BatchSend"

      override def username() = SchoolConfig.valueOfKey(schoolId, "smsPushAccount") getOrElse ""

      override def password() = SchoolConfig.valueOfKey(schoolId, "smsPushPassword") getOrElse ""


      override def template(): String = s"${message.smsPushContent.getOrElse(message.aps.get.alert)}【${SchoolConfig.valueOfKey(schoolId, "smsPushSignature").getOrElse("幼乐宝")}】"
    }
    message.phone map {
      p =>
        val smsReq: String = provider.template()
        Logger.info(s"smsReq = $smsReq")
        SMSController.sendSMS(smsReq)(provider)
    }
  }

  def sendNewsSms(schoolId: Long, content: String, phones: List[String]) = {
    val provider: SMSProvider = new Mb365SMS {
      override def url() = "http://mb345.com:999/ws/LinkWS.asmx/BatchSend"

      override def username() = SchoolConfig.valueOfKey(schoolId, "smsPushAccount") getOrElse ""

      override def password() = SchoolConfig.valueOfKey(schoolId, "smsPushPassword") getOrElse ""

      override def template(): String = s"$content【${SchoolConfig.valueOfKey(schoolId, "smsPushSignature").getOrElse("幼乐宝")}】"

    }
    phones.sliding(100, 100).toList map {
      case every100 =>
        val smsReq: String = renderNews(provider.username(), provider.password(), every100, provider.template())
        Logger.info(s"sendNewsSms = $smsReq")
        SMSController.sendSMS(smsReq)(provider)
    }

  }

  def renderNews(userName: String, password: String, mobiles: List[String], content: String): String = {
    Map("CorpID" -> Seq(userName), "Pwd" -> Seq(password), "Mobile" -> mobiles,
      "Content" -> Seq(content), "Cell" -> Seq(""), "SendTime" -> Seq("")).toList.map(p => s"${p._1}=${urlEncode(p._2.mkString(","))}").mkString("&")
  }
}


