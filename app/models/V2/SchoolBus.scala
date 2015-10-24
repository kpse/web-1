package models.V2

import anorm.SqlParser._
import anorm._
import models.Employee
import play.api.Logger
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class SchoolBus(id: Option[Long], name: String, driver: Option[Employee], school_id: Long, morning_path: String, evening_path: String, morning_start: String, morning_end: String, evening_start: String, evening_end: String, updated_at: Option[Long], status: Option[Int] = Some(1)) {

  def nameDuplicated = DB.withConnection {
    implicit c =>
      id match {
        case Some(existingId) =>
          SQL("select count(1) from schoolbus where school_id={kg} and name={name} and uid<>{id} and status=1")
            .on(
              'kg -> school_id.toString,
              'name -> name,
              'id -> existingId
            ).as(get[Long]("count(1)") single) > 0
        case None =>
          SQL("select count(1) from schoolbus where school_id={kg} and name={name} and status=1")
            .on(
              'kg -> school_id.toString,
              'name -> name
            ).as(get[Long]("count(1)") single) > 0
      }
  }

  def revive: Option[SchoolBus] = DB.withConnection {
    implicit c =>
      val currentTime = System.currentTimeMillis
      val updated: Int = SQL("update schoolbus set name={name}, updated_at={time}, status=1, " +
        "morning_path={morningPath}, evening_path={eveningPath}, morning_start={morningStart}, morning_end={morningEnd}, " +
        "evening_start={eveningStart}, evening_end={eveningEnd} where school_id={kg} and employee_id={driver}")
        .on(
          'kg -> school_id,
          'name -> name,
          'driver -> pickDriverId(driver),
          'morningPath -> morning_path,
          'eveningPath -> evening_path,
          'morningStart -> morning_start,
          'morningEnd -> morning_end,
          'eveningStart -> evening_start,
          'eveningEnd -> evening_end,
          'time -> currentTime
        ).executeUpdate()
      updated match {
        case i if i > 0 => SchoolBus.findByDriver(school_id, pickDriverId(driver))
        case _ => None
      }

  }

  def create: Option[SchoolBus] = DB.withConnection {
    implicit c =>
      val currentTime = System.currentTimeMillis
      val id: Option[Long] = SQL("insert into schoolbus (school_id, employee_id, name, morning_path, evening_path, morning_start, morning_end, evening_start, evening_end, updated_at) values " +
        "({kg}, {driver}, {name}, {morningPath}, {eveningPath}, {morningStart}, {morningEnd}, {eveningStart}, {eveningEnd}, {time})")
        .on(
          'kg -> school_id,
          'name -> name,
          'driver -> pickDriverId(driver),
          'morningPath -> morning_path,
          'eveningPath -> evening_path,
          'morningStart -> morning_start,
          'morningEnd -> morning_end,
          'eveningStart -> evening_start,
          'eveningEnd -> evening_end,
          'time -> currentTime
        ).executeInsert()
      id match {
        case Some(i) => Some(copy(id = Some(i), updated_at = Some(currentTime), status = Some(1)))
        case None => None
      }
  }

  def update: Option[SchoolBus] = DB.withConnection {
    implicit c =>
      id.map {
        (index) =>
          val currentTime = System.currentTimeMillis
          val updated: Int = SQL("update schoolbus set employee_id={driver}, updated_at={time}, status=1, name={name}, " +
            "morning_path={morningPath}, evening_path={eveningPath}, morning_start={morningStart}, morning_end={morningEnd}, " +
            "evening_start={eveningStart}, evening_end={eveningEnd} where school_id={kg} and uid={id}")
            .on(
              'id -> index,
              'kg -> school_id,
              'name -> name,
              'driver -> pickDriverId(driver),
              'morningPath -> morning_path,
              'eveningPath -> evening_path,
              'morningStart -> morning_start,
              'morningEnd -> morning_end,
              'eveningStart -> evening_start,
              'eveningEnd -> evening_end,
              'time -> currentTime
            ).executeUpdate()
          updated match {
            case i if i > 0 => SchoolBus.show(school_id, index)
            case _ => None
          }
      }.getOrElse(None)
  }

  private def pickDriverId(driver: Option[Employee]): String = driver match {
    case Some(d) => d.id.getOrElse("")
    case None => ""
  }

  def exists = DB.withConnection {
    implicit c =>
      SQL("select count(1) from schoolbus where school_id={kg} and uid={id}")
        .on(
          'kg -> school_id.toString,
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def deleted = DB.withConnection {
    implicit c =>
      driver match {
        case Some(empl) =>
          SQL("select count(1) from schoolbus where school_id={kg} and employee_id={driver} and status=0")
            .on(
              'kg -> school_id.toString,
              'driver -> empl.id
            ).as(get[Long]("count(1)") single) > 0
        case None => false
      }

  }
}

object SchoolBus {
  implicit val writeSchoolBus = Json.writes[SchoolBus]
  implicit val readSchoolBus = Json.reads[SchoolBus]
  private val logger: Logger = Logger(classOf[SchoolBus])

  def driverIsExisting(driverId: String): Boolean = DB.withConnection {
    implicit c =>
      SQL("select count(1) from schoolbus where employee_id={driver} and status=1")
        .on('driver -> driverId)
        .as(get[Long]("count(1)") single) > 0
  }


  def driverDoesNotMatch(driverId: String, busId: Long): Boolean = DB.withConnection {
    implicit c =>
      SQL("select count(1) from schoolbus where employee_id={driver} and uid={bus} and status=1")
        .on('driver -> driverId,
          'bus -> busId)
        .as(get[Long]("count(1)") single) == 0
  }

  def delete(kg: Long, id: Long) = DB.withTransaction {
    implicit c =>
      try {
        SQL("update schoolbus set status=0 where school_id={kg} and uid={id} and status=1")
          .on('kg -> kg.toString,
            'id -> id)
          .executeUpdate()
        val updated: Int = SQL("update childrenbusplan set status=0 where school_id={kg} and employee_id=(select employee_id from schoolbus where school_id={kg} and uid={id} ) and status=1")
          .on('kg -> kg.toString,
            'id -> id)
          .executeUpdate()
        c.commit()
        updated
      }
      catch {
        case t: Throwable =>
          logger.warn(t.getLocalizedMessage)
          logger.warn(t.getMessage)
          c.rollback()
          0
      }
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from schoolbus where school_id={kg} and uid={id}")
        .on('kg -> kg.toString,
          'id -> id)
        .as(simple singleOpt)
  }

  def findByDriver(kg: Long, driverId: String) = DB.withConnection {
    implicit c =>
      SQL("select * from schoolbus where school_id={kg} and employee_id={id}")
        .on('kg -> kg.toString,
          'id -> driverId)
        .as(simple singleOpt)
  }

  def index(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("select * from schoolbus where school_id={kg} and status=1")
        .on('kg -> kg.toString)
        .as(simple *)
  }

  val simple = {
    get[Long]("uid") ~
      get[String]("name") ~
      get[String]("school_id") ~
      get[String]("employee_id") ~
      get[String]("morning_path") ~
      get[String]("evening_path") ~
      get[String]("morning_start") ~
      get[String]("morning_end") ~
      get[String]("evening_start") ~
      get[String]("evening_end") ~
      get[Long]("updated_at") ~
      get[Int]("status") map {
      case id ~ name ~ school ~ driver ~ path ~ eveningPath ~ morningStart ~ morningEnd ~ eveningStart ~ eveningEnd ~ time ~ status =>
        SchoolBus(Some(id), name, Employee.findById(school.toLong, driver), school.toLong, path, eveningPath, morningStart, morningEnd, eveningStart, eveningEnd, Some(time), Some(status))
    }
  }

}
