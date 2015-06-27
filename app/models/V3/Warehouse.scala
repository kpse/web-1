package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class WarehouseKeeper(id: Option[Long], employee_id: Option[Long]) {
  def exists(id: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from warehousekeeper where uid={id}")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(id: Long, base: Long) = exists(id) match {
    case true =>
      update(id, base)
    case false =>
      create(id, base)
  }


  def update(kg: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL("update warehousekeeper set employee_id={employee_id}, warehouse_id={warehouse_id}, updated_at={time} " +
        "where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'employee_id -> employee_id,
          'warehouse_id -> base,
          'time -> System.currentTimeMillis
        ).executeUpdate()
  }

  def create(kg: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL("insert into warehousekeeper (school_id, employee_id, warehouse_id, updated_at) values (" +
        "{school_id}, {employee_id}, {warehouse_id}, {time})")
        .on(
          'school_id -> kg,
          'employee_id -> employee_id,
          'warehouse_id -> base,
          'time -> System.currentTimeMillis
        ).executeInsert()
  }
}

case class Warehouse(id: Option[Long], employees: Option[List[WarehouseKeeper]], name: Option[String], memo: Option[String]) {
  def exists(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from warehouse where uid={id}")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(id: Long) = exists(id) match {
    case true =>
      update(id)
    case false =>
      create(id)
  }


  def update(kg: Long): Option[Warehouse] = DB.withTransaction {
    implicit c =>
      try {
        SQL("update warehouse set name={name}, memo={memo}, updated_at={time} where school_id={school_id} and uid={id}")
          .on(
            'id -> id,
            'school_id -> kg,
            'name -> name,
            'memo -> memo,
            'time -> System.currentTimeMillis
          ).executeUpdate()
        id foreach {
          case i =>
            Warehouse.clean(kg, i)
            employees foreach {
              _ foreach {
                _.handle(kg, i)
              }
            }
        }
        c.commit()
        Warehouse.show(kg, id.getOrElse(0))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }

  def create(kg: Long): Option[Warehouse] = DB.withTransaction {
    implicit c =>
      try {
        val insert: Option[Long] = SQL("insert into warehouse (school_id, name, memo, updated_at) values (" +
          "{school_id}, {name}, {memo}, {time})")
          .on(
            'school_id -> kg,
            'name -> name,
            'memo -> memo,
            'time -> System.currentTimeMillis
          ).executeInsert()
        insert foreach {
          case i =>
            Warehouse.clean(kg, i)
            employees foreach {
              _ foreach {
                _.handle(kg, i)
              }
            }
        }
        c.commit()
        Warehouse.show(kg, insert.getOrElse(0))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }
}

object Warehouse {

  implicit val writeWarehouseKeeper = Json.writes[WarehouseKeeper]
  implicit val writeWarehouse = Json.writes[Warehouse]
  implicit val readWarehouseKeeper = Json.reads[WarehouseKeeper]
  implicit val readWarehouse = Json.reads[Warehouse]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from warehouse where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to,
          'most -> most
        ).as(simple *)
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from warehouse where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def keepers(kg: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from warehousekeeper where school_id={kg} and status=1 and warehouse_id={base}")
        .on(
          'kg -> kg.toString,
          'base -> base
        ).as(simpleKeeper *)
  }

  def clean(kg: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update warehousekeeper set status=0 where school_id={kg} and status=1 and warehouse_id={base}")
        .on(
          'kg -> kg.toString,
          'base -> base
        ).execute()
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update warehouse set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
      clean(kg, id)
  }

  val simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[Option[String]]("name") ~
      get[Option[String]]("memo") map {
      case id ~ kg ~ name ~ memo =>
        Warehouse(Some(id), Some(keepers(kg.toLong, id)), name, memo)
    }
  }

  val simpleKeeper = {
    get[Long]("uid") ~
      get[Option[Long]]("employee_id") map {
      case id ~ project =>
        WarehouseKeeper(Some(id), project)
    }
  }
}
