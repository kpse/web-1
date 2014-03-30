package models

import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._
import anorm.~
import java.util.Date
import models.helper.TimeHelper.any2DateTime
import org.joda.time.DateTime

case class ChargeInfo(school_id: Long, total_phone_number: Long, expire_date: String, status: Int, used: Long)

object Charge {

  def limitExceed(kg: Long): Boolean = DB.withConnection {
    implicit c =>
      SQL("select ((select count(distinct p.phone) from parentinfo p, relationmap r " +
        "where p.parent_id=r.parent_id and r.status=1 and member_status=1 and p.status=1 and school_id={kg}) >= total_phone_number) as exceed " +
        "from chargeinfo where school_id={kg}")
        .on(
          'kg -> kg.toString
        ).as(get[Boolean]("exceed") single)
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

  def update(kg: Long, charge: ChargeInfo) = DB.withConnection {
    implicit c =>
      SQL("update chargeinfo set total_phone_number={count}, expire_date={expire}, status={status}" +
        " where school_id={kg}")
        .on(
          'kg -> kg.toString,
          'count -> charge.total_phone_number,
          'expire -> charge.expire_date,
          'status -> charge.status
        ).executeUpdate
  }

  def create(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("insert into chargeinfo (school_id, total_phone_number, expire_date) " +
        "values ({kg}, 0, {expire})")
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
        "where p.parent_id=r.parent_id and r.status=1 and member_status=1 and p.status=1 and school_id={kg}) as used from chargeinfo where school_id={kg}")
        .on(
          'kg -> kg.toString
        ).as(simple *)
  }

  val simple = {
    get[String]("school_id") ~
      get[Long]("total_phone_number") ~
      get[Date]("expire_date") ~
      get[Int]("status") ~
      get[Long]("used") map {
      case kg ~ count ~ expire ~ status ~ used =>
        ChargeInfo(kg.toLong, count, expire.toDateOnly, status, used)
    }
  }
}
