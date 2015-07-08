package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class DietArrangement(id: Option[Long], menu_id: Option[Long], menu_name: Option[String], arrange_date: Option[Long],
                           arrange_type: Option[Int], master_id: Option[Long], grade_id: Option[Long], weight: Option[String], updated_at: Option[Long]) {
  def exists(id: Long) = DB.withConnection {
    implicit c =>
      SQL("select count(1) from dietarrangement where uid={id}")
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


  def update(kg: Long, base: Long): Option[DietArrangement] = DB.withConnection {
    implicit c =>
      SQL("update dietarrangement set school_id={school_id}, menu_id={menu_id}, menu_name={menu_name}, " +
        "arrange_type={arrange_type}, master_id={master_id}, grade_id={grade_id}, weight={weight}, group_id={group_id}, " +
        "updated_at={time}, arrange_date={arrange_date} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'group_id -> base,
          'menu_id -> menu_id,
          'menu_name -> menu_name,
          'arrange_type -> arrange_type,
          'arrange_date -> arrange_date,
          'master_id -> master_id,
          'grade_id -> grade_id,
          'weight -> weight,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      id flatMap (DietArrangement.show(kg, _))
  }

  def create(kg: Long, base: Long): Option[DietArrangement] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into dietarrangement (school_id, group_id, menu_id, menu_name, arrange_type, master_id, grade_id, weight, updated_at, arrange_date) values (" +
        "{school_id}, {group_id}, {menu_id}, {menu_name}, {arrange_type}, {master_id}, {grade_id}, {weight}, {time}, {arrange_date})")
        .on(
          'school_id -> kg,
          'group_id -> base,
          'menu_id -> menu_id,
          'menu_name -> menu_name,
          'arrange_type -> arrange_type,
          'arrange_date -> arrange_date,
          'master_id -> master_id,
          'grade_id -> grade_id,
          'weight -> weight,
          'time -> System.currentTimeMillis
        ).executeInsert()
      insert flatMap (DietArrangement.show(kg, _))
  }
}

case class ArrangementGroup(id: Option[Long], name: Option[String], items: Option[List[DietArrangement]]) {
  def update(kg: Long): Option[ArrangementGroup] = DB.withTransaction {
    implicit c =>
      try {
        SQL("update dietarrangementgroup set school_id={school_id}, name={name}, updated_at={time} where school_id={school_id} and uid={id}")
          .on(
            'id -> id,
            'school_id -> kg,
            'name -> name,
            'time -> System.currentTimeMillis
          ).executeUpdate()
        id foreach {
          case i =>
            ArrangementGroup.cleanArrangement(kg, i)
            items foreach {
              _ foreach {
                _.handle(kg, i)
              }
            }
        }
        c.commit()
        ArrangementGroup.show(kg, id.getOrElse(0))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }

  def create(kg: Long): Option[ArrangementGroup] = DB.withTransaction {
    implicit c =>
      try {
        val insert: Option[Long] = SQL("insert into dietarrangementgroup (school_id, name, updated_at) values (" +
          "{school_id}, {name}, {time})")
          .on(
            'school_id -> kg,
            'name -> name,
            'time -> System.currentTimeMillis
          ).executeInsert()
        insert foreach {
          case i =>
            ArrangementGroup.cleanArrangement(kg, i)
            items foreach {
              _ foreach {
                _.handle(kg, i)
              }
            }
        }
        c.commit()
        insert flatMap (ArrangementGroup.show(kg, _))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }
}

object ArrangementGroup {
  implicit val writeStocking = Json.writes[DietArrangement]
  implicit val readStocking = Json.reads[DietArrangement]
  implicit val writeArrangementGroup = Json.writes[ArrangementGroup]
  implicit val readArrangementGroup = Json.reads[ArrangementGroup]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from dietarrangementgroup where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def arrangementIndex(kg: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from dietarrangement where school_id={kg} and status=1 and group_id={base}")
        .on(
          'kg -> kg.toString,
          'base -> base
        ).as(simpleArrangementTemplate *)
  }

  def cleanArrangement(kg: Long, base: Long) = DB.withTransaction {
    implicit c =>
      SQL(s"update dietarrangement set status=0 where school_id={kg} and status=1 and group_id={base}")
        .on(
          'kg -> kg.toString,
          'base -> base
        ).executeUpdate()
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from dietarrangementgroup where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update dietarrangementgroup set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
      cleanArrangement(kg, id)
  }

  def simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[Option[String]]("name") map {
      case id ~ kg ~ name =>
        ArrangementGroup(Some(id), name, Some(arrangementIndex(kg.toLong, id)))
    }
  }

  def simpleArrangementTemplate = {
    get[Long]("uid") ~
      get[Option[Long]]("menu_id") ~
      get[Option[String]]("menu_name") ~
      get[Option[Int]]("arrange_type") ~
      get[Option[Long]]("arrange_date") ~
      get[Option[Long]]("master_id") ~
      get[Option[Long]]("grade_id") ~
      get[Option[String]]("weight") ~
      get[Option[Long]]("updated_at") map {
      case id ~ menuId ~ name ~ typ ~ date ~ master ~ grade ~ weight ~ time =>
        DietArrangement(Some(id), menuId, name, date, typ, master, grade, weight, time)
    }
  }
}

object DietArrangement {
  implicit val writeStocking = Json.writes[DietArrangement]
  implicit val readStocking = Json.reads[DietArrangement]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from dietarrangement where school_id={kg} and group_id=0 and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to
        ).as(simple *)
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from dietarrangement where school_id={kg} and group_id=0 and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update dietarrangement set status=0 where uid={id} and group_id=0 and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  def simple = {
    get[Long]("uid") ~
      get[Option[Long]]("menu_id") ~
      get[Option[String]]("menu_name") ~
      get[Option[Int]]("arrange_type") ~
      get[Option[Long]]("arrange_date") ~
      get[Option[Long]]("master_id") ~
      get[Option[Long]]("grade_id") ~
      get[Option[String]]("weight") ~
      get[Option[Long]]("updated_at") map {
      case id ~ menuId ~ name ~ typ ~ date ~ master ~ grade ~ weight ~ time =>
        DietArrangement(Some(id), menuId, name, date, typ, master, grade, weight, time)
    }
  }
}
