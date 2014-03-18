package models

import play.api.db.DB
import anorm._
import play.api.Play.current
import anorm.SqlParser._
import anorm.~
import java.util.Date
import models.helper.TimeHelper.any2DateTime

case class ChargeInfo(school_id: Long, total_phone_number: Long, expire_date: String)

object Charge {
  def update(kg: Long, charge: ChargeInfo) = DB.withConnection {
    implicit c =>
      SQL("update chargeinfo set total_phone_number={count}, expire_date={expire}" +
        " where school_id={kg}")
        .on(
          'kg -> kg.toString,
          'count -> charge.total_phone_number,
          'expire -> charge.expire_date
        ).executeUpdate
  }

  def index(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from chargeinfo where school_id={kg} and status=1")
        .on(
          'kg -> kg.toString
        ).as(simple *)
  }

  val simple = {
    get[String]("school_id") ~
      get[Long]("total_phone_number") ~
      get[Date]("expire_date") map {
      case kg ~ count ~ expire =>
        ChargeInfo(kg.toLong, count, expire.toDateOnly)
    }
  }
}
