package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class GoodsOrigin(id: Option[Long], name: Option[String], short_name: Option[String], warehouse_id: Option[Long], memo: Option[String]) {
  def exists(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from goodsorigin where uid={id}")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(id: Long, warehouseId: Long) = exists(id) match {
    case true =>
      update(id, warehouseId)
    case false =>
      create(id, warehouseId)
  }


  def update(kg: Long, warehouseId: Long): Option[GoodsOrigin] = DB.withConnection {
    implicit c =>
      SQL("update goodsorigin set school_id={school_id}, warehouse_id={warehouse_id}, name={name}, " +
        "short_name={short_name}, memo={memo}, updated_at={time} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'warehouse_id -> warehouse_id,
          'name -> name,
          'short_name -> short_name,
          'memo -> memo,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      GoodsOrigin.show(kg, warehouseId, id.getOrElse(0))
  }

  def create(kg: Long, warehouseId: Long): Option[GoodsOrigin] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into goodsorigin (school_id, warehouse_id, name, short_name, memo, updated_at) values (" +
        "{school_id}, {warehouse_id}, {name}, {short_name}, {memo}, {time})")
        .on(
          'school_id -> kg,
          'warehouse_id -> warehouse_id,
          'name -> name,
          'short_name -> short_name,
          'memo -> memo,
          'time -> System.currentTimeMillis
        ).executeInsert()
      GoodsOrigin.show(kg, warehouseId, insert.getOrElse(0))
  }
}

object GoodsOrigin {
  implicit val writeWarehouse = Json.writes[GoodsOrigin]
  implicit val readWarehouse = Json.reads[GoodsOrigin]

  def index(kg: Long, warehouseId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from goodsorigin where school_id={kg} and warehouse_id={warehouse} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'warehouse -> warehouseId,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def show(kg: Long, warehouseId: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from goodsorigin where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, warehouseId: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update goodsorigin set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  def simple = {
    get[Long]("uid") ~
      get[Option[Long]]("warehouse_id") ~
      get[Option[String]]("name") ~
      get[Option[String]]("short_name") ~
      get[Option[String]]("memo") map {
      case id ~ wId ~ name ~ short_name ~ memo =>
        GoodsOrigin(Some(id), name, short_name, wId, memo)
    }
  }
}
