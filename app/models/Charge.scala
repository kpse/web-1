package models

import play.Logger
import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._
import anorm.~
import java.util.Date
import models.helper.TimeHelper.any2DateTime
import org.joda.time.DateTime
import play.api.libs.json.Json

case class ChargeInfo(school_id: Long, total_phone_number: Long, expire_date: String, status: Int, used: Long, total_video_account: Option[Long]=Some(0), video_user_name: Option[String] = None, video_user_password: Option[String] = None)
case class ActiveCount(school_id: Long, activated: Long, all: Long, member: Long, video: Long, check_in_out: Long, children: Long)

object Charge {
  implicit val writeActiveCount = Json.writes[ActiveCount]

  def countActivePhones(kg: Long) = DB.withConnection {
    implicit c =>
      val countActive: Long = SQL("select count(1) from accountinfo where active=1 and accountid in (select phone from parentinfo where school_id={kg})")
        .on('kg -> kg.toString).as(get[Long]("count(1)") single)
      val countAll: Long = SQL("select count(1) from accountinfo where accountid in (select phone from parentinfo where school_id={kg})")
        .on('kg -> kg.toString).as(get[Long]("count(1)") single)

      val countMember: Long = SQL("select count(1) from accountinfo where accountid in " +
        "(select phone from parentinfo where school_id={kg} and member_status=1)")
        .on('kg -> kg.toString).as(get[Long]("count(1)") single)
      val countVideoMember: Long = SQL("select count(1) from accountinfo where accountid in " +
        "(select phone from parentinfo p, videomembers v where p.school_id = v.school_id and " +
        "p.school_id={kg} and p.parent_id = v.parent_id)")
        .on('kg -> kg.toString).as(get[Long]("count(1)") single)

      val countActiveCheckIn = SQL("select count(1) from (SELECT p.parent_id as parent, count(1) as times " +
        "FROM DAILYLOG d, parentinfo p, relationmap r " +
        "where d.card_no = r.card_num and p.parent_id = r.parent_id " +
        "and p.school_id = {kg}  and check_at > {oneMonthAgo} and check_at < {now} " +
        "group by p.parent_id having times > 10) people_checkin")
        .on('oneMonthAgo -> DateTime.now().minusDays(30).getMillis,
          'now -> System.currentTimeMillis(),
        'kg -> kg.toString).as(get[Long]("count(1)") single)
      val countChildren: Long = SQL("select count(1) from childinfo where school_id={kg} and status=1")
        .on('kg -> kg.toString).as(get[Long]("count(1)") single)
      ActiveCount(kg, countActive, countAll, countMember, countVideoMember, countActiveCheckIn, countChildren)
  }


  def limitExceed(kg: Long): Boolean = DB.withConnection {
    implicit c =>
      SQL("select ( total_phone_number - (select count(distinct p.phone) from parentinfo p, relationmap r " +
        "where p.parent_id=r.parent_id and r.status=1 and member_status=1 and p.status=1 and school_id={kg})) as exceed " +
        "from chargeinfo where school_id={kg} and status=1")
        .on(
          'kg -> kg.toString
        ).as(get[Long]("exceed") single) <= 0
  }

  def delete(kg: Long) = DB.withConnection {
    implicit c =>

      SQL("update chargeinfo set status=0, expire_date={expire}" +
        " where school_id={kg}")
        .on(
          'kg -> kg.toString,
          'expire -> new DateTime().toString("yyyy-MM-dd")
        ).executeUpdate
  }

  def update(kg: Long, charge: ChargeInfo) = DB.withTransaction {
    implicit c =>
      try {
        SQL("update chargeinfo set total_phone_number={count}, total_video_account={accounts}, expire_date={expire}, status={status}" +
          " where school_id={kg}")
          .on(
            'kg -> kg.toString,
            'count -> charge.total_phone_number,
            'accounts -> charge.total_video_account,
            'expire -> charge.expire_date,
            'status -> charge.status
          ).executeUpdate
        charge.video_user_name map { value => addConfig(kg, ConfigItem("video_user_name", value))}
        charge.video_user_password map { value => addConfig(kg, ConfigItem("video_user_password", value))}
        c.commit()
        0
      }
      catch {
        case e: Throwable => c.rollback()
          Logger.info(e.getLocalizedMessage)
          1
      }
  }

  def create(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("insert into chargeinfo (school_id, expire_date) " +
        "values ({kg}, {expire})")
        .on(
          'kg -> kg.toString,
          'expire -> new DateTime().toString("yyyy-MM-dd")
        ).executeInsert()
  }

  def createIfNotExists(kg: Long) = DB.withConnection {
    implicit c =>
      val exists = SQL("select count(1) from chargeinfo where school_id={kg}")
        .on(
          'kg -> kg.toString
        ).as(get[Long]("count(1)") single) > 0
      exists match {
        case true =>
          index(kg)
        case false =>
          create(kg)
          index(kg)
      }
  }

  def index(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select *, (select count(distinct p.phone) from parentinfo p, relationmap r " +
        "where p.parent_id=r.parent_id and r.status=1 and member_status=1 and p.status=1 and school_id={kg}) as used, " +
        " (select value from schoolconfig where school_id={kg} and name='video_user_name' ) as video_user_name, " +
        " (select value from schoolconfig where school_id={kg} and name='video_user_password' ) as video_user_password " +
        " from chargeinfo where school_id={kg}")
        .on(
          'kg -> kg.toString
        ).as(simple *)
  }

  val simple = {
    get[String]("school_id") ~
      get[Long]("total_phone_number") ~
      get[Long]("total_video_account") ~
      get[Date]("expire_date") ~
      get[Int]("status") ~
      get[Long]("used") ~
      get[Option[String]]("video_user_name") ~
      get[Option[String]]("video_user_password") map {
      case kg ~ count ~ videoCount ~ expire ~ status ~ used ~ videoAccount ~ videoPassword =>
        ChargeInfo(kg.toLong, count, expire.toDateOnly, status, used, Some(videoCount), videoAccount, videoPassword)
    }
  }

  def addConfig(kg: Long, config: ConfigItem) = DB.withConnection {
    implicit c =>
      config.isExist(kg) match {
        case true =>
          config.update(kg)
        case false =>
          config.create(kg)
      }

  }
}
