package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class EmployeeCard(id: Option[Long], school_id: Long, employee_id: Long, card: String, updated_at: Option[Long]) {

  def reuseDeleted(kg: Long) = DB.withConnection {
    implicit c =>
      SQL("update employeecard set status=1, employee_id={employee}, updated_at={time} where school_id={school_id} and card={card} and status=0")
        .on(
          'id -> id,
          'school_id -> school_id,
          'employee -> employee_id,
          'card -> card,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      EmployeeCard.searchByCard(kg, card)
  }

  def update(kg: Long): Option[EmployeeCard] = DB.withConnection {
    implicit c =>
      SQL("update employeecard set card={card}, employee_id={employee}, updated_at={time} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> school_id,
          'employee -> employee_id,
          'card -> card,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (EmployeeCard.show(kg, _))
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
      SQL("select count(1) from employeecard where status=1 and card={card}")
        .on(
          'card -> card
        ).as(get[Long]("count(1)") single) > 0
  }

  def cardDeleted = DB.withConnection {
    implicit c =>
      SQL("select count(1) from employeecard where status=0 and card={card}")
        .on(
          'card -> card
        ).as(get[Long]("count(1)") single) > 0
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
          'kg -> kg,
          'card -> card
        ).as(simple singleOpt)
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
