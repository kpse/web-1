package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class Goods(id: Option[Long], name: Option[String], short_name: Option[String], unit: Option[String], max_warning: Option[String],
                 min_warning: Option[String], warehouse_id: Option[Long], stock_place: Option[String], memo: Option[String]) {
  def exists(id: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from goods where uid={id}")
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


  def update(kg: Long, warehouseId: Long): Option[Goods] = DB.withConnection {
    implicit c =>
      SQL("update goods set school_id={school_id}, name={name}, short_name={short_name}, unit={unit}, max_warning={max_warning}," +
        "min_warning={min_warning}, warehouse_id={warehouse_id}, stock_place={stock_place}, memo={memo}, updated_at={time} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'name -> name,
          'short_name -> short_name,
          'unit -> unit,
          'max_warning -> max_warning,
          'min_warning -> min_warning,
          'warehouse_id -> warehouseId,
          'stock_place -> stock_place,
          'memo -> memo,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (Goods.show(kg, warehouseId, _))
  }

  def create(kg: Long, warehouseId: Long): Option[Goods] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into goods (school_id, warehouse_id, name, short_name, unit, max_warning, min_warning, stock_place, memo, updated_at) values (" +
        "{school_id}, {warehouse_id}, {name}, {short_name}, {unit}, {max_warning}, {min_warning}, {stock_place}, {memo}, {time})")
        .on(
          'school_id -> kg,
          'name -> name,
          'short_name -> short_name,
          'unit -> unit,
          'max_warning -> max_warning,
          'min_warning -> min_warning,
          'warehouse_id -> warehouseId,
          'stock_place -> stock_place,
          'memo -> memo,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (Goods.show(kg, warehouseId, _))
  }
}
case class Inventory(id: Option[Long], goods_id: Option[Long], goods_name: Option[String], warehouse_id: Option[Long], created_at: Option[Long], updated_at: Option[Long], quality: Option[Int], unit: Option[String]) {
  def update(kg: Long, warehouseId: Long): Option[Inventory] = DB.withConnection {
    implicit c =>
      SQL("update warehouseinventory set school_id={school_id}, goods_id={goods_id}, goods_name={goods_name}, " +
        "warehouse_id={warehouse_id}, quality={quality}, unit={unit}, updated_at={update} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'goods_id -> goods_id,
          'goods_name -> goods_name,
          'warehouse_id -> warehouseId,
          'quality -> quality,
          'unit -> unit,
          'update -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (Inventory.show(kg, warehouseId, _))
  }

  def create(kg: Long, warehouseId: Long): Option[Inventory] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into warehouseinventory (school_id, warehouse_id, goods_id, goods_name, unit, quality, updated_at, created_at) values (" +
        "{school_id}, {warehouse_id}, {goods_id}, {goods_name}, {unit}, {quality}, {update}, {create})")
        .on(
          'school_id -> kg,
          'goods_id -> goods_id,
          'goods_name -> goods_name,
          'warehouse_id -> warehouseId,
          'quality -> quality,
          'unit -> unit,
          'update -> System.currentTimeMillis,
          'create -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (Inventory.show(kg, warehouseId, _))
  }
}

case class Stocking(id: Option[Long], `type`: Option[Int], invoice_type: Option[Int], invoice_name: Option[String], serial_number: Option[String], sn_base: Option[Long],
                    creator: Option[String], updated_at: Option[Long], employee_id: Option[Long], warehouse_id: Option[Long], memo: Option[String], origin_id: Option[Long], items: Option[List[StockingDetail]]) {
  def update(kg: Long, warehouseId: Long): Option[Stocking] = DB.withTransaction {
    implicit c =>
      try {
        SQL("update warehousestocking set school_id={school_id}, type={type}, invoice_type={invoice_type}, invoice_name={invoice_name}," +
          "serial_number={serial_number}, sn_base={sn_base}, creator={creator}, employee_id={employee_id}, warehouse_id={warehouse_id}, " +
          "memo={memo}, origin_id={origin_id}, updated_at={time} where school_id={school_id} and uid={id}")
          .on(
            'id -> id,
            'school_id -> kg,
            'type -> `type`,
            'invoice_type -> invoice_type,
            'invoice_name -> invoice_name,
            'serial_number -> serial_number,
            'sn_base -> sn_base,
            'creator -> creator,
            'employee_id -> employee_id,
            'warehouse_id -> warehouseId,
            'memo -> memo,
            'origin_id -> origin_id,
            'time -> System.currentTimeMillis
          ).executeUpdate()
        id foreach {
          case i =>
            Stocking.cleanDetail(kg, i)
            items foreach {
              _ foreach {
                _.handle(kg, warehouseId, i)
              }
            }
        }
        c.commit()
        id flatMap (Stocking.show(kg, warehouseId, _))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }

  def create(kg: Long, warehouseId: Long): Option[Stocking] = DB.withTransaction {
    implicit c =>
      try {
        val insert: Option[Long] = SQL("insert into warehousestocking (school_id, warehouse_id, type, invoice_type, " +
          "invoice_name, serial_number, sn_base, creator, employee_id, origin_id, memo, updated_at) values (" +
          "{school_id}, {warehouse_id}, {type}, {invoice_type}, {invoice_name}, {serial_number}, {sn_base}, {creator}, {employee_id}, {origin_id}, {memo}, {time})")
          .on(
            'school_id -> kg,
            'type -> `type`,
            'invoice_type -> invoice_type,
            'invoice_name -> invoice_name,
            'serial_number -> serial_number,
            'sn_base -> sn_base,
            'creator -> creator,
            'employee_id -> employee_id,
            'warehouse_id -> warehouseId,
            'memo -> memo,
            'origin_id -> origin_id,
            'time -> System.currentTimeMillis
          ).executeInsert()
        insert foreach {
          case i =>
            Stocking.cleanDetail(kg, i)
            items foreach {
              _ foreach {
                _.handle(kg, warehouseId, i)
              }
            }
        }
        c.commit()
        insert flatMap (Stocking.show(kg, warehouseId, _))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }
}

case class StockingDetail(id: Option[Long], stocking_id: Option[Long], goods_id: Option[Long], goods_name: Option[String],
                          origin_id: Option[Long], origin_name: Option[String], price: Option[String], quality: Option[Int], unit: Option[String], memo: Option[String], subtotal: Option[String]) {
  def exists(id: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from warehousestockingdetail where uid={id}")
        .on(
          'id -> id
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(id: Long, warehouseId: Long, base: Long) = exists(id) match {
    case true =>
      update(id, warehouseId, base)
    case false =>
      create(id, warehouseId, base)
  }


  def update(kg: Long, warehouseId: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL("update warehousestockingdetail set school_id={school_id}, warehouse_id={warehouse_id}, stocking_id={stocking_id}, goods_id={goods_id}, " +
        "goods_name={goods_name}, origin_id={origin_id}, origin_name={origin_name}, price={price}, quality={quality}, " +
        "unit={unit}, memo={memo}, subtotal={subtotal}, updated_at={time} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'stocking_id -> base,
          'warehouse_id -> warehouseId,
          'goods_id -> goods_id,
          'goods_name -> goods_name,
          'origin_id -> origin_id,
          'origin_name -> origin_name,
          'price -> price,
          'quality -> quality,
          'unit -> unit,
          'memo -> memo,
          'subtotal -> subtotal,
          'time -> System.currentTimeMillis
        ).executeUpdate()
  }

  def create(kg: Long, warehouseId: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL("insert into warehousestockingdetail (school_id, warehouse_id, stocking_id, origin_id, origin_name, goods_id, goods_name, price, unit, quality, subtotal, updated_at) values (" +
        "{school_id}, {warehouse_id}, {stocking_id}, {origin_id}, {origin_name}, {goods_id}, {goods_name}, {price}, {unit}, {quality}, {subtotal}, {time})")
        .on(
          'school_id -> kg,
          'stocking_id -> base,
          'warehouse_id -> warehouseId,
          'goods_id -> goods_id,
          'goods_name -> goods_name,
          'origin_id -> origin_id,
          'origin_name -> origin_name,
          'price -> price,
          'quality -> quality,
          'unit -> unit,
          'memo -> memo,
          'subtotal -> subtotal,
          'time -> System.currentTimeMillis
        ).executeInsert()
  }
}

object Goods {
  implicit val writeGoods = Json.writes[Goods]
  implicit val readGoods = Json.reads[Goods]

  def index(kg: Long, base: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from goods where school_id={kg} and warehouse_id={base} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'base -> base,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def show(kg: Long, base: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from goods where school_id={kg} and warehouse_id={base} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'base -> base,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, base: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update goods set status=0 where uid={id} and school_id={kg} and warehouse_id={base} and status=1")
        .on(
          'kg -> kg.toString,
          'base -> base,
          'id -> id
        ).executeUpdate()
  }

  def simple = {
    get[Long]("uid") ~
      get[Option[Long]]("warehouse_id") ~
      get[Option[String]]("name") ~
      get[Option[String]]("short_name") ~
      get[Option[String]]("unit") ~
      get[Option[String]]("max_warning") ~
      get[Option[String]]("min_warning") ~
      get[Option[String]]("stock_place") ~
      get[Option[String]]("memo") map {
      case id ~ warehouseId ~ name ~ short ~ unit ~ max ~ min ~ stock ~ memo =>
        Goods(Some(id), name, short, unit, max, min, warehouseId, stock, memo)
    }
  }

}

object Inventory {
  implicit val writeInventory = Json.writes[Inventory]
  implicit val readInventory = Json.reads[Inventory]

  def index(kg: Long, base: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from warehouseinventory where school_id={kg} and warehouse_id={base} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'base -> base,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def show(kg: Long, base: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from warehouseinventory where school_id={kg} and warehouse_id={base} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'base -> base,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, base: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update warehouseinventory set status=0 where uid={id} and school_id={kg} and warehouse_id={base} and status=1")
        .on(
          'kg -> kg.toString,
          'base -> base,
          'id -> id
        ).executeUpdate()
  }

  def simple = {
    get[Long]("uid") ~
      get[Option[Long]]("goods_id") ~
      get[Option[String]]("goods_name") ~
      get[Option[Long]]("warehouse_id") ~
      get[Option[Int]]("quality") ~
      get[Option[String]]("unit") ~
      get[Option[Long]]("created_at") ~
      get[Option[Long]]("updated_at") map {
      case id ~ goodsId ~ name ~ warehouse ~ quality ~ unit ~ create ~ update =>
        Inventory(Some(id), goodsId, name, warehouse, create, update, quality, unit)
    }
  }
}

object Stocking {
  implicit val writeStockingDetail = Json.writes[StockingDetail]
  implicit val readStockingDetail = Json.reads[StockingDetail]
  implicit val writeStocking = Json.writes[Stocking]
  implicit val readStocking = Json.reads[Stocking]

  def index(kg: Long, warehouseId: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from warehousestocking where school_id={kg} and warehouse_id={base} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'base -> warehouseId,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def detailIndex(kg: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from warehousestockingdetail where school_id={kg} and status=1 and stocking_id={base}")
        .on(
          'kg -> kg.toString,
          'base -> base
        ).as(simpleDetail *)
  }

  def cleanDetail(kg: Long, base: Long) = DB.withTransaction {
    implicit c =>
      SQL(s"update warehousestockingdetail set status=0 where school_id={kg} and status=1 and stocking_id={base}")
        .on(
          'kg -> kg.toString,
          'base -> base
        ).executeUpdate()
  }

  def show(kg: Long, warehouseId: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from warehousestocking where school_id={kg} and warehouse_id={base} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'base -> warehouseId,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, warehouseId: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update warehousestocking set status=0 where uid={id} and warehouse_id={base} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'base -> warehouseId,
          'id -> id
        ).executeUpdate()
      cleanDetail(kg, id)
  }

  def simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[Option[Int]]("type") ~
      get[Option[Int]]("invoice_type") ~
      get[Option[String]]("invoice_name") ~
      get[Option[String]]("serial_number") ~
      get[Option[Long]]("sn_base") ~
      get[Option[String]]("creator") ~
      get[Option[Long]]("employee_id") ~
      get[Option[Long]]("warehouse_id") ~
      get[Option[Long]]("origin_id") ~
      get[Option[String]]("memo") ~
      get[Option[Long]]("updated_at") map {
      case id ~ kg ~ typ ~ iType ~ name ~ number ~ sn ~ creator ~ employee ~ warehouse ~ origin ~ memo ~ time =>
        Stocking(Some(id), typ, iType, name, number, sn, creator, time, employee, warehouse, memo, origin, Some(detailIndex(kg.toLong, id)))
    }
  }

  def simpleDetail = {
    get[Long]("uid") ~
      get[Option[Long]]("stocking_id") ~
      get[Option[Long]]("goods_id") ~
      get[Option[String]]("goods_name") ~
      get[Option[Long]]("origin_id") ~
      get[Option[String]]("origin_name") ~
      get[Option[String]]("price") ~
      get[Option[Int]]("quality") ~
      get[Option[String]]("unit") ~
      get[Option[String]]("memo") ~
      get[Option[String]]("subtotal") map {
      case id ~ stocking ~ goodsId ~ name ~ origin ~ originName ~ price ~ quality ~ unit ~ memo ~ subtotal =>
        StockingDetail(Some(id), stocking, goodsId, name, origin, originName, price, quality, unit, memo, subtotal)
    }
  }
}
