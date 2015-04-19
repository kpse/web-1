package models.V2

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class ChildrenPlan(id: Option[Long], driver_id: String, child_id: String, school_id: Long, timestamp: Option[Long], status: Option[Int] = Some(1)) {
  def create: Option[ChildrenPlan] = DB.withConnection {
    implicit c =>
      val currentTime = System.currentTimeMillis
      val id: Option[Long] = SQL("insert into childrenbusplan (school_id, employee_id, child_id, updated_at) values " +
        "({kg}, {driver}, {child}, {time})")
        .on(
          'kg -> school_id,
          'child -> child_id,
          'driver -> driver_id,
          'time -> currentTime
        ).executeInsert()
      id match {
        case Some(i) => Some(copy(id = Some(i), timestamp = Some(currentTime)))
        case None => None
      }
  }

  def update: Option[ChildrenPlan] = DB.withConnection {
    implicit c =>
      val currentTime = System.currentTimeMillis
      val updated: Int = SQL("update childrenbusplan set employee_id={driver}, updated_at={time}, status=1 " +
        "where school_id={kg} and child_id={child}")
        .on(
          'kg -> school_id,
          'child -> child_id,
          'driver -> driver_id,
          'time -> currentTime
        ).executeUpdate()
      updated match {
        case i if i > 0 => ChildrenPlan.show(school_id, child_id)
        case _ => None
      }
  }

  def exists = DB.withConnection {
    implicit c =>
      SQL("select count(1) from childrenbusplan where school_id={kg} and child_id={child}")
        .on(
          'kg -> school_id.toString,
          'child -> child_id
        ).as(get[Long]("count(1)") single) > 0
  }
}

object ChildrenPlan {
  implicit val writeChildrenPlan = Json.writes[ChildrenPlan]
  implicit val readChildrenPlan = Json.reads[ChildrenPlan]

  def show(kg: Long, childId: String) = DB.withConnection {
    implicit c =>
      SQL("select * from childrenbusplan where school_id={kg} and child_id={child} and status=1 order by uid DESC limit 1")
        .on(
          'kg -> kg.toString,
          'child -> childId
        ).as(simple singleOpt)
  }

  def delete(kg: Long, childId: String) = DB.withConnection {
    implicit c =>
      val currentTime = System.currentTimeMillis
      SQL("update childrenbusplan set status=0, updated_at={time} " +
        "where school_id={kg} and child_id={child} and status=1")
        .on(
          'kg -> kg.toString,
          'time -> currentTime,
          'child -> childId
        ).execute()
  }

  def index(kg: Long, driverId: String) = DB.withConnection {
    implicit c =>
      SQL("select * from childrenbusplan where school_id={kg} and employee_id={driver} and status=1")
        .on(
          'kg -> kg.toString,
          'driver -> driverId
        ).as(simple *)
  }

  val simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[String]("employee_id") ~
      get[String]("child_id") ~
      get[Long]("updated_at") ~
      get[Int]("status") map {
      case id ~ kg ~ driver ~ child ~ time ~ status =>
        ChildrenPlan(Some(id), driver, child, kg.toLong, Some(time), Some(status))
    }
  }
}
