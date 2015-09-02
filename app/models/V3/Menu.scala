package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class DietNutritionPreview(id: Option[Long], nutrition_id: Long, weight: String)

case class Menu(id: Option[Long], name: Option[String], weight: Option[String], arrange_type: Option[Int],
                nutrition_units: List[DietNutritionPreview], recipe: String, property: String, tips: String, food_type_id: Long,
                store_type: Int = 1, former_id: Option[Long] = None) {
  def exists(id: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from dietmenu where uid={id}")
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


  def update(kg: Long): Option[Menu] = DB.withConnection {
    implicit c =>
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
      id flatMap (Menu.show(kg, _))
  }

  def create(kg: Long): Option[Menu] = DB.withConnection {
    implicit c =>
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
      insert flatMap (Menu.show(kg, _))
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

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update dietmenu set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  def previewNutritionUnit(id: Long): List[DietNutritionPreview] = DB.withConnection {
    implicit c =>
      List(DietNutritionPreview(Some(1), id, "123g"), DietNutritionPreview(Some(2), id, "1t"), DietNutritionPreview(Some(3), id, "312g"))
  }

  def simple = {
    get[Long]("uid") ~
      get[Option[String]]("name") ~
      get[Option[String]]("weight") ~
      get[Int]("store_type") ~
      get[Option[Int]]("arrange_type") map {
      case id ~ name ~ weight ~ store ~ typ =>
        Menu(Some(id), name, weight, typ, previewNutritionUnit(id), "recipe", "property", "tips", 1, store, Some(1))
    }
  }

  def simpleUnit(id: Long) = {
    get[Long]("uid") ~
      get[Option[String]]("weight") map {
      case uid ~ weight =>
        DietNutritionPreview(Some(uid), id, weight.getOrElse(""))
    }
  }
}
