package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.api.Play.current
import play.api.db.DB
import play.api.libs.json.Json

case class Vitamin(va: Option[String], vc: Option[String], vd: Option[String], ve: Option[String], vb1: Option[String],
                   vb2: Option[String], vb3: Option[String], vb5: Option[String], vb6: Option[String], vb9: Option[String], vb12: Option[String], vh: Option[String])

case class Metal(ca: Option[String], k: Option[String], na: Option[String], mg: Option[String], fe: Option[String],
                 zn: Option[String], se: Option[String], cu: Option[String], mn: Option[String], cr: Option[String], mo: Option[String])

case class DietNutrition(id: Option[Long], food_type_id: Option[Long], name: Option[String], alias: Option[String], weight: Option[String], available_weight: Option[String],
                         calorie: Option[String], protein: Option[String], fat: Option[String], carbohydrate: Option[String], ash: Option[String],
                         carotine: Option[String], fibre: Option[String], cholesterol: Option[String], vitamin: Option[Vitamin]
                         , p: Option[String], metal: Option[Metal], store_type: Int = 1, former_id: Option[Long] = None) {
  def update(kg: Long): Option[DietNutrition] = DB.withConnection {
    implicit c =>
      SQL("update dietnutrition set food_type_id={food_type_id}, name={name}, alias={alias}, weight={weight}, " +
        "available_weight={available_weight}, calorie={calorie}, protein={protein}, fat={fat}, carbohydrate={carbohydrate}, " +
        "ash={ash}, carotine={carotine}, fibre={fibre}, cholesterol={cholesterol}, vitamin_va={vitamin_va}, vitamin_vc={vitamin_vc}, " +
        "vitamin_vd={vitamin_vd}, vitamin_ve={vitamin_ve}, vitamin_vb1={vitamin_vb1}, vitamin_vb2={vitamin_vb2}, " +
        "vitamin_vb3={vitamin_vb3}, vitamin_vb5={vitamin_vb5}, vitamin_vb6={vitamin_vb6}, vitamin_vb9={vitamin_vb9}, " +
        "vitamin_vb12={vitamin_vb12}, vitamin_vh={vitamin_vh}, p={p}, metal_ca={metal_ca}, metal_k={metal_k}, " +
        "metal_na={metal_na}, metal_mg={metal_mg}, metal_fe={metal_fe}, metal_zn={metal_zn}, metal_se={metal_se}, " +
        "metal_cu={metal_cu}, metal_mn={metal_mn}, metal_cr={metal_cr}, metal_mo={metal_mo}, " +
        "store_type={store_type}, former_id={former_id} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'food_type_id -> food_type_id,
          'name -> name,
          'alias -> alias,
          'weight -> weight,
          'available_weight -> available_weight,
          'calorie -> calorie,
          'protein -> protein,
          'fat -> fat,
          'carbohydrate -> carbohydrate,
          'ash -> ash,
          'carotine -> carotine,
          'fibre -> fibre,
          'cholesterol -> cholesterol,
          'vitamin_va -> vitamin.map(_.va),
          'vitamin_vc -> vitamin.map(_.vc),
          'vitamin_vd -> vitamin.map(_.vd),
          'vitamin_ve -> vitamin.map(_.ve),
          'vitamin_vb1 -> vitamin.map(_.vb1),
          'vitamin_vb2 -> vitamin.map(_.vb2),
          'vitamin_vb3 -> vitamin.map(_.vb3),
          'vitamin_vb5 -> vitamin.map(_.vb5),
          'vitamin_vb6 -> vitamin.map(_.vb6),
          'vitamin_vb9 -> vitamin.map(_.vb9),
          'vitamin_vb12 -> vitamin.map(_.vb12),
          'vitamin_vh -> vitamin.map(_.vh),
          'p -> p,
          'metal_ca -> metal.map(_.ca),
          'metal_k -> metal.map(_.k),
          'metal_na -> metal.map(_.na),
          'metal_mg -> metal.map(_.mg),
          'metal_fe -> metal.map(_.fe),
          'metal_zn -> metal.map(_.zn),
          'metal_se -> metal.map(_.se),
          'metal_cu -> metal.map(_.cu),
          'metal_mn -> metal.map(_.mn),
          'metal_cr -> metal.map(_.cr),
          'metal_mo -> metal.map(_.mo),
          'store_type -> store_type,
          'former_id -> former_id,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (DietNutrition.show(kg, _))
  }

  def create(kg: Long): Option[DietNutrition] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into dietnutrition (school_id, food_type_id, name, alias, weight, " +
        "available_weight, calorie, protein, fat, carbohydrate, ash, carotine, fibre, cholesterol, vitamin_va, " +
        "vitamin_vc, vitamin_vd, vitamin_ve, vitamin_vb1, vitamin_vb2, vitamin_vb3, vitamin_vb5, vitamin_vb6, " +
        "vitamin_vb9, vitamin_vb12, vitamin_vh, p, metal_ca, metal_k, metal_na, metal_mg, metal_fe, metal_zn, " +
        "metal_se, metal_cu, metal_mn, metal_cr, metal_mo, store_type, former_id, created_at) values (" +
        "{school_id}, {name}, {alias}, {weight}, {available_weight}, {calorie}, {protein}, {fat}, {carbohydrate}, {ash}, " +
        "{carotine}, {fibre}, {cholesterol}, {vitamin_va}, {vitamin_vc}, {vitamin_vd}, {vitamin_ve}, {vitamin_vb1}, {vitamin_vb2}, {vitamin_vb3}, " +
        "{vitamin_vb5}, {vitamin_vb6}, {vitamin_vb9}, {vitamin_vb12}, {vitamin_vh}, {p}, {metal_ca}, {metal_k}, {metal_na}, {metal_mg}, " +
        "{metal_fe}, {metal_zn}, {metal_se}, {metal_cu}, {metal_mn}, {metal_cr}, {metal_mo}, {store_type}, " +
        "{former_id}, {time})")
        .on(
          'school_id -> kg,
          'food_type_id -> food_type_id,
          'name -> name,
          'alias -> alias,
          'weight -> weight,
          'available_weight -> available_weight,
          'calorie -> calorie,
          'protein -> protein,
          'fat -> fat,
          'carbohydrate -> carbohydrate,
          'ash -> ash,
          'carotine -> carotine,
          'fibre -> fibre,
          'cholesterol -> cholesterol,
          'vitamin_va -> vitamin.map(_.va),
          'vitamin_vc -> vitamin.map(_.vc),
          'vitamin_vd -> vitamin.map(_.vd),
          'vitamin_ve -> vitamin.map(_.ve),
          'vitamin_vb1 -> vitamin.map(_.vb1),
          'vitamin_vb2 -> vitamin.map(_.vb2),
          'vitamin_vb3 -> vitamin.map(_.vb3),
          'vitamin_vb5 -> vitamin.map(_.vb5),
          'vitamin_vb6 -> vitamin.map(_.vb6),
          'vitamin_vb9 -> vitamin.map(_.vb9),
          'vitamin_vb12 -> vitamin.map(_.vb12),
          'vitamin_vh -> vitamin.map(_.vh),
          'p -> p,
          'metal_ca -> metal.map(_.ca),
          'metal_k -> metal.map(_.k),
          'metal_na -> metal.map(_.na),
          'metal_mg -> metal.map(_.mg),
          'metal_fe -> metal.map(_.fe),
          'metal_zn -> metal.map(_.zn),
          'metal_se -> metal.map(_.se),
          'metal_cu -> metal.map(_.cu),
          'metal_mn -> metal.map(_.mn),
          'metal_cr -> metal.map(_.cr),
          'metal_mo -> metal.map(_.mo),
          'store_type -> store_type,
          'former_id -> former_id,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (DietNutrition.show(kg, _))
  }
}


object DietNutrition {
  implicit val writeVitamin = Json.writes[Vitamin]
  implicit val readVitamin = Json.reads[Vitamin]
  implicit val writeMetal = Json.writes[Metal]
  implicit val readMetal = Json.reads[Metal]
  implicit val writeDietNutrition = Json.writes[DietNutrition]
  implicit val readDietNutrition = Json.reads[DietNutrition]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from dietnutrition where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from dietnutrition where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update dietnutrition set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  def simple = {
    get[Long]("uid") ~
      get[Option[String]]("school_id") ~
      get[Option[Long]]("food_type_id") ~
      get[Option[String]]("name") ~
      get[Option[String]]("alias") ~
      get[Option[String]]("weight") ~
      get[Option[String]]("available_weight") ~
      get[Option[String]]("calorie") ~
      get[Option[String]]("protein") ~
      get[Option[String]]("fat") ~
      get[Option[String]]("carbohydrate") ~
      get[Option[String]]("ash") ~
      get[Option[String]]("carotine") ~
      get[Option[String]]("fibre") ~
      get[Option[String]]("cholesterol") ~
      get[Option[String]]("vitamin_va") ~
      get[Option[String]]("vitamin_vc") ~
      get[Option[String]]("vitamin_vd") ~
      get[Option[String]]("vitamin_ve") ~
      get[Option[String]]("vitamin_vb1") ~
      get[Option[String]]("vitamin_vb2") ~
      get[Option[String]]("vitamin_vb3") ~
      get[Option[String]]("vitamin_vb5") ~
      get[Option[String]]("vitamin_vb6") ~
      get[Option[String]]("vitamin_vb9") ~
      get[Option[String]]("vitamin_vb12") ~
      get[Option[String]]("vitamin_vh") ~
      get[Option[String]]("p") ~
      get[Option[String]]("metal_ca") ~
      get[Option[String]]("metal_k") ~
      get[Option[String]]("metal_na") ~
      get[Option[String]]("metal_mg") ~
      get[Option[String]]("metal_fe") ~
      get[Option[String]]("metal_zn") ~
      get[Option[String]]("metal_se") ~
      get[Option[String]]("metal_cu") ~
      get[Option[String]]("metal_mn") ~
      get[Option[String]]("metal_cr") ~
      get[Option[String]]("metal_mo") ~
      get[Option[Int]]("store_type") ~
      get[Option[Long]]("former_id") map {
      case id ~ school_id ~ food_type_id ~ name ~ alias ~ weight ~ available_weight ~ calorie ~ protein ~ fat ~
        carbohydrate ~ ash ~ carotine ~ fibre ~ cholesterol ~ vitamin_va ~ vitamin_vc ~ vitamin_vd ~ vitamin_ve ~
        vitamin_vb1 ~ vitamin_vb2 ~ vitamin_vb3 ~ vitamin_vb5 ~ vitamin_vb6 ~ vitamin_vb9 ~ vitamin_vb12 ~ vitamin_vh ~
        p ~ metal_ca ~ metal_k ~ metal_na ~ metal_mg ~ metal_fe ~ metal_zn ~ metal_se ~ metal_cu ~ metal_mn ~
        metal_cr ~ metal_mo ~ store_type ~ former_id =>
        DietNutrition(Some(id), food_type_id, name, alias, weight, available_weight, calorie, protein, fat,
          carbohydrate, ash, carotine, fibre, cholesterol, Some(Vitamin(vitamin_va, vitamin_vc, vitamin_vd, vitamin_ve,
            vitamin_vb1, vitamin_vb2, vitamin_vb3, vitamin_vb5, vitamin_vb6, vitamin_vb9, vitamin_vb12, vitamin_vh)), p,
          Some(Metal(metal_ca, metal_k, metal_na, metal_mg, metal_fe, metal_zn, metal_se, metal_cu, metal_mn, metal_cr,
            metal_mo)), store_type.getOrElse(1), former_id)
    }
  }
}