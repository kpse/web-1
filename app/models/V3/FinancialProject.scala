package models.V3

import anorm.SqlParser._
import anorm._
import models.helper.RangerHelper
import play.Logger
import play.api.db.DB
import play.api.libs.json.Json
import play.api.Play.current

case class FinancialProject(id: Option[Long], parent_id: Option[Long], name: Option[String], short_name: Option[String],
                            total: Option[String], memo: Option[String]) {
  def exists(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from financialproject where uid={id}")
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


  def update(kg: Long): Option[FinancialProject] = DB.withConnection {
    implicit c =>
      SQL("update financialproject set parent_id={parent_id}, name={name}, short_name={short_name}, " +
        "total={total}, memo={memo}, updated_at={time} where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'name -> name,
          'short_name -> short_name,
          'parent_id -> parent_id,
          'total -> total,
          'memo -> memo,
          'time -> System.currentTimeMillis
        ).executeUpdate()
      FinancialProject.show(kg, id.getOrElse(0))
  }

  def create(kg: Long): Option[FinancialProject] = DB.withConnection {
    implicit c =>
      val insert: Option[Long] = SQL("insert into financialproject (school_id, parent_id, name, short_name, total, memo, updated_at) values (" +
        "{school_id}, {parent_id}, {name}, {short_name}, {total}, {memo}, {time})")
        .on(
          'school_id -> kg,
          'parent_id -> parent_id,
          'name -> name,
          'short_name -> short_name,
          'total -> total,
          'memo -> memo,
          'time -> System.currentTimeMillis
        ).executeInsert()
      FinancialProject.show(kg, insert.getOrElse(0))
  }
}

case class FinancialProjectGroup(id: Option[Long], name: Option[String], short_name: Option[String], projects: Option[List[GroupedFinancialProject]]) {
  def exists(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from financialprojectgroup where uid={id}")
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


  def update(kg: Long): Option[FinancialProjectGroup] = DB.withTransaction {
    implicit c =>
      try {
        SQL("update financialprojectgroup set name={name}, short_name={short_name}, updated_at={time} where school_id={school_id} and uid={id}")
          .on(
            'id -> id,
            'school_id -> kg,
            'name -> name,
            'short_name -> short_name,
            'time -> System.currentTimeMillis
          ).executeUpdate()
        id foreach {
          case i =>
            FinancialProjectGroup.cleanRelation(kg, i)
            projects foreach {
              _ foreach {
                _.handle(kg, i)
              }
            }
        }
        c.commit()
        FinancialProjectGroup.show(kg, id.getOrElse(0))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }

  def create(kg: Long): Option[FinancialProjectGroup] = DB.withTransaction {
    implicit c =>
      try {
        val insert: Option[Long] = SQL("insert into financialprojectgroup (school_id, name, short_name, updated_at) values (" +
          "{school_id}, {name}, {short_name}, {time})")
          .on(
            'school_id -> kg,
            'name -> name,
            'short_name -> short_name,
            'time -> System.currentTimeMillis
          ).executeInsert()
        id foreach {
          case i =>
            FinancialProjectGroup.cleanRelation(kg, i)
            projects foreach {
              _ foreach {
                _.handle(kg, i)
              }
            }
        }
        c.commit()
        FinancialProjectGroup.show(kg, insert.getOrElse(0))
      }
      catch {
        case t: Throwable =>
          Logger.info(t.getLocalizedMessage)
          c.rollback()
          None
      }
  }
}






case class GroupedFinancialProject(id: Option[Long], project_id: Option[Long]) {
  def exists(id: Long) = DB.withTransaction {
    implicit c =>
      SQL("select count(1) from groupedfinancialproject where uid={id}")
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
      SQL("update groupedfinancialproject set project_id={project}, group_id={group}, updated_at={time} " +
        "where school_id={school_id} and uid={id}")
        .on(
          'id -> id,
          'school_id -> kg,
          'project -> project_id,
          'group -> base,
          'time -> System.currentTimeMillis
        ).executeUpdate()
  }

  def create(kg: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL("insert into groupedfinancialproject (school_id, project_id, group_id, updated_at) values (" +
        "{school_id}, {project}, {group}, {time})")
        .on(
          'school_id -> kg,
          'project -> project_id,
          'group -> base,
          'time -> System.currentTimeMillis
        ).executeInsert()
  }
}

object FinancialProject {
  implicit val writeFinancialProject = Json.writes[FinancialProject]
  implicit val readFinancialProject = Json.reads[FinancialProject]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from financialproject where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to,
          'most -> most
        ).as(simple *)
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from groupedfinancialproject where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update financialproject set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
  }

  val simple = {
    get[Long]("uid") ~
      get[Option[Long]]("parent_id") ~
      get[Option[String]]("name") ~
      get[Option[String]]("short_name") ~
      get[Option[String]]("total") ~
      get[Option[String]]("memo") map {
      case id ~ parent_id ~ name ~ short_name ~ total ~ memo =>
        FinancialProject(Some(id), parent_id, name, short_name, total, memo)
    }
  }


}

object FinancialProjectGroup {
  implicit val readGroupedFinancialProject = Json.reads[GroupedFinancialProject]
  implicit val writeGroupedFinancialProject = Json.writes[GroupedFinancialProject]

  implicit val writeFinancialProjectGroup = Json.writes[FinancialProjectGroup]
  implicit val readFinancialProjectGroup = Json.reads[FinancialProjectGroup]

  def index(kg: Long, from: Option[Long], to: Option[Long], most: Option[Int]) = DB.withConnection {
    implicit c =>
      SQL(s"select * from financialprojectgroup where school_id={kg} and status=1 ${RangerHelper.generateSpan(from, to, most)}")
        .on(
          'kg -> kg.toString,
          'from -> from,
          'to -> to,
          'most -> most
        ).as(simple *)
  }

  def show(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from financialprojectgroup where school_id={kg} and uid={id} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).as(simple singleOpt)
  }

  def indexInGroup(kg: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL(s"select * from groupedfinancialproject where school_id={kg} and status=1 and group_id={base}")
        .on(
          'kg -> kg.toString,
          'base -> base
        ).as(simpleRelation *)
  }

  def cleanRelation(kg: Long, base: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update groupedfinancialproject set status=0 where school_id={kg} and status=1 and group_id={base}")
        .on(
          'kg -> kg.toString,
          'base -> base
        ).execute()
  }

  def deleteById(kg: Long, id: Long) = DB.withConnection {
    implicit c =>
      SQL(s"update financialprojectgroup set status=0 where uid={id} and school_id={kg} and status=1")
        .on(
          'kg -> kg.toString,
          'id -> id
        ).executeUpdate()
      cleanRelation(kg, id)
  }

  val simple = {
    get[Long]("uid") ~
      get[String]("school_id") ~
      get[Option[String]]("name") ~
      get[Option[String]]("short_name") map {
      case id ~ kg ~ name ~ short_name =>
        FinancialProjectGroup(Some(id), name, short_name, Some(indexInGroup(kg.toLong, id)))
    }
  }

  val simpleRelation = {
    get[Long]("uid") ~
      get[Option[Long]]("project_id") map {
      case id ~ project =>
        GroupedFinancialProject(Some(id), project)
    }
  }
}

