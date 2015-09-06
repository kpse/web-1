package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.Logger
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class DietNutritionPreview(id: Option[Long], nutrition_id: Long, weight: String) {
  def exists(id: Option[Long]) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from dietstructure where uid={id}")
        .on(
          'id -> id.getOrElse(0)
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(kg: Long, menuId: Long) = exists(id) match {
    case true =>
      update(kg, menuId)
    case false =>
      create(kg, menuId)
  }


  def update(kg: Long, menuId: Long) = DB.withConnection {
    implicit c =>
      SQL("update dietstructure set school_id={school_id}, nutrition_id={nutritionId}, weight={weight}, " +
        " status=1 where school_id={school_id} and uid={id} and menu_id={menuId}")
        .on(
          'id -> id,
          'school_id -> kg,
          'menuId -> menuId,
          'nutritionId -> nutrition_id,
          'weight -> weight
        ).executeUpdate()
  }

  def create(kg: Long, menuId: Long) = DB.withConnection {
    implicit c =>
      SQL("insert into dietstructure (school_id, menu_id, nutrition_id, weight, created_at) values (" +
        "{school_id}, {menuId}, {nutritionId}, {weight}, {time})")
        .on(
          'school_id -> kg,
          'menuId -> menuId,
          'nutritionId -> nutrition_id,
          'weight -> weight,
          'time -> System.currentTimeMillis
        ).executeInsert()
  }
}


case class Menu(id: Option[Long], name: Option[String], weight: Option[String], arrange_type: Option[Int],
                nutrition_units: List[DietNutritionPreview], recipe: String, property: String, tips: String, food_type_id: Option[Long],
                store_type: Int = 1, former_id: Option[Long] = None) {
  def deleted = DB.withConnection {
    implicit c =>
      SQL("select count(1) from dietmenu where uid={id} and status=0")
        .on(
          'id -> id.getOrElse(0)
        ).as(get[Long]("count(1)") single) > 0
  }


  def exists(id: Option[Long]) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from dietmenu where uid={id}")
        .on(
          'id -> id.getOrElse(0)
        ).as(get[Long]("count(1)") single) > 0
  }

  def handle(kg: Long) = exists(id) match {
    case true =>
      update(kg)
    case false =>
      create(kg)
  }


  def update(kg: Long): Option[Menu] = DB.withTransaction {
    implicit c =>
      try {
        SQL("update dietmenu set school_id={school_id}, name={name}, weight={weight}, " +
          "arrange_type={arrange_type}, store_type={store_type}, updated_at={time} where school_id={school_id} and uid={id}")
          .on(
            'id -> id,
            'school_id -> kg,
            'name -> name,
            'weight -> weight,
            'arrange_type -> arrange_type,
            'store_type -> store_type,
            'time -> System.currentTimeMillis
          ).executeUpdate()
        id foreach {
          case i =>
            Menu.cleanNutritionUnits(kg, i)
            nutrition_units foreach {
              _.handle(kg, i)
            }
        }
        c.commit()
        Logger.info(s"id = ${id}")
        id flatMap (Menu.show(kg, _))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }

  }

  def create(kg: Long): Option[Menu] = DB.withTransaction {
    implicit c =>
      try {
        val insert: Option[Long] = SQL("insert into dietmenu (school_id, name, weight, arrange_type, updated_at) values (" +
          "{school_id}, {name}, {weight}, {arrange_type}, {time})")
          .on(
            'school_id -> kg,
            'name -> name,
            'weight -> weight,
            'arrange_type -> arrange_type,
            'store_type -> store_type,
            'time -> System.currentTimeMillis
          ).executeInsert()
        insert foreach {
          case i =>
            Menu.cleanNutritionUnits(kg, i)
            nutrition_units foreach {
              _.handle(kg, i)
            }
        }
        c.commit()
        insert flatMap (Menu.show(kg, _))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }

  def validUnits(kg: Long) : Boolean = DB.withTransaction {
    implicit c =>
      val unitIds: List[Long] = nutrition_units.filter(_.id.nonEmpty).map(_.id.get)
      SQL(s"select count(1) from dietstructure where menu_id={menu} and school_id={kg} and uid in (${unitIds.mkString(",")})")
        .on(
          'menu -> id,
          'kg -> kg.toString
        ).as(get[Long]("count(1)") single) == unitIds.size
  }
}

object Menu {
  implicit val writeDietNutritionPreview = Json.writes[DietNutritionPreview]
  implicit val readDietNutritionPreview = Json.reads[DietNutritionPreview]
  implicit val writeMenu = Json.writes[Menu]
  implicit val readMenu = Json.reads[Menu]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from dietmenu where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from dietmenu where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withTransaction {
    implicit c =>
      try {
        SQL(s"update dietmenu set status=0 where uid={id} and school_id={kg} and status=1")
          .on(
            'kg -> kg.toString,
            'id -> id
          ).executeUpdate()

        SQL(s"update dietstructure set status=0 where menu_id={id} and school_id={kg} and status=1")
          .on(
            'kg -> kg.toString,
            'id -> id
          ).executeUpdate()
        c.commit()
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }

  def previewNutritionUnit(kg: Long, id: Long): List[DietNutritionPreview] = DB.withConnection {
    implicit c =>
      SQL("select * from dietstructure where menu_id={id} and status=1 and school_id={kg}")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simpleUnit *)
  }

  def cleanNutritionUnits(kg: Long, base: Long) = DB.withTransaction {
    implicit c =>
      SQL(s"update dietstructure set status=0 where school_id={kg} and status=1 and menu_id={base}")
        .on(
          'kg -> kg.toString,
          'base -> base
        ).executeUpdate()
  }

  def simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[Option[String]]("name") ~
      get[Option[String]]("weight") ~
      get[Option[String]]("recipe") ~
      get[Option[String]]("property") ~
      get[Option[String]]("tips") ~
      get[Int]("store_type") ~
      get[Option[Long]]("food_type_id") ~
      get[Option[Long]]("former_id") ~
      get[Option[Int]]("arrange_type") map {
      case id ~ kg ~ name ~ weight ~ recipe ~ property ~ tips ~ store ~ food ~ formerId ~ typ =>
        Menu(Some(id), name, weight, typ, previewNutritionUnit(kg.toLong, id), recipe.getOrElse(""),
          property.getOrElse(""), tips.getOrElse(""), food, store, formerId)
    }
  }

  def simpleUnit = {
    get[Long]("uid") ~
      get[Long]("menu_id") ~
      get[Long]("nutrition_id") ~
      get[Option[String]]("weight") map {
      case uid ~ menu ~ nutrition ~ weight =>
        DietNutritionPreview(Some(uid), nutrition, weight.getOrElse(""))
    }
  }
}
