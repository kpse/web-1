package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class EmployeeCard(id: Option[Long], school_id: Long, employee_id: Long, card: String, updated_at: Option[Long]) {

  def reuseDeleted(kg: Long) = DB.withTransaction {
    implicit c =>
      try {
        clearDeletedEmployee()(c)
        SQL("update employeecard set status=1, employee_id={employee}, updated_at={time} where school_id={school_id} and card={card} and status=0")
          .on(
            'id -> id,
            'school_id -> kg,
            'employee -> employee_id,
            'card -> card,
            'time -> System.currentTimeMillis
          ).executeUpdate()
        c.commit()
        EmployeeCard.searchByCard(kg, card)
      } catch {
        case t: Throwable =>
          Logger.info(s"error in reusing deleted card ${t.getLocalizedMessage}")
          c.rollback()
          None
      }
  }

  def changeOwner(kg: Long) = DB.withTransaction {
    implicit c =>
      try {
        clearDeletedCard()(c)
        SQL("update employeecard set status=1, updated_at={time}, card={card} where school_id={school_id} and employee_id={employee} and status=0")
          .on(
            'id -> id,
            'school_id -> kg,
            'employee -> employee_id,
            'card -> card,
            'time -> System.currentTimeMillis
          ).executeUpdate()
        c.commit()
        EmployeeCard.searchByCard(kg, card)
      } catch {
        case t: Throwable =>
          Logger.info(s"error in changing card owner ${t.getLocalizedMessage}")
          c.rollback()
          None
      }
  }

  def update(kg: Long): Option[EmployeeCard] = DB.withTransaction {
    implicit c =>
      try {
        clearDeletedEmployee()(c)
        clearDeletedCard()(c)
        SQL("update employeecard set card={card}, employee_id={employee}, updated_at={time} where school_id={school_id} and uid={id}")
          .on(
            'id -> id,
            'school_id -> school_id,
            'employee -> employee_id,
            'card -> card,
            'time -> System.currentTimeMillis
          ).executeUpdate()
        c.commit()
        id flatMap (EmployeeCard.show(kg, _))
      } catch {
        case t: Throwable =>
          Logger.info(s"error in updating employee card ${t.getLocalizedMessage}")
          c.rollback()
          None
      }
  }

  def create(kg: Long): Option[EmployeeCard] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into employeecard (school_id, employee_id, card, updated_at) values (" +
        "{school_id}, {employee}, {card}, {time})")
        .on(
          'school_id -> school_id,
          'employee -> employee_id,
          'card -> card,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (EmployeeCard.show(kg, _))
  }

  def cardExists = DB.withConnection {
    implicit c =>
      id match {
        case Some(i) =>
          SQL("select count(1) from employeecard where status=1 and card={card} and uid<>{id}")
            .on(
              'card -> card,
              'id -> i
            ).as(get[Long]("count(1)") single) > 0
        case None =>
          SQL("select count(1) from employeecard where status=1 and card={card}")
            .on(
              'card -> card
            ).as(get[Long]("count(1)") single) > 0
      }

  }

  def cardDeleted = DB.withConnection {
    implicit c =>
      SQL("select count(1) from employeecard where status=0 and card={card}")
        .on(
          'card -> card
        ).as(get[Long]("count(1)") single) > 0
  }

  def employeeDeleted = DB.withConnection {
    implicit c =>
      SQL("select count(1) from employeecard where status=0 and employee_id={employee}")
        .on(
          'employee -> employee_id
        ).as(get[Long]("count(1)") single) > 0
  }

  def employeeExists = DB.withConnection {
    implicit c =>
      id match {
        case Some(i) =>
          SQL("select count(1) from employeecard where employee_id={employee} and status=1 and uid<>{id}")
            .on(
              'employee -> employee_id,
              'id -> i
            ).as(get[Long]("count(1)") single) > 0
        case None =>
          SQL("select count(1) from employeecard where employee_id={employee} and status=1")
            .on(
              'employee -> employee_id
            ).as(get[Long]("count(1)") single) > 0
      }

  }

  def clearDeletedEmployee()(implicit connection: java.sql.Connection) = {
    id match {
      case Some(i) =>
        SQL(s"delete from employeecard where employee_id={employee_id} and status=0 and uid<>{id}")
          .on('employee_id -> employee_id, 'id -> i).execute()(connection)
      case None =>
        SQL(s"delete from employeecard where employee_id={employee_id} and status=0 and card<>{card}")
          .on('employee_id -> employee_id, 'card -> card).execute()(connection)
    }
  }

  def clearDeletedCard()(implicit connection: java.sql.Connection) = {
    id match {
      case Some(i) =>
        SQL(s"delete from employeecard where card={card} and status=0 and uid<>{id}")
          .on('card -> card, 'id -> i).execute()(connection)
      case None =>
        SQL(s"delete from employeecard where card={card} and status=0 and employee_id<>{employee_id}")
          .on('employee_id -> employee_id, 'card -> card).execute()(connection)
    }
  }
}

object EmployeeCard {
  implicit val readEmployeeCard = Json.reads[EmployeeCard]
  implicit val writeEmployeeCard = Json.writes[EmployeeCard]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from employeecard where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from employeecard where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update employeecard set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  def searchByCard(kg: Long, card: String) = DB.withConnection {
    implicit c =>
      SQL(s"select * from employeecard where school_id={kg} and card={card} and status=1")
        .on(
          'kg -> kg.toString,
          'card -> card
        ).as(simple singleOpt)
  }

  def generateIdCheck(id: Option[Long]) = id match {
    case Some(x) => " and uid <> {id} "
    case None => ""
  }

  def cardExists(card: String, id: Option[Long]) = DB.withConnection {
    implicit c =>
      SQL(s"select count(1) from employeecard where card={card} and status=1 ${generateIdCheck(id)}")
        .on(
          'card -> card,
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[Long]("employee_id") ~
      get[String]("card") ~
      get[Option[Long]]("updated_at") map {
      case id ~ kg ~ employee ~ card ~ time =>
        EmployeeCard(Some(id), kg.toLong, employee, card, time)
    }
  }
}
